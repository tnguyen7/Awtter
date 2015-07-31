package com.example.tina.awtter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

public class UploadImage extends AsyncTask<String, String, String> {
    InputStream inputStream;
    Bitmap bitmap;
    int bitmapWidth, bitmapHeight;
    double scale;
    Uri uri;
    Context context;
    boolean portrait;
    DatabaseHandler databaseHandler;

    public UploadImage (Context context, Uri imageUri) {
        this.context = context;
        this.uri = imageUri;
        databaseHandler = new DatabaseHandler(context);
    }

    @SuppressWarnings( "deprecation" )
    @Override
    protected String doInBackground(String... args) {


        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
            InputStream stream = context.getContentResolver().openInputStream(
                    uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.v("SCALE", "HEIGHT = " + height);
        Log.v("SCALE", "WIDTH = " + width);

        // Get the correct orientaiton uploaded
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = ((Activity)context).managedQuery(uri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        Log.v("SCALE", "ORIENTATION = " + orientation);


        bitmapHeight = bitmap.getHeight();
        bitmapWidth  = bitmap.getWidth();
        Log.v("SCALE", "BITMAPHEIGHT = " + bitmapHeight);
        Log.v("SCALE", "BITMAPWIDTH = " + bitmapWidth);

        if (orientation == 90 || orientation == 270) {
            //actually landscape
            if (bitmapHeight > bitmapWidth) {
                portrait = false;
                scale = ((double) width)/bitmapWidth;

                Log.v("SCALE", "SCALE = " + scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, (int) (scale * bitmapHeight), true);
            } else {
                portrait = true;
                scale = ((double) height)/bitmapHeight;

                Log.v("SCALE", "SCALE = " + scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (scale * bitmapWidth), height, true);
            }
        } else {
            if (bitmapHeight < bitmapWidth) {
                portrait = false;
                scale = ((double) width)/bitmapWidth;

                Log.v("SCALE", "SCALE = " + scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, (int) (scale * bitmapHeight), true);
            } else {
                portrait = true;
                scale = ((double) height)/bitmapHeight;

                Log.v("SCALE", "SCALE = " + scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (scale * bitmapWidth), height, true);
            }
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeBytes(byte_arr);
        final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("image", image_str));
        nameValuePairs.add(new BasicNameValuePair("portrait", String.valueOf(portrait)));

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://76.244.35.83/upload_image.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    final String the_string_response = convertResponseToString(response);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "Response : " + the_string_response, Toast.LENGTH_LONG).show();
                        }
                    });

                    databaseHandler.createMyPicture(databaseHandler.getLastIDMyPicture(), Integer.valueOf(the_string_response));

                }catch(final Exception e){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    System.out.println("Error in http connection "+e.toString());
                }
            }
        });
        t.start();

        return null;
    }

    @SuppressWarnings( "deprecation" )
    public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        final int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
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

            final String finalRes = res;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Result : " + finalRes, Toast.LENGTH_LONG).show();
                }
            });
            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}