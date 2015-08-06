package com.example.tina.awtter;

import android.app.Activity;
import android.app.Application;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by tina on 8/5/15.
 */
public class GlobalState extends Application
{
    private int index;
    private List<Animal> animals;
    private SwipeRefreshLayout swipeLayoutFavorites, swipeLayoutMyPictures;
    private SwipeRefreshLayout.OnRefreshListener refreshListenerFavorites, refreshListenerMyPictures;
    private View view;

    public void setVariables(List<Animal> animals, int index) {
        this.animals = animals;
        this.index = index;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public void incUpAws() {
        ++animals.get(index).upAws;
    }

    public void decUpAws() {
        --animals.get(index).upAws;
    }

    public int getUpAws() {
        return animals.get(index).upAws;
    }

    public void setRefreshFavorites(SwipeRefreshLayout swipeLayoutFavorites, SwipeRefreshLayout.OnRefreshListener refreshListenerFavorites) {
        this.swipeLayoutFavorites = swipeLayoutFavorites;
        this.refreshListenerFavorites = refreshListenerFavorites;
    }

    public void setRefreshMyPictures(SwipeRefreshLayout swipeLayoutMyPictures, SwipeRefreshLayout.OnRefreshListener refreshListenerMyPictures) {
        this.swipeLayoutMyPictures = swipeLayoutMyPictures;
        this.refreshListenerMyPictures = refreshListenerMyPictures;
    }

    public void refresh(String fragmentName, Activity activity) {

        if (fragmentName.equals("Favorites")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    swipeLayoutFavorites.setRefreshing(true);
                    refreshListenerFavorites.onRefresh();
                }
            });



        } else if (fragmentName.equals("MyPictures")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    swipeLayoutMyPictures.setRefreshing(true);
                    refreshListenerMyPictures.onRefresh();
                }
            });

        }

    }
}