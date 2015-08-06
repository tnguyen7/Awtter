package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tina on 7/22/15.
 */
class IncUpAws extends AsyncTask<String, String, String> {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ID = "__id";
    private static final String TAG_INCREMENT = "increment";
    private static final String TAG_DECREMENT = "decrement";

    private JSONParser jParser;

    private String url_upAw = "http://76.244.35.83/upAw.php";

    private String __id;
    private Context context;

    private boolean increment;

    Button button;

    public IncUpAws(Context context, String id, boolean increment, Button button) {
        this.context = context;
        this.__id = id;
        this.increment = increment;
        this.button = button;
    }

    /**
     * Before starting background thread Show Progress Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        jParser = new JSONParser();
    }

    /**
     * getting All products from url
     * */
    @SuppressWarnings( "deprecation" )
    protected String doInBackground(String... args) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_ID, __id));

        if (increment) {
            params.add(new BasicNameValuePair(TAG_INCREMENT, TAG_INCREMENT));
        } else {
            params.add(new BasicNameValuePair(TAG_INCREMENT, TAG_DECREMENT));
        }

        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_upAw, "POST", params);


        // Check your log cat for JSON response
        Log.d("All Products: ", json.toString());

        // Checking for SUCCESS TAG
        int success = 0;
        try {
            success = json.getInt(TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success == 1) {
            // increment successful
            if (button != null) {
                if (increment) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            button.setTextColor(context.getResources().getColor(R.color.accent));

                        }
                    });
                } else {

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            button.setTextColor(context.getResources().getColor(R.color.white));

                        }
                    });

                }
            }
        } else {
            // increment failed
        }

        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
    }

}