package com.example.tina.awtter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by richellevital on 8/30/15.
 */
public class NewCommentActivity extends Activity {

    private static final String TAG_ID = "__id";
    private static final String TAG_NAME = "__name";
    private static final String TAG_COMMENT = "__comment";
    private static final String TAG_SUCCESS = "success";

    String id;
    String name = "Richelle";
    String comment;
    DatabaseHandler databaseHandler;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_comment);

        Intent intent = getIntent();
        id = intent.getStringExtra("animalid");

        final TextView nameText = (TextView) findViewById(R.id.user_comment);
        nameText.setText(name);

        final TextView commentText = (TextView) findViewById(R.id.newcommenttext);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comment = commentText.getText().toString();

                new LoadComments().execute();

               finish();
            }

        });

        databaseHandler = new DatabaseHandler(this);
        //name = databaseHandler.getName();

        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        LinearLayout ll = (LinearLayout) findViewById(R.id.newcommentlayout);
        TextView tv = (TextView) findViewById(R.id.newcommenttext);
        ll.setOnTouchListener(gestureListener);
        tv.setOnTouchListener(gestureListener);
    }

    class LoadComments extends AsyncTask<String, String, String> {

        private static final String url_all_products = "http://76.244.35.83/add_comment.php";

        JSONParser jParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jParser = new JSONParser();

        }

        /**
         * getting All products from url
         */
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {
            boolean isPortrait;

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_ID, id));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_COMMENT, comment));

            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);

            // Checking for SUCCESS TAG
            int success = 0;
            try {
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success == 1) {

            } else {

            }

            return null;
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            /*
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }*/

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                finish();
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                return false; // Bottom to top
            }
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
