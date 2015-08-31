package com.example.tina.awtter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
        name = databaseHandler.getName();
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

}
