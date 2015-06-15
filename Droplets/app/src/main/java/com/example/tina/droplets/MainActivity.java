package com.example.tina.droplets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;


public class MainActivity extends Activity {
    Drops drops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startDrops(View view) {
        drops = new Drops(this);
        setContentView(drops);
    }
}


class Drops extends SurfaceView implements SurfaceHolder.Callback {
    GameThread thread;
    int screenW; //Device's screen width.
    int screenW13; //1/3 screen width
    int screenW23; //2/3 screen widthint screenW; //Device's screen width.
    int screenH; //Devices's screen height.
    int screenH14; //1/3 screen width
    int screenH24; //2/3 screen width
    int screenH34; //2/3 screen width


    int dropX; //Ball x position.
    int dropY; //Ball y position.
    int initialX;
    int initialY;
    float dY; //Ball vertical speed.
    int dropW, umbW;
    int dropH, umbH;
    float x, y; //Coordinates of user touch
    float umbX, umbY;

    Bitmap origDrop, drop, origUmb, umb;

    int[] random = new int[3];
    Random randomGenerator;

    public Drops(Context context) {
        super(context);
        origDrop = BitmapFactory.decodeResource(getResources(), R.drawable.drop); //Load a droplet image
        origUmb = BitmapFactory.decodeResource(getResources(),R.drawable.umb); //Load a umbrella image

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenW13 = screenW/3;
        screenW23 = screenW13*2;
        screenH = h;
        screenH14 = screenH/4;
        screenH24 = screenH14*2;
        screenH34 = screenH14*3;

        initialX = screenW13;
        initialY = screenH14;

        umb = Bitmap.createScaledBitmap(origUmb, screenW13, screenH14 - 10, true);
        drop = Bitmap.createScaledBitmap(origDrop, screenW13, screenH14 - 10, true);

        // Generate random location for drops
        for (int index = 0; index < 3; ++index) {
            int randomInt = randomGenerator.nextInt(3);
            random[index] = randomInt;
        }

        umbX = screenW13;
        umbY = screenH34;
    }

    //***************************************
    //*************  TOUCH  *****************
    //***************************************
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        x = ev.getX();

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

        return true;
    }

    // Move each drop and umbrella
    private void move() {

        // Move drops through array
        for(int index = random.length - 2; index >= 0; ++index) {
            random[index + 1] = random[index];
        }

        // Add random to first one
        random[0] = randomGenerator.nextInt(3);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Place umbrella on canvas
        canvas.drawBitmap(umb, screenW13, screenH34, null); //Draw the ball by applying the canvas rotated matrix.

        // Place drops on canvas
        for (int index = 0; index < 3; ++index) {
            dropY = (index+1)*screenW13;

            if (random[index] == 0) {
                canvas.drawBitmap(drop, 0, dropY, null);
            } else if (random[index] == 1) {
                canvas.drawBitmap(drop, screenW13, dropY, null);
            } else if (random[index] == 2) {
                canvas.drawBitmap(drop, screenW23, dropY, null);
            }
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


