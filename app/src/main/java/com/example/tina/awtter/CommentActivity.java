package com.example.tina.awtter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommentActivity extends Activity {

    private String startPoint = "0";
    private String id;
    private static final String TAG_START_POINT = "start";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "__name";
    private static final String TAG_COMMENT = "__comment";
    private static final String TAG_CREATEDAT = "__createdAt";
    private static final String TAG_COMMENTS = "comments";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_view);

        new LoadComments().execute();

        ListView commentList = (ListView) findViewById(R.id.comment_listview);

        Button createComment = (Button) findViewById(R.id.new_comment_button);
        createComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.new_comment);
            }

        });

    }

    class LoadComments extends AsyncTask<String, String, String> {

        private static final String url_all_products = "http://76.244.35.83/get_comments.php";

        JSONParser jParser;
        JSONArray animals_all = null;

        private HashMap<String, String> map;

        ArrayList<HashMap<String, String>> comments;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jParser = new JSONParser();
            comments = new ArrayList<>();

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
            params.add(new BasicNameValuePair(TAG_START_POINT, startPoint));

            startPoint = String.valueOf(Integer.valueOf(startPoint) + 15);

            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = 0;
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (success == 1) {
                    // products found
                    // Getting Array of Animals
                    animals_all = json.getJSONArray(TAG_COMMENTS);

                    // looping through all animals
                    for (int i = 0; i < animals_all.length(); i++) {
                        JSONObject c = animals_all.getJSONObject(i);

                        // Storing each json item in variable
                        String name = c.getString(TAG_NAME);
                        String comment = c.getString(TAG_COMMENT);
                        String date = c.getString(TAG_CREATEDAT);

                        // creating new HashMap
                        map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value;
                        map.put(TAG_NAME, name);
                        map.put(TAG_COMMENT, comment);
                        map.put(TAG_CREATEDAT, date);


                        // adding HashList to ArrayList
                        comments.add(map);
                    }
                } else {
                    // Error from database
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    protected void onPostExecute(String file_url) {
    }

}
