package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FILTER = "filter";
    private static final String TAG_START_POINT = "start";
    private static final String TAG_ID = "__id";
    private static final String TAG_UPAWS = "__upAws";
    private static final String TAG_CREATEDAT = "__createdAt";
    private static final String TAG_PORTRAIT = "__portrait";
    private static final String TAG_ANIMALS = "animals";
    private static final String TAG_TOTAL_PICS = "totalPics";
    private static final String myFavoritesFragment = "myFavoritesFragment";

    private OnFragmentInteractionListener mListener;
    Context context;
    DatabaseHandler databaseHandler;

    RecyclerView rv;
    GridLayoutManager glm;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RVAdapter adapter;

    ArrayList<String> favorites;
    ArrayList<HashMap<String, String>> animalsList;
    List<Animal> animals;
    ArrayList<ArrayList<Object>> porOrLan;

    public boolean topPadding = true;

    boolean runOnce = false;

    String startPoint = "0";

    int stillLeft = 0;

    // On scroll
    private Handler handler;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        context = getActivity();

        databaseHandler = new DatabaseHandler(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler, container, false);

        animalsList = new ArrayList<HashMap<String, String>>();

        animals = new ArrayList<>();

        favorites = new ArrayList<String>();

        porOrLan = new ArrayList<ArrayList<Object>>();

        new LoadAnimals().execute();

        rv = (RecyclerView) view.findViewById(R.id.rv);

        glm = new GridLayoutManager(context, 3);

        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                // if it's a progress bar
                if (animals.get(position) == null) {
                    return 3;
                }

                switch (animals.get(position).sizeOrient) {
                    case 1:
                        return 1;
                    case 2:
                        return 2;
                    case 3:
                        return 3;
                    default:
                        return 0;
                }
            }
        });

        rv.setLayoutManager(glm);

        handler = new Handler();

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = glm.getChildCount();
                totalItemCount = glm.getItemCount();
                pastVisiblesItems = glm.findFirstVisibleItemPosition();

                if (loading) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                        // if there are still pictures left
                        if (animalsList.size() == 15) {

                            loading = false;

                            // add progress item
                            animals.add(null);
                            adapter.notifyItemInserted(animals.size() - 1);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //remove progress item
                                    animals.remove(animals.size() - 1);
                                    adapter.notifyItemRemoved(animals.size());

                                    new LoadAnimals().execute();

                                }
                            }, 700);

                        }

                    }
                }
            }
        });

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        int animalsSize = animals.size();

                        int i = animalsSize;
                        while (animals.size() > 0) {
                            animals.remove(--i);
                        }

                        i = porOrLan.size();
                        while (porOrLan.size() > 0) {
                            porOrLan.remove(--i);
                        }

                        loading = true;
                        startPoint = "0";
                        topPadding = true;
                        stillLeft = 0;

                        new LoadAnimals().execute();

                    }
                }
        );

        return view;
    }

    // System calls this method when adding the fragment to the activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Background Async Task to Load all animals by making HTTP Request
     */
    class LoadAnimals extends AsyncTask<String, String, String> {

        private static final String url_all_products = "http://76.244.35.83/get_animals.php";
        private static final String filter = "date";

        JSONParser jParser;
        JSONArray animals_all = null;

        private HashMap<String, String> map;
        ArrayList<HashMap<String, String>> threeAnimals;

        int indexAnimalsList = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jParser = new JSONParser();

            animalsList = new ArrayList<HashMap<String, String>>();

            threeAnimals = new ArrayList<HashMap<String, String>>();
        }

        /**
         * getting All products from url
         */
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {

            int animalid;
            boolean isPortrait;


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_FILTER, filter));
            params.add(new BasicNameValuePair(TAG_START_POINT, startPoint));

            favorites = databaseHandler.getAllFavorites(startPoint);

            params.add(new BasicNameValuePair(TAG_TOTAL_PICS, String.valueOf(favorites.size())));
            Log.v(TAG, "my favorites size: " + String.valueOf(favorites.size()));

            startPoint = String.valueOf(Integer.valueOf(startPoint) + 15);

            for (int index = favorites.size() - 1; index >= 0; --index) {
                animalid = Integer.parseInt(favorites.get(index));

                params.add(new BasicNameValuePair(String.valueOf(index), String.valueOf(animalid)));
            }

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON response
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = 0;
                try {
                    success = json.getInt(TAG_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();

                }

                if (success == 1) {
                    // products found
                    // Getting Array of Animals
                    animals_all = json.getJSONArray(TAG_ANIMALS);

                    // looping through All Panimals
                    for (int index = 0; index < animals_all.length(); index++) {
                        JSONObject c = animals_all.getJSONObject(index);

                        // Storing each json item in variable


                        String id = c.getString(TAG_ID);
                        String upAws = c.getString(TAG_UPAWS);
                        String date = c.getString(TAG_CREATEDAT);
                        try {
                            int portrait = c.getInt(TAG_PORTRAIT);
                            if (portrait == 1) {
                                isPortrait = true;
                            } else {
                                isPortrait = false;
                            }
                            // creating new HashMap
                            map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_ID, id);
                            map.put(TAG_UPAWS, upAws);
                            map.put(TAG_CREATEDAT, date);
                            map.put(TAG_PORTRAIT, String.valueOf(isPortrait));


                            // adding HashList to ArrayList
                            animalsList.add(map);
                        } catch (JSONException e) {
                            Log.v(TAG, "a picture has been deleted and cannot be loaded in favorites fragment");
                            databaseHandler.deleteFavoriteFromAnimalID(Integer.valueOf(favorites.get(index)));
                        }

                    }
                } else {
                    //No animals found || cannot connect to server
                }

                Log.v(TAG, "animalslist size: " + String.valueOf(animalsList.size()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String file_url) {
            String id;
            boolean isPortrait;
            int sizeOrient;

            // For every animal
            while (indexAnimalsList < animalsList.size()) {
                HashMap<String, String> animal1 = animalsList.get(indexAnimalsList);
                threeAnimals.add(animal1);

                // if por or lan doesn't have a pic in it left
                if (porOrLan.size() == 0) {

                    if (animalsList.size() > indexAnimalsList + 1) {

                        HashMap<String, String> animal2 = animalsList.get(indexAnimalsList + 1);
                        threeAnimals.add(animal2);

                    }

                    if (animalsList.size() > indexAnimalsList + 2) {

                        HashMap<String, String> animal3 = animalsList.get(indexAnimalsList + 2);
                        threeAnimals.add(animal3);

                    }

                    indexAnimalsList = indexAnimalsList + 3;

                } else if (porOrLan.size() == 1) {

                    if (animalsList.size() > indexAnimalsList + 1) {

                        HashMap<String, String> animal2 = animalsList.get(indexAnimalsList + 1);
                        threeAnimals.add(animal2);

                    }

                    indexAnimalsList = indexAnimalsList + 2;

                } else {
                    ++indexAnimalsList;
                }

                Log.v(TAG, "threeanimals size: " + String.valueOf(threeAnimals.size()));

                // Add threeanimals to pororlan
                for (int index = 0; index < threeAnimals.size(); index++) {

                    id = threeAnimals.get(index).get(TAG_ID);
                    isPortrait = Boolean.valueOf(threeAnimals.get(index).get(TAG_PORTRAIT));

                    ArrayList<Object> toAdd = new ArrayList<Object>();
                    toAdd.add(isPortrait);
                    toAdd.add(Integer.valueOf(id));

                    porOrLan.add(toAdd);
                }

                // Create Animal for three animals and add to animals
                reorganize();

                // Clear threeanimals
                int i = threeAnimals.size();
                while (threeAnimals.size() > 0) {
                    threeAnimals.remove(--i);
                }

            }

            // Attach animals to layout
            if (!runOnce) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RVAdapter(animals, context, myFavoritesFragment);
                        rv.setAdapter(adapter);
                        SpacesItemDecoration spaces = new SpacesItemDecoration(13, animals);
                        rv.addItemDecoration(spaces);

                    }
                });
                runOnce = true;
            }

            // Stop refreshing sign
            mySwipeRefreshLayout.setRefreshing(false);

            // Can load more animals now
            loading = true;
        }

        private void reorganize() {

            // If refresh, now notifydatasetchanged
            if (topPadding == true && adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (stillLeft == 1) {
                Log.v(TAG, "deleting one animal");
                animals.remove(animals.size() - 1);

                if (adapter != null) {
                    adapter.notifyItemRemoved(animals.size());
                }
            } else if (stillLeft == 2) {
                Log.v(TAG, "deleting two animals");
                int size = animals.size();
                animals.remove(size - 1);
                animals.remove(size - 2);

                if (adapter != null) {
                    adapter.notifyItemRangeRemoved(size - 2, 2);
                }
            }

            stillLeft = 0;

            if (porOrLan.size() == 3) {

                if ((boolean) (porOrLan.get(0).get(0)) == true && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == true) {
                    // PPP
                    Log.v(TAG, "P,P,P");
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, topPadding, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v(TAG, "P1L,P2");
                    //P1 L aka PPL
                    //P2
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 2, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, false, true, true));

                    porOrLan.set(0, porOrLan.get(1));
                    porOrLan.remove(2);
                    porOrLan.remove(1);

                    stillLeft = 1;

                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v(TAG, "PL,L");
                    //PL
                    //L

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 3, false, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == true) {
                    Log.v(TAG, "PL,P3");
                    // PL
                    // P3
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, false, true, true));

                    porOrLan.set(0, porOrLan.get(2));
                    porOrLan.remove(2);
                    porOrLan.remove(1);

                    stillLeft = 1;

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v(TAG, "L,L,L");
                    //L
                    //L
                    //L
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 3, false, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 3, false, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == true) {
                    Log.v(TAG, "L,LP");
                    //L
                    //LP

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, false, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, false, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v(TAG, "LP,L");
                    //LP
                    //L

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 3, false, false, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == true) {
                    Log.v(TAG, "LP,P3");
                    //LP
                    //P3

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, false, true, true));

                    porOrLan.set(0, porOrLan.get(2));
                    porOrLan.remove(2);
                    porOrLan.remove(1);

                    stillLeft = 1;
                }


                if (adapter != null) {
                    adapter.notifyItemRangeInserted(animals.size() - 3, 3);
                }

            } else if (porOrLan.size() == 2) {
                Log.v(TAG, "size = 2");

                if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == true) {
                    Log.v(TAG, "P, P");

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, false, true));

                    stillLeft = 2;

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == false) {
                    Log.v(TAG, "L, L");

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 3, false, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == true) {
                    Log.v(TAG, "P, L");

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else {
                    Log.v(TAG, "L, P");

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                }

                if (adapter != null) {
                    adapter.notifyItemRangeInserted(animals.size() - 2, 2);
                }

            } else if (porOrLan.size() == 1) {
                Log.v(TAG, "size = 1");

                if ((boolean) porOrLan.get(0).get(0)) {
                    Log.v(TAG, "P");

                    stillLeft = 1;

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                } else {
                    Log.v(TAG, "L");

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, false, true));

                    porOrLan.remove(0);

                }


                if (adapter != null) {
                    adapter.notifyItemInserted(animals.size() - 1);
                }
            }

            // Only have top padding once
            if (topPadding == true) {
                topPadding = false;
            }

        }
    }
}