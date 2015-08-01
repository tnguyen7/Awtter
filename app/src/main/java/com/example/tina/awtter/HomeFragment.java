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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    // TODO:May be unecessary
    private static final String homeFragment = "homeFragment";

    public Context context;
    private OnFragmentInteractionListener mListener;

    ArrayList<HashMap<String, String>> animalsList;
    String startPoint = "0";

    boolean topPadding = true;

    List<Animal> animals;
    ArrayList<ArrayList<Object>> porOrLan;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<HashMap<String, String>> threeAnimals;
    RecyclerView rv;
    GridLayoutManager glm;

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
    private static final String url = "http://76.244.35.83/media/";

    public boolean runOnce = false;

    private Handler handler;


    SwipeRefreshLayout mySwipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler, container, false);

        handler = new Handler();
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("refresh", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.


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

        // Holds results from database
        animals = new ArrayList<>();

        new LoadAnimals().execute();

        rv = (RecyclerView) view.findViewById(R.id.rv);

        glm = new GridLayoutManager(context, 3);
        rv.setLayoutManager(glm);

        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (animals.get(position) == null) {
                    return 3;
                }

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

                        if (animalsList.size() == 15) {
                            loading = false;
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
                            }, 1000);

                        } else {
                            loading = false;
                        }

                    }
                }
            }
        });




        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**********************************************************************************************/

    /**
     * Background Async Task to Load all animals by making HTTP Request
     * */
    class LoadAnimals extends AsyncTask<String, String, String> {

        String filter = "popularity";
        int indexThreeAnimals = 0;

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

            animalsList = new ArrayList<HashMap<String, String>>();

            porOrLan = new ArrayList<ArrayList<Object>>();
        }

        /**
         * getting All products from url
         */
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_FILTER, filter));
            params.add(new BasicNameValuePair(TAG_START_POINT, startPoint));
            startPoint = String.valueOf(Integer.valueOf(startPoint) + 15);
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
                        int portrait = c.getInt(TAG_PORTRAIT);
                        boolean isPortrait;
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

            if (!runOnce) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RVAdapter(animals, context, glm, homeFragment);
                        rv.setAdapter(adapter);
                        SpacesItemDecoration spaces = new SpacesItemDecoration(13, animals);
                        rv.addItemDecoration(spaces);

                    }
                });
                runOnce = true;
            } else {
                adapter.notifyDataSetChanged();
            }

            mySwipeRefreshLayout.setRefreshing(false);
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

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (topPadding == true) {
                topPadding = false;
            }

        }
    }
}