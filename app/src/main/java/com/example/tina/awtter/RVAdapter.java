package com.example.tina.awtter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by richellevital on 7/19/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "RVAdapter";
    private static final String homeFragment = "homeFragment";
    private static final String favoriteFragment = "myFavoritesFragment";
    private static final String myPicturesFragment = "myPicturesFragment";
    private static final String url = "http://76.244.35.83/media/";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Animal> animals;
    private Context context;
    private String currentFragment;
    private DatabaseHandler databaseHandler;

    // Instance variables
    private static final int padding = 13;
    private static final int padPPP = padding*4;
    private static final int paddingPL = padding*3;
    private static final int paddingL = padding*2;
    private static final double portraitScale = 1.8;
    private int portraitWidth, portraitHeight, landscape1Width, landscape1Height, landscape2Width, landscape2Height;
    private  int widthScreen;

    RVAdapter(List<Animal> animals, Context context, String fragment) {

        this.animals = animals;
        this.context = context;
        this.currentFragment = fragment;

        databaseHandler = new DatabaseHandler(context);

        // Get dimensions of screen and determine dimensions of each picture
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        widthScreen = size.x;

        portraitWidth = (widthScreen - padPPP)/3;
        portraitHeight = (int) ((widthScreen - padPPP)/3 * portraitScale);

        landscape1Width = widthScreen - paddingPL - portraitWidth;
        landscape2Width = widthScreen - paddingL;

        landscape1Height = portraitHeight;
        landscape2Height = portraitHeight;

    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {

        ImageView photo, heart;

        AnimalViewHolder(View itemView) {

            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.photo);

            heart = (ImageView) itemView.findViewById(R.id.heart);


        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {

            super(v);

            progressBar = (ProgressBar)v.findViewById(R.id.progressBar);

        }
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }
    /*
     * method is called when the custom viewholder needs to be initialized
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = null;
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_ITEM) {

            if (currentFragment.equals(homeFragment)) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home, viewGroup, false);
            } else if (currentFragment.equals(favoriteFragment)) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_favorites, viewGroup, false);
            } else if (currentFragment.equals(myPicturesFragment)) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_my_pictures, viewGroup, false);
            }

            vh = new AnimalViewHolder(v);

        } else {

            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.progress_item, viewGroup, false);

            vh = new ProgressViewHolder(v);

        }

        return vh;
    }

    //setting values
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        if(holder instanceof AnimalViewHolder) {

            final int animalid = animals.get(i).id;

            int sizeOrient = animals.get(i).sizeOrient;

            AnimalViewHolder animalViewHolder = (AnimalViewHolder) holder;
            animalViewHolder.heart.setVisibility(View.INVISIBLE);
            final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context, new GestureListener(animalid, animalViewHolder.heart));
            animalViewHolder.photo.requestLayout();

            switch (sizeOrient) {
                case 1:

                    animalViewHolder.photo.getLayoutParams().height = portraitHeight;
                    animalViewHolder.photo.getLayoutParams().width = portraitWidth;

                    Target target = new CustomTarget(animalViewHolder.photo);
                    Picasso.with(context)
                            .load(url + String.valueOf(animalid))
                            .resize(portraitWidth, portraitHeight)
                            .centerCrop()
                            .into(target);
                    animalViewHolder.photo.setTag(target);
                    break;

                case 2:

                    animalViewHolder.photo.getLayoutParams().height = landscape1Height;
                    animalViewHolder.photo.getLayoutParams().width = landscape1Width;

                    Picasso.with(context)
                            .load(url + String.valueOf(animalid))
                            .resize(landscape1Width, landscape1Height)
                            .centerCrop()
                            .into(animalViewHolder.photo);

                    break;

                case 3:
                    animalViewHolder.photo.getLayoutParams().height = landscape2Height;
                    animalViewHolder.photo.getLayoutParams().width = landscape2Width;

                    Picasso.with(context)
                            .load(url + String.valueOf(animalid))
                            .resize(landscape2Width, landscape2Height)
                            .centerCrop()
                            .into(animalViewHolder.photo);

                    break;

            }

            animalViewHolder.photo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            animalViewHolder.photo.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    Log.v(TAG, "there was a long click");
                    if (currentFragment == homeFragment) {

                    } else if (currentFragment == favoriteFragment) {

                    } else if (currentFragment == myPicturesFragment) {

                    }

                    return true;
                }
            });

        } else {

            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return animals.get(position)!=null? VIEW_ITEM: VIEW_PROG;
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

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        ImageView heart;
        int animalid;
        Thread thread;

        public GestureListener(int animalid, ImageView heart) {
            this.animalid = animalid;
            this.heart = heart;

            thread=  new Thread(){
                @Override
                public void run(){
                    try {
                        synchronized(this){
                            wait(700);
                        }
                    }
                    catch(InterruptedException ex){
                    }
                }
            };

            thread.start();
        }

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Intent intent = new Intent(context, FullPicture.class);
            intent.putExtra("animalid", animalid);
            intent.putExtra("fragment", currentFragment);
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation((Activity)context).toBundle());
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            if(databaseHandler.getFavoriteFromAnimalID(animalid) == -1) {
                databaseHandler.createFavorite(databaseHandler.getLastIDMyFavorites(), animalid);
                new IncUpAws(context, String.valueOf(animalid), true).execute();

                Log.v(TAG, "double tap if");

                heart.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.Bounce)
                        .duration(700)
                        .playOn(heart);

/*                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.BounceIn)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);*/

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        heart.setVisibility(View.INVISIBLE);
                    }
                }, 700);


            } else {
                databaseHandler.deleteFavoriteFromAnimalID(animalid);
                new IncUpAws(context, String.valueOf(animalid), false).execute();

                Log.v(TAG, "double tap else");

                heart.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.Bounce)
                        .duration(700)
                        .playOn(heart);

                /*YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.BounceIn)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.RubberBand)
                        .duration(700)
                        .playOn(heart);

                YoYo.with(Techniques.ZoomOut)
                        .duration(700)
                        .playOn(heart);*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        heart.setVisibility(View.INVISIBLE);
                    }
                }, 700);


            }

            return true;
        }
    }
}