package com.example.tina.awtter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FullPicture extends AppCompatActivity{


    private static final String url = "http://76.244.35.83/media/";
    private static final String homeFragment = "homeFragment";
    private static final String favoriteFragment = "myFavoritesFragment";
    private static final String myPicturesFragment = "myPicturesFragment";
    private static final String TAG = "FullPicture";
    private static final String UPAWS = " uPaws";

    private String currentFragment;
    private ImageView imageView, heart;

    private Toolbar toolbarBottom;
    private DatabaseHandler databaseHandler;
    private GestureDetectorCompat mDetector;
    GlobalState gs;
    private TextView tv;

    private int animalid;

    private boolean needToRefresh = false;

    private Context context;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture);



        Intent intent = getIntent();
        animalid = intent.getIntExtra("animalid", -1);
        currentFragment = intent.getStringExtra("fragment");

        imageView = (ImageView) findViewById(R.id.imageView);
        heart = (ImageView) findViewById(R.id.heart);

        heart.setVisibility(View.INVISIBLE);

        context = this;

        Target target = new CustomTarget(imageView);

        Picasso.with(this)
                .load(url+animalid)
                .into(target);
        imageView.setTag(target);

        setUpActionBar();

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        gs = (GlobalState) getApplication();

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

    public void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);

        setUpFavorite();

        setUpComment();

        setUpShare();

    }

    public void setUpFavorite() {
        button = (Button) findViewById(R.id.favoriteButton);

        databaseHandler = new DatabaseHandler(getApplicationContext());

        if(databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setTextColor(getResources().getColor(R.color.accent));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If not in favorites, then increment, otherwise decrement
                if (databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
                    button.setTextColor(getResources().getColor(R.color.accent));
                    databaseHandler.createFavorite(databaseHandler.getLastIDMyFavorites(), animalid);
                    new IncUpAws(context, String.valueOf(animalid), true, button).execute();
                    gs.incUpAws();
                    tv.setText(gs.getUpAws() + UPAWS);
                } else {
                    button.setTextColor(getResources().getColor(R.color.white));
                    databaseHandler.deleteFavoriteFromAnimalID(animalid);
                    new IncUpAws(context, String.valueOf(animalid), false, button).execute();
                    gs.decUpAws();
                    tv.setText(gs.getUpAws() + UPAWS);
                }

                //TODO: find a way to refresh the favorites layout if in favorites (after exiting?)
            }
        });
    }

    public void setUpComment(){};

    public void setUpShare() {};

    @Override
    public boolean onTouchEvent(MotionEvent event){

        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            toggleActionBar();

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

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            needToRefresh = true;

            if (databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
                databaseHandler.createFavorite(databaseHandler.getLastIDMyFavorites(), animalid);
                new IncUpAws(context, String.valueOf(animalid), true, button).execute();

                Log.v(TAG, "double tap if");

                heart.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(heart);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        heart.setVisibility(View.INVISIBLE);
                    }
                }, 700);

                gs.incUpAws();
                tv.setText(gs.getUpAws() + UPAWS);

            } else {
                databaseHandler.deleteFavoriteFromAnimalID(animalid);
                new IncUpAws(context, String.valueOf(animalid), false, button).execute();
                Log.v(TAG, "double tap else");

                heart.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(heart);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        heart.setVisibility(View.INVISIBLE);
                    }
                }, 700);

                gs.decUpAws();
                tv.setText(gs.getUpAws() + UPAWS);

            }

            return true;
        }
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
                toolbarBottom.animate().setDuration(200).translationY(0).setInterpolator(new DecelerateInterpolator()).start();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v(TAG, currentFragment);

        if (currentFragment.equals(homeFragment)) {
            getMenuInflater().inflate(R.menu.menu_full_picture_home, menu);
        } else if (currentFragment.equals(favoriteFragment)) {
            getMenuInflater().inflate(R.menu.menu_full_picture_fav, menu);
        } else if (currentFragment.equals(myPicturesFragment)) {
            getMenuInflater().inflate(R.menu.menu_full_picture_pic, menu);
        }

        tv = new TextView(this);
        tv.setText(gs.getUpAws() + UPAWS);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setPadding(5, 0, 5, 0);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(14);
        menu.add(Menu.NONE, 0, Menu.NONE, "upAws").setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
                                new DeleteAnimal(FullPicture.this, String.valueOf(animalid)).execute();
                                databaseHandler.deleteMyPictureFromAnimalID(animalid);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CheckStartActivity", "onActivityResult and resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Log.v(TAG, "Pass");
        } else {
            Log.v(TAG, "Fail");
        }
    }

    @Override
    public void onBackPressed() {
        if (needToRefresh) {
            GlobalState gs = (GlobalState) ((Activity) context).getApplication();
            gs.refresh("Favorites", this);
        }
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}