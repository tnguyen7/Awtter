package com.example.tina.awtter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by richellevital on 7/19/15.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AnimalViewHolder>{

    List<Animal> animals;
    int widthScreen;
    int heightScreen;
    Context context;

    // Instance variables
    int padding = 13;
    int padPPP = padding*4;
    int paddingPL = padding*3;
    int paddingL = padding*2;

    int portraitWidth, portraitHeight, landscape1Width, landscape1Height, landscape2Width, landscape2Height;
    double portraitScale = 1.8;
    private static final String url = "http://76.244.35.83/media/";

    GridLayoutManager glm;

    private static final String homeFragment = "homeFragment";
    private static final String favoriteFragment = "favoriteFragment";
    private static final String myPicturesFragment = "myPicturesFragment";

    static String currentFragment;

    RVAdapter(List<Animal> animals, Context context, GridLayoutManager glm, String fragment) {

        this.glm = glm;
        this.animals = animals;
        this.context = context;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        widthScreen = size.x;
        heightScreen = size.y;
        Log.v("ADAPT", "WidthScreen = " + widthScreen);
        Log.v("ADAPT", "HeightScreen = " + heightScreen);
        //item in recyclerview
        portraitWidth = (widthScreen - padPPP)/3;
        portraitHeight = (int) ((widthScreen - padPPP)/3 * portraitScale);

        landscape1Width = widthScreen - paddingPL - portraitWidth;
        landscape2Width = widthScreen - paddingL;

        landscape1Height = portraitHeight;
        landscape2Height = portraitHeight;

        Log.v("ADAPT", "PortraitWidth = " + portraitWidth);
        Log.v("ADAPT", "RegLandscapeWidth = " +  landscape1Width);
        Log.v("ADAPT", "BigLandscapeWidth = " + landscape2Width);

        currentFragment = fragment;
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;

        AnimalViewHolder(View itemView) {
            super(itemView);

            photo = (ImageView) itemView.findViewById(R.id.photo);
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
    public AnimalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) { //specifying layout of eachu
        View v = null;
        if (currentFragment == homeFragment) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home, viewGroup, false);
        } else if (currentFragment == favoriteFragment) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_favorites, viewGroup, false);
        } else if (currentFragment == myPicturesFragment) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_my_pictures, viewGroup, false);
        }

        AnimalViewHolder pvh = new AnimalViewHolder(v);

        return pvh;
    }

    //setting values
    @Override
    public void onBindViewHolder(AnimalViewHolder animalViewHolder, int i) {

        int sizeOrient = animals.get(i).sizeOrient;

        animalViewHolder.photo.requestLayout();

        switch (sizeOrient) {
            case 1:

                animalViewHolder.photo.getLayoutParams().height = portraitHeight;
                animalViewHolder.photo.getLayoutParams().width = portraitWidth;

                Picasso.with(context)
                        .load(url+String.valueOf(animals.get(i).id))
                        .resize(portraitWidth, portraitHeight)
                        .centerCrop()
                        .into(animalViewHolder.photo);
                break;

            case 2:

                animalViewHolder.photo.getLayoutParams().height = landscape1Height;
                animalViewHolder.photo.getLayoutParams().width = landscape1Width;

                Picasso.with(context)
                        .load(url+String.valueOf(animals.get(i).id))
                        .resize(landscape1Width, landscape1Height)
                        .centerCrop()
                        .into(animalViewHolder.photo);

                break;

            case 3:
                animalViewHolder.photo.getLayoutParams().height = landscape2Height;
                animalViewHolder.photo.getLayoutParams().width = landscape2Width;

                Picasso.with(context)
                        .load(url+String.valueOf(animals.get(i).id))
                        .resize(landscape2Width, landscape2Height)
                        .centerCrop()
                        .into(animalViewHolder.photo);

                break;

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}


