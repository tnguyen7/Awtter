package com.example.tina.awtter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;


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

    RVAdapter(List<Animal> animals, Context context, GridLayoutManager glm) {

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


    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;

        AnimalViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView)itemView.findViewById(R.id.photo);
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

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home, viewGroup, false);
        AnimalViewHolder pvh = new AnimalViewHolder(v);

        pvh.photo.setOnClickListener( // and the click is handled
                (View.OnClickListener) new RecyclerClickListener(context, new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // STUB:
                        // The click on the item must be handled

                        Toast.makeText(context, "itemclick: " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, FullPicture.class);
                        intent.putExtra("animalid", animals.get(position).id);
                        context.startActivity(intent);


                    }
                }));


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


