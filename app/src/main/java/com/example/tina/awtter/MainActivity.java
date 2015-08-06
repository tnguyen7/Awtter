package com.example.tina.awtter;

import android.net.Uri;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.socialize.Socialize;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        FavoritesFragment.OnFragmentInteractionListener, MyPicturesFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener {

    private static final String HOME = "Home";
    private static final String FAVORITE = "Favorites";
    private static final String MY_PICTURES = "My Pictures";
    private static final String SETTINGS = "Settings";

    DrawerLayout drawerLayout;
    View content;
    HomeFragment homeFragment;
    FavoritesFragment favoritesFragment;
    MyPicturesFragment myPicturesFragment;
    SettingsFragment settingsFragment;
    AddDialogFragment addFragment;
    Fragment currentFragment;

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Socialize.initAsync(this);
            setContentView(R.layout.activity_main);

            setUpFragments();

            setUpToolbar();

            setUpDrawer();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Socialize.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Socialize.onResume(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Switch to see which item was selected from the action bar
        switch (item.getItemId()) {

            // If hamburger was selected, open the drawer
            case android.R.id.home: {

                drawerLayout.openDrawer(GravityCompat.START);

                return true;
            }
            case (R.id.action_add): {
                //TODO: move this to dialogfragment
                addFragment.show(getSupportFragmentManager(), "Add a Picture");
                return true;
            }
            case (R.id.action_settings): {

                // If settings was selected, ...
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpFragments() {

        currentFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.home));

        if (currentFragment == null) {

            addFragment = new AddDialogFragment();
            homeFragment = new HomeFragment();
            favoritesFragment = new FavoritesFragment();
            myPicturesFragment = new MyPicturesFragment();
            settingsFragment = new SettingsFragment();


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, settingsFragment, getString(R.string.settings))
                    .hide(settingsFragment)
                    .add(R.id.content, myPicturesFragment, getString(R.string.my_pictures))
                    .hide(myPicturesFragment)
                    .add(R.id.content, favoritesFragment, getString(R.string.favorite))
                    .hide(favoritesFragment)
                    .add(R.id.content, homeFragment, getString(R.string.home))
                    .commit();
            currentFragment = homeFragment;

        } else {

            homeFragment = (HomeFragment) currentFragment;
            myPicturesFragment = (MyPicturesFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.my_pictures));
            favoritesFragment = (FavoritesFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.favorite));
            settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.settings));

            getSupportFragmentManager().beginTransaction()
                    .hide(settingsFragment)
                    .hide(myPicturesFragment)
                    .hide(favoritesFragment)
                    .commit();

        }
    }

    private void setUpToolbar() {
        // Enable toolbar as action bar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        // Set up home indicator and enable home as up
        if (actionBar != null) {
            // Assing icon for home
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpDrawer() {

        // Set content view to actual content/FrameLayout ( for snackbar )
        content = findViewById(R.id.content);

        // Initialize Navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Initialize NavigationView
        final NavigationView view = (NavigationView) findViewById(R.id.navigation_view);


        // Set up listener so that when a person clicks on an item
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                title = menuItem.getTitle().toString();

                switch (title) {

                    case (HOME): {

                        switchTo(homeFragment, title);
                        break;

                    }
                    case (FAVORITE): {

                        switchTo(favoritesFragment, title);
                        break;

                    }
                    case (MY_PICTURES): {

                        switchTo(myPicturesFragment, title);
                        break;

                    }
                    case (SETTINGS): {

                        switchTo(settingsFragment, title);
                        break;


                    }

                }
                // Make snackbar on content(FrameLayout) view on teh bottom
                //Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();

                // Make it checked/highlighted when pressed
                menuItem.setChecked(true);

                // Close the drawer
                drawerLayout.closeDrawers();

                return true;
            }

        });

    }

    private void switchTo (Fragment fragment, String name) {

        if (fragment.isVisible())
            return;

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setCustomAnimations(R.anim.slide_in,
                R.anim.slide_out);

        // Make sure the next view is below the current one
        fragment.getView().bringToFront ();
        // And bring the current one to the very top
        currentFragment.getView().bringToFront ();

        // Hide the current fragment
        t.hide (currentFragment);
        t.show(fragment);
        currentFragment = fragment;

        t.addToBackStack(null);
        t.commit();

    }

    public boolean useCamera() {

        Intent intentCamera = new Intent(this, UseCamera.class);
        startActivity(intentCamera);
        return true;
    }

    public boolean pickImage() {
        Intent intentPickImage = new Intent(this, PickImage.class);
        startActivity(intentPickImage);
        return true;
    }

}