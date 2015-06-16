package com.example.tina.droplets;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import java.util.List;
import java.util.Random;


// Width of screen 1080
// Hieght of screen 1920
// implement drops
// make it so it's faster dialog
//game over dimensions all of the dimensions in fact are killing me
public class MainActivity extends Activity {
    Drops drops;
    boolean inGame = false;
    boolean inHowToPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inGame = false;
    }

    public void startDrops(View view) {
        drops = new Drops(this);
        setContentView(drops);
        inGame = true;
    }

    public void howToPlay(View view) {
        setContentView(R.layout.how_to_play);
        inHowToPlay = true;
    }

    @Override
    public void onBackPressed() {
        if(inGame) {

            Intent intent = new Intent(this, DisplayPopup.class);
            startActivity(intent);
            Drops.isPaused();

        } else if (inHowToPlay) {

            setContentView(R.layout.activity_main);
            inHowToPlay = false;

        } else {

            finish();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Drops.isPaused) {
            Drops.restartTimer();
            Drops.isPaused = false;
        }
    }
}



class Drops extends SurfaceView implements SurfaceHolder.Callback {
    static Context mContext;
    GameThread thread;
    static int screenW; //Device's screen width.
    int screenW13; //1/3 screen width
    int screenW23; //2/3 screen widthint screenW; //Device's screen width.
    int screenH; //Devices's screen height.
    int screenH14; //1/3 screen width
    int screenH24; //2/3 screen width
    int screenH34; //2/3 screen width

    int dropY; //Drop y position.
    int initialX;
    int initialY;
    float x; //Coordinates of user touch
    float umbX, umbY;
    static int score = 0;

    public static boolean isPaused = false;

    static int timeLeft;

    Bitmap origDrop, drop, origUmb, umb;

    public static CountDownTimer timer;

    int[] random = new int[3];
    Random randomGenerator;
    boolean touchedBefore = false;

    Paint mPaint = new Paint();
    static int secUntilFinished;

    public Drops(Context context) {
        super(context);
        mContext = context;
        origDrop = BitmapFactory.decodeResource(getResources(), R.drawable.drop); //Load a droplet image
        origUmb = BitmapFactory.decodeResource(getResources(), R.drawable.umb); //Load a umbrella image

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);

        reset();

    }

    public static void reset() {
        score = 0;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished % 1 == 0) {
                    secUntilFinished = (int) (millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                secUntilFinished = 0;

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View view = inflater.inflate(R.layout.game_over, null);
                builder.setView(view);
                View closeButton = view.findViewById(R.id.quit);
                View retryButton = view.findViewById(R.id.retry);
                TextView scoreText = (TextView) view.findViewById(R.id.score);
                scoreText.setText(String.valueOf(score));

                final AlertDialog finishDialog = builder.create();
                finishDialog.show();

                closeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View clicked) {
                        if (clicked.getId() == R.id.quit) {
                            finishDialog.dismiss();
                            ((Activity) mContext).finish();
                        }
                    }
                });

                retryButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View clicked) {
                        if (clicked.getId() == R.id.retry) {
                            Drops.reset();
                            finishDialog.dismiss();
                        }
                    }
                });


            }
        }.start();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenW13 = screenW / 3;
        screenW23 = screenW13 * 2;
        screenH = h;
        screenH14 = screenH / 4;
        screenH24 = screenH14 * 2;
        screenH34 = screenH14 * 3;

        initialX = screenW13;
        initialY = screenH14;

        umb = Bitmap.createScaledBitmap(origUmb, screenW13 - 40, screenH14 - 60, true);
        drop = Bitmap.createScaledBitmap(origDrop, screenW13 - 40, screenH14 - 40, true);

        randomGenerator = new Random();
        // Generate random location for drops
        for (int index = 0; index < 3; ++index) {
            int randomInt = randomGenerator.nextInt(3);
            random[index] = randomInt;
        }

        umbX = screenW13;
        umbY = screenH34 + 15;


    }

    //***************************************
    //*************  TOUCH  *****************
    //***************************************
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        x = ev.getX();

        if (!touchedBefore) {
            // Position 0
            if (x <= screenW13) {

                if (random[2] == 0) {
                    move();
                }

                //Move umbrella
                umbX = 0;

                // Position 1
            } else if (x > screenW13 && x <= screenW23) {

                if (random[2] == 1) {
                    move();
                }

                //Move umbrella
                umbX = screenW13;

                // Position 2
            } else {

                if (random[2] == 2) {
                    move();
                }

                //Move umbrella
                umbX = screenW23;

            }
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchedBefore = true;
                break;
            }


            case MotionEvent.ACTION_UP: {
                touchedBefore = false;

                break;
            }

        }

        return true;
    }

    // Move each drop and umbrella
    private void move() {

        ++score;

        // Move drops through array
        for (int index = random.length - 2; index >= 0; --index) {
            random[index + 1] = random[index];
        }

        // Add random to first one
        random[0] = randomGenerator.nextInt(3);

    }


    public static void restartTimer() {
            timer.cancel();
            timer = new CountDownTimer(timeLeft*1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished % 1 == 0) {
                        secUntilFinished = (int) (millisUntilFinished / 1000);
                    }
                }

                public void onFinish() {
                    secUntilFinished = 0;

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                    View view = inflater.inflate(R.layout.game_over, null);
                    builder.setView(view);
                    View closeButton = view.findViewById(R.id.quit);
                    View retryButton = view.findViewById(R.id.retry);
                    TextView scoreText = (TextView) view.findViewById(R.id.score);
                    scoreText.setText(String.valueOf(score));

                    final AlertDialog finishDialog = builder.create();
                    finishDialog.show();

                    closeButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View clicked) {
                            if (clicked.getId() == R.id.quit) {
                                finishDialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        }
                    });

                    retryButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View clicked) {
                            if (clicked.getId() == R.id.retry) {
                                Drops.reset();
                                finishDialog.dismiss();
                            }
                        }
                    });
                }
            }.start();
        isPaused = false;
    }

    public static void isPaused() {
        isPaused = true;
        timeLeft = secUntilFinished;
        timer.cancel();
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {

            // prepare a paint
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(5);
            mPaint.setAntiAlias(true);

            // draw a rectangle
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL); //fill the background with blue color
            canvas.drawRect(0, 0, screenW, screenH, mPaint);


            // Place umbrella on canvas
            canvas.drawBitmap(umb, umbX + 10, umbY, null);

            // Place drops on canvas
            for (int index = 0; index < 3; ++index) {
                dropY = (index) * screenH14;

                if (random[index] == 0) {
                    canvas.drawBitmap(drop, 20, dropY + 20, null);
                } else if (random[index] == 1) {
                    canvas.drawBitmap(drop, screenW13 + 20, dropY + 20, null);
                } else if (random[index] == 2) {
                    canvas.drawBitmap(drop, screenW23 + 20, dropY + 20, null);
                }
            }

            mPaint.setTextSize(50);

            // Draw score
            mPaint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(score), 20, 55, mPaint);

            // Draw seconds left
            canvas.drawText(String.valueOf(secUntilFinished) + " sec", screenW - 160, 55, mPaint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }


    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private Drops gameView;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, Drops gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (run) {
                c = null;

                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        //call methods to draw and process next fame
                        gameView.onDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}