package com.example.tina.awtter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class FullPicture extends AppCompatActivity{


    ImageView imageView;

    boolean isImageFitToScreen;

    private static final String TAG = "Touch" ;
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF start = new  PointF();
    public static PointF mid = new PointF();

    // We can be in one of these 3 states
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static int mode = NONE;

    float oldDist;

    private static final String url = "http://76.244.35.83/media/";

    ArrayList<Animal> animals;

    Toolbar toolbarBottom;

    DatabaseHandler databaseHandler;

    String currentFragment;

    private static final String homeFragment = "homeFragment";
    private static final String favoriteFragment = "myFavoritesFragment";
    private static final String myPicturesFragment = "myPicturesFragment";

    int animalid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        // have a otter logo heheheehehehehehhe
        //actionBar.setLogo();
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);

        Intent intent = getIntent();
        animalid = intent.getIntExtra("animalid", -1);
        currentFragment = intent.getStringExtra("fragment");


        databaseHandler = new DatabaseHandler(getApplicationContext());

        final Button button = (Button) findViewById(R.id.favoriteButton);

        if(databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setTextColor(getResources().getColor(R.color.accent));
        }

            button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If not in favorites, then increment, otherwise decrement
                if(databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
                    button.setTextColor(getResources().getColor(R.color.accent));
                    databaseHandler.createFavorite(databaseHandler.getLastIDMyFavorites(), animalid);
                    new IncUpAws(getApplicationContext(), String.valueOf(animalid), true).execute();
                } else {
                    button.setTextColor(getResources().getColor(R.color.white));
                    databaseHandler.deleteFavoriteFromAnimalID(animalid);
                    new IncUpAws(getApplicationContext(), String.valueOf(animalid), false).execute();
                }
            }
        });


        imageView = (ImageView) findViewById(R.id.imageView);

        Target target = new CustomTarget(imageView);

        Picasso.with(this)
                .load(url+animalid)
                .into(target);
        imageView.setTag(target);

        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    class CustomTarget implements Target {
        private ImageView imageView;

        public CustomTarget(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageView.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageView.setImageDrawable(placeHolderDrawable);
        }

        @Override
        public boolean equals(Object o) {
            return imageView.equals(o);
        }

        @Override
        public int hashCode() {
            return imageView.hashCode();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleActionBar();
        }
        return true;
    }

    private void toggleActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            if(actionBar.isShowing()) {
                actionBar.hide();
                toolbarBottom.animate().setDuration(250).translationY(toolbarBottom.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            }
            else {
                actionBar.show();
                toolbarBottom.animate().setDuration(250).translationY(0).setInterpolator(new DecelerateInterpolator()).start();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            Log.v("fullpicture", currentFragment);

        if (currentFragment == homeFragment) {
            getMenuInflater().inflate(R.menu.menu_full_picture_home, menu);
        } else if (currentFragment == favoriteFragment) {
            getMenuInflater().inflate(R.menu.menu_full_picture_fav, menu);
        } else if (currentFragment == myPicturesFragment) {
            getMenuInflater().inflate(R.menu.menu_full_picture_pic, menu);
        }
        getMenuInflater().inflate(R.menu.menu_full_picture_pic, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(FullPicture.this)
                        .setTitle("Delete photo")
                        .setMessage("Do you want to delete this photo?")
                        .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteAnimal(getApplicationContext(), String.valueOf(animalid)).execute();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();                            }
                        })
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}