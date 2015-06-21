package com.example.tina.awtter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingScreenActivity extends Activity {

    //Introduce an delay
    private final int WAIT_TIME = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        System.out.println("LoadingScreenActivity  screen started");
        setContentView(R.layout.loading_screen);

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
                Intent mainIntent = new Intent(LoadingScreenActivity.this,MainActivity.class);
                LoadingScreenActivity.this.startActivity(mainIntent);
                LoadingScreenActivity.this.finish();
            }
        }, WAIT_TIME);
    }
}