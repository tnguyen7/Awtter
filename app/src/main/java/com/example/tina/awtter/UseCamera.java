package com.example.tina.awtter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UseCamera extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @SuppressWarnings( "deprecation" )
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                View view = findViewById(android.R.id.content);
                // Image captured and saved to fileUri specified in the Intent
                Snackbar.make(view, "Image saved to:\n" +
                        fileUri, Snackbar.LENGTH_LONG).show();
                finish();




                Uri uri = data.getData();

                Intent intent = new Intent(this, UploadImage.class);
                intent.putExtra("imageUri", uri);
                startActivity(intent);

            } else if (resultCode == RESULT_CANCELED) {
                finish();
                //for debugging:
                //Toast.makeText(this, "User cancelled the image capture", Toast.LENGTH_LONG).show();
            } else {
                View view = findViewById(android.R.id.content);
                Snackbar.make(view, "Image capture failed", Snackbar.LENGTH_LONG).show();
            }


        }

        /** For teh videoz
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        } **/
    }


    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            Log.d("MyCameraApp", "exists");
            if (! mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
