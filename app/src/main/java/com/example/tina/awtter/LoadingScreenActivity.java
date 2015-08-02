package com.example.tina.awtter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class LoadingScreenActivity extends Activity {
    LoadingSurfaceView loadingSurfaceView;

    //Introduce an delay
    private final int WAIT_TIME = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        System.out.println("LoadingScreenActivity screen started");
        setContentView(R.layout.loading_screen);

        //loadingSurfaceView = new LoadingSurfaceView(this);
        //setContentView(loadingSurfaceView);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //Simulating a long running task
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

	  /* Create an Intent that will start the ProfileData-Activity. */
                Intent mainIntent = new Intent(LoadingScreenActivity.this, MainActivity.class);
                LoadingScreenActivity.this.startActivity(mainIntent);
                LoadingScreenActivity.this.finish();
            }
        }, WAIT_TIME);
    }
}

class LoadingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public LoadingSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {

    }

    @Override
    public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
