package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.internal.zzhl.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Context context;
    private OnFragmentInteractionListener mListener;

    ArrayList<HashMap<String, String>> animalsList;
    ListView lv;

    String startPoint = "1";
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /* Test Picasso

        //Initialize ImageView
        ImageView imageView = (ImageView) view.findViewById(R.id.content);

        //Loading image from below url into imageView

        Picasso.with(getActivity())
                .load("http://76.244.35.83/images/grapefruit")
                .into(imageView);*/

        // Hashmap for ListView
        animalsList = new ArrayList<HashMap<String, String>>();
        // Get listview
        lv = (ListView) view.findViewById(R.id.listView);
        // Loading products in Background Thread
        new LoadAnimals().execute();




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

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAnimals extends AsyncTask<String, String, String> {

        String filter = "popularity";

        // Progress Dialog
        private ProgressDialog pDialog;

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        // url to get all products list
        private String url_all_products = "http://76.244.35.83/get_animals.php";

        private HashMap<String, String> map;

        // JSON Node names
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_FILTER = "filter";
        private static final String TAG_START_POINT = "start";
        private static final String TAG_ID = "__id";
        private static final String TAG_UPAWS = "__upAws";
        private static final String TAG_CREATEDAT = "__createdAt";
        private static final String TAG_ANIMALS = "animals";

        // products JSONArray
        JSONArray animals = null;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * getting All products from url
         * */
        @SuppressWarnings( "deprecation" )
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_FILTER, filter));
            params.add(new BasicNameValuePair(TAG_START_POINT, startPoint));

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
                    animals = json.getJSONArray(TAG_ANIMALS);

                    // looping through All Panimals
                    for (int i = 0; i < animals.length(); i++) {
                        JSONObject c = animals.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String upAws = c.getString(TAG_UPAWS);
                        String date = c.getString(TAG_CREATEDAT);

                        // creating new HashMap
                        map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_UPAWS, upAws);
                        map.put(TAG_CREATEDAT, date);

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
         * **/
        protected void onPostExecute(String file_url) {

            ArrayList<HashMap<String, String>> threeAnimals = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> animal1 = animalsList.get(0);
            HashMap<String, String> animal2 = animalsList.get(1);
            HashMap<String, String> animal3 = animalsList.get(2);

            threeAnimals.add(animal1);
            threeAnimals.add(animal2);
            threeAnimals.add(animal3);

            Intent intent = new Intent (context, RecyclerMainActivity.class);
            intent.putExtra("3 animals", threeAnimals);
            startActivity(intent);

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            context, animalsList,
                            R.layout.list_item, new String[]{TAG_ID,
                            TAG_UPAWS, TAG_CREATEDAT},
                            new int[]{R.id.id, R.id.upAws, R.id.createdAt});
                    // updating listview
                    lv.setAdapter(adapter);
                }
            });



        }

    }
}