package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

public class MyPicturesFragment extends Fragment {

    private static final String myPicturesFragment = "myPicturesFragment";
    private static final String TAG = "MyPicturesFragment";
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

    private OnFragmentInteractionListener mListener;
    Context context;
    DatabaseHandler databaseHandler;

    RecyclerView rv;
    GridLayoutManager glm;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RVAdapter adapter;

    ArrayList<HashMap<String, String>> animalsList;
    List<Animal> animals;
    ArrayList<String> myPictures;

    public boolean topPadding = true;

    public boolean runOnce = false;

    String startPoint = "0";

    // On scroll
    private Handler handler;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public MyPicturesFragment() {
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

        myPictures = new ArrayList<String>();

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
                            adapter.notifyItemInserted(animals.size());

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //remove progress item
                                    animals.remove(animals.size() - 1);
                                    adapter.notifyItemRemoved(animals.size());

                                    new LoadAnimals().execute();

                                }
                            }, 700);

                        } else {

                            loading = false;

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

                        int i = animals.size();
                        while (animals.size() > 0) {
                            animals.remove(--i);
                        }

                        loading = true;
                        startPoint = "0";
                        topPadding = true;

                        new LoadAnimals().execute();
                    }
                }
        );

        return view;

    }

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
        inflater.inflate(R.menu.menu_my_pictures, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    class LoadAnimals extends AsyncTask<String, String, String> {

        private static final String url_all_products = "http://76.244.35.83/get_animals.php";
        private static final String filter = "date";

        JSONParser jParser;
        JSONArray animals_all = null;

        private HashMap<String, String> map;

        ArrayList<HashMap<String, String>> threeAnimals;
        ArrayList<ArrayList<Object>> porOrLan;

        int indexAnimalsList = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            jParser = new JSONParser();

            animalsList = new ArrayList<HashMap<String, String>>();

            threeAnimals = new ArrayList<HashMap<String, String>>();

            porOrLan = new ArrayList<ArrayList<Object>>();
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

            myPictures = databaseHandler.getAllMyPictures(startPoint);

            params.add(new BasicNameValuePair(TAG_TOTAL_PICS, String.valueOf(myPictures.size())));
            Log.v("mypicture", "my pictures size: " + String.valueOf(myPictures.size()));

            startPoint = String.valueOf(Integer.valueOf(startPoint) + 15);

            for (int index = myPictures.size() - 1; index >= 0; --index) {
                animalid = Integer.parseInt(myPictures.get(index));

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
                            databaseHandler.deleteMyPictureFromAnimalID(myPictures.get(index));
                        }
                    }
                } else {
                    //No animals found
                }
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

                if (animalsList.size() > indexAnimalsList + 1) {

                    HashMap<String, String> animal2 = animalsList.get(indexAnimalsList + 1);
                    threeAnimals.add(animal2);

                }

                // if por or lan doesn't have a pic in it left
                if (porOrLan.size() == 0) {

                    if (animalsList.size() > indexAnimalsList + 2) {

                        HashMap<String, String> animal3 = animalsList.get(indexAnimalsList + 2);
                        threeAnimals.add(animal3);

                    }

                    indexAnimalsList = indexAnimalsList + 3;

                } else {

                    indexAnimalsList = indexAnimalsList + 2;

                }

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

            // If pororlan still has an animal left from reorganize, add it to animals to show it
            if (porOrLan.size() > 0) {
                if ((boolean) porOrLan.get(0).get(0) == true) {
                    sizeOrient = 1;
                } else {
                    sizeOrient = 3;
                }
                animals.add(new Animal((int) porOrLan.get(0).get(1), sizeOrient, false, true, true));
            }

            // Attach animals to layout
            if (!runOnce) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RVAdapter(animals, context, myPicturesFragment);
                        rv.setAdapter(adapter);
                        SpacesItemDecoration spaces = new SpacesItemDecoration(13, animals);
                        rv.addItemDecoration(spaces);

                    }
                });
                runOnce = true;
            } else {
                adapter.notifyDataSetChanged();
            }

            // Stop refreshing sign
            mySwipeRefreshLayout.setRefreshing(false);

            // Can load more animals now
            loading = true;
        }

        private void reorganize() {

            if (porOrLan.size() == 3) {

                if ((boolean) (porOrLan.get(0).get(0)) == true && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == true) {
                    // PPP
                    Log.v("HERE", "P,P,P");
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, topPadding, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);


                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v("HERE", "P1L,P2");
                    //P1 L aka PPL
                    //P2
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 2, topPadding, true, true));

                    porOrLan.set(0, porOrLan.get(1));
                    porOrLan.remove(2);
                    porOrLan.remove(1);

                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v("HERE", "PL,L");
                    //PL
                    //L

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 3, false, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == true) {
                    Log.v("HERE", "PL,P3");
                    // PL
                    // P3
                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    porOrLan.set(0, porOrLan.get(2));
                    porOrLan.remove(2);
                    porOrLan.remove(1);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == false && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v("HERE", "L,L,L");
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
                    Log.v("HERE", "L,LP");
                    //L
                    //LP

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, false, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 1, false, true, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == false) {
                    Log.v("HERE", "LP,L");
                    //LP
                    //L

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(2).get(1), 3, false, false, true));

                    porOrLan.remove(2);
                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == true && (boolean) porOrLan.get(2).get(0) == true) {
                    Log.v("HERE", "LP,P3");
                    //LP
                    //P3

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    porOrLan.set(0, porOrLan.get(2));
                    porOrLan.remove(2);
                    porOrLan.remove(1);
                }
            } else if (porOrLan.size() == 2) {
                Log.v("HERE", "size = 2");

                if ((boolean) porOrLan.get(0).get(0) == true && (boolean) porOrLan.get(1).get(0) == true) {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, false, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == false && (boolean) porOrLan.get(1).get(0) == false) {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, true, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 3, false, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else if ((boolean) porOrLan.get(0).get(0) == true) {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 2, topPadding, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                } else {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 2, topPadding, false, true));

                    animals.add(new Animal((int) porOrLan.get(1).get(1), 1, topPadding, true, true));

                    porOrLan.remove(1);
                    porOrLan.remove(0);

                }

            } else if (porOrLan.size() == 1) {
                Log.v("HERE", "size = 1");

                if ((boolean) porOrLan.get(0).get(0)) {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 1, topPadding, false, true));

                    porOrLan.remove(0);

                } else {

                    animals.add(new Animal((int) porOrLan.get(0).get(1), 3, topPadding, false, true));

                    porOrLan.remove(0);

                }
            }

            if (topPadding == true) {
                topPadding = false;
            }

        }
    }
}