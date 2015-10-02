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
public class DeleteAnimal extends AsyncTask<String, String, String> {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ID = "__id";

    // Creating JSON Parser object
    JSONParser jParser;

    private String url_delete_animal = "http://awtter.website/delete_animal.php";
    private String __id;
    private Context context;
    private GlobalState gs;


    public DeleteAnimal(Context context, String id) {
        this.context = context;
        this.__id = id;

        gs = (GlobalState) ((Activity) context).getApplication();
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

        // getting JSON string from URL
        JSONObject json = jParser.makeHttpRequest(url_delete_animal, "POST", params);


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
            Snackbar.make(gs.getView(), "Photo deleted", Snackbar.LENGTH_LONG)
                    .show();
            ((Activity)context).finish();
            GlobalState gs = (GlobalState) ((Activity) context).getApplication();
            gs.refresh("MyPictures", (Activity) context);

        } else {
            Snackbar.make(gs.getView(), "Error deleting photo", Snackbar.LENGTH_LONG)
                    .show();
        }

        return null;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(String file_url) {
    }

}
