package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

    private OnFragmentInteractionListener mListener;

    DatabaseHandler databaseHandler;

    ArrayList<HashMap<String, String>> threeAnimals;
    RecyclerView rv;
    GridLayoutManager glm;

    ArrayList<HashMap<String, String>> animalsList;
    List<Animal> animals;
    Animal[] posts = new Animal[3];
    ArrayList<ArrayList<Object>> porOrLan;

    Context context;

    RVAdapter adapter;

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
    private static final String TAG_ANIMAL = "animal";
    private static final String url = "http://76.244.35.83/media/";
    private static final String myPicturesFragment = "myPicturesFragment";


    public boolean runOnce = false;

    int indexThreeAnimals = 0;

    public boolean topPadding = true;

    String startPoint;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    SwipeRefreshLayout mySwipeRefreshLayout;

    boolean refresh = false;

    ArrayList<String> myPictures;

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

        myPictures = new ArrayList<String>();
        // Holds results from database
        animalsList = new ArrayList<HashMap<String, String>>();

        animals = new ArrayList<>();

        porOrLan = new ArrayList<ArrayList<Object>>();

        new LoadAnimals().execute();

        rv = (RecyclerView) view.findViewById(R.id.rv);

        glm = new GridLayoutManager(context, 3);
        rv.setLayoutManager(glm);

        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Log.v("ADAPT", "SIZEORIENT = " + animals.get(position).sizeOrient);
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

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = glm.getChildCount();
                totalItemCount = glm.getItemCount();
                pastVisiblesItems = glm.findFirstVisibleItemPosition();

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        Log.v("...", "Last Item Wow !");
                    }
                }
            }
        });

        rv.addOnItemTouchListener( // and the click is handled
                new RecyclerClickListener(context, new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // STUB:
                        // The click on the item must be handled

                        Toast.makeText(context, "itemclick: " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, FullPicture.class);
                        intent.putExtra("animalid", animals.get(position).id);
                        startActivity(intent);


                    }
                })
        );

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("refresh", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        int i = animalsList.size();
                        while (animalsList.size() > 0) {
                            animalsList.remove(--i);
                        }

                        i = animals.size();
                        while (animals.size() > 0) {
                            animals.remove(--i);
                        }

                        i = porOrLan.size();
                        while (porOrLan.size() > 0) {
                            porOrLan.remove(--i);
                        }

                        topPadding = true;
                        indexThreeAnimals = 0;
                        runOnce = false;
                        if (adapter != null) {
                            refresh = true;

                        }
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
    public void onSaveInstanceState(Bundle outstate) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_pictures, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        switch (item.getItemId()) {
            case R.id.action_add:

                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class LoadAnimals extends AsyncTask<String, String, String> {

        String filter = "date";

        // Progress Dialog
        private ProgressDialog pDialog;

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        // url to get all products list
        private String url_all_products = "http://76.244.35.83/get_animals.php";

        private HashMap<String, String> map;

        // products JSONArray
        JSONArray animals_all = null;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         */
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {
            boolean isPortrait;

            int totalPics = databaseHandler.getMyPictureCount();
            int animalid;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_FILTER, filter));
            params.add(new BasicNameValuePair(TAG_START_POINT, startPoint));
            params.add(new BasicNameValuePair(TAG_TOTAL_PICS, String.valueOf(totalPics)));


            Log.v("mypicture", "count: " + String.valueOf(totalPics));

            myPictures = databaseHandler.getAllMyPictures();

            for (int index = myPictures.size() - 1; index >= 0; --index) {
                animalid = Integer.parseInt(myPictures.get(index));

                Log.v("mypicture", "animalid: " + String.valueOf(animalid));

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
                    for (int i = 0; i < animals_all.length(); i++) {
                        JSONObject c = animals_all.getJSONObject(i);

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
                            Log.v("favorites fragment", "a picture has been deleted and cannot be loaded in favorites fragment");
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
            // TODO: Fix to keep getting three until load certain amount of pictures
            // Holds three animals to be organized
            String id;
            boolean isPortrait;

            threeAnimals = new ArrayList<HashMap<String, String>>();

            while (indexThreeAnimals < animalsList.size()) {
                HashMap<String, String> animal1 = animalsList.get(indexThreeAnimals);
                threeAnimals.add(animal1);

                if (animalsList.size() > indexThreeAnimals + 1) {

                    HashMap<String, String> animal2 = animalsList.get(indexThreeAnimals + 1);
                    threeAnimals.add(animal2);

                }

                // if por or lan still has a pic left then only add two animals and increment the index by two
                if (porOrLan.size() == 0) {

                    if (animalsList.size() > indexThreeAnimals + 2) {

                        HashMap<String, String> animal3 = animalsList.get(indexThreeAnimals + 2);
                        threeAnimals.add(animal3);

                    }

                    indexThreeAnimals = indexThreeAnimals + 3;

                } else {

                    indexThreeAnimals = indexThreeAnimals + 2;

                }

                Log.v("threeanimals", String.valueOf(threeAnimals.size()));

                for (int index = 0; index < threeAnimals.size(); index++) {
                    id = threeAnimals.get(index).get(TAG_ID);
                    isPortrait = Boolean.valueOf(threeAnimals.get(index).get(TAG_PORTRAIT));

                    ArrayList<Object> toAdd = new ArrayList<Object>();
                    toAdd.add(isPortrait);
                    toAdd.add(Integer.valueOf(id));

                    porOrLan.add(toAdd);
                }

                reorganize();

                int i = threeAnimals.size();
                while (threeAnimals.size() > 0) {
                    threeAnimals.remove(--i);
                }

            }

            int sizeOrient;
            if (porOrLan.size() > 0) {
                if ((boolean) porOrLan.get(0).get(0) == true) {
                    sizeOrient = 1;
                } else {
                    sizeOrient = 3;
                }
                animals.add(new Animal((int) porOrLan.get(0).get(1), sizeOrient, false, true, true));
            }

            if (!refresh) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RVAdapter(animals, context, glm, myPicturesFragment);
                        rv.setAdapter(adapter);
                        SpacesItemDecoration spaces = new SpacesItemDecoration(13, animals);
                        rv.addItemDecoration(spaces);

                    }
                });
            } else {
                adapter = new RVAdapter(animals, context, glm, myPicturesFragment);
                rv.setAdapter(adapter);
                refresh = false;
            }


            mySwipeRefreshLayout.setRefreshing(false);
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