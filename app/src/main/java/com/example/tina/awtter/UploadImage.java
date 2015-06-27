package com.example.tina.awtter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

public class UploadImage extends Activity {
    InputStream inputStream;
    String res = "";
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.drawer_background);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str;
        image_str = Base64.encodeBytes(byte_arr);
        HashMap<String,String> nameValuePairs = new HashMap<String,String>();

        nameValuePairs.put("image", image_str);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                try{

                    HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://76.244.35.83/upload_image.php").openConnection();
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    final String the_string_response = convertResponseToString(urlConnection);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(UploadImage.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
                        }
                    });

                }catch(final Exception e){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    System.out.println("Error in http connection "+e.toString());
                }
            }
        });
        t.start();
    }

    public String convertResponseToString(HttpURLConnection response) throws IllegalStateException, IOException{

        StringBuffer buffer = new StringBuffer();
       // inputStream = response.getContent();
        final int contentLength = (int) response.getContentLength(); //getting content length…..
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(UploadImage.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(UploadImage.this, "Result : " + res, Toast.LENGTH_LONG).show();
                }
            });
            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
    }
}