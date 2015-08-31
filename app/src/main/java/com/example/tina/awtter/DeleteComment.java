package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

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
public class DeleteComment extends AsyncTask<String, String, String> {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_AUTO = "__auto";

    // Creating JSON Parser object
    JSONParser jParser;

    private String url_delete_animal = "http://76.244.35.83/delete_comment.php";
    private String __auto;


    public DeleteComment(String auto) {
        this.__auto = auto;

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
        params.add(new BasicNameValuePair(TAG_AUTO, __auto));

        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_delete_animal, "POST", params);

        // Check your log cat for JSON response
        Log.d("Response ", json.toString());

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

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
    }

}
