package com.example.tina.awtter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    String startPoint;



    List<Animal> animals;
    Animal[] posts = new Animal[3];
    HashMap<Boolean, Bitmap> porOrLan = new HashMap<Boolean, Bitmap>();

    public int height;
    public int width;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<HashMap<String, String>> threeAnimals;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FILTER = "filter";
    private static final String TAG_START_POINT = "start";
    private static final String TAG_ID = "__id";
    private static final String TAG_UPAWS = "__upAws";
    private static final String TAG_CREATEDAT = "__createdAt";
    private static final String TAG_ANIMALS = "animals";
    private static final String url = "http://76.244.35.83/media/";


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

        animalsList = new ArrayList<HashMap<String, String>>();

        new LoadAnimals().execute();




        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        initializeData();

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);

        final GridLayoutManager glm = new GridLayoutManager(context, 3);
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

        RVAdapter adapter = new RVAdapter(animals, context, glm);
        rv.setAdapter(adapter);
        SpacesItemDecoration spaces = new SpacesItemDecoration(13, animals);
        rv.addItemDecoration(spaces);


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
                    @Override public void onItemClick(View view, int position) {
                        // STUB:
                        // The click on the item must be handled
                        Toast.makeText(context, "itemclick: " + position, Toast.LENGTH_SHORT).show();

                    }
                }));


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

    // This method creates an ArrayList that has three Animal objects
    private void initializeData() {
        this.animals = new ArrayList<>();
        String id;
        Bitmap bitmap;
        int imageHeight, imageWidth;
        String src;
        for (int index = 0; index < threeAnimals.size(); index++) {
            id = threeAnimals.get(0).get(TAG_ID);

            src = url + id;

            try {

                //TODO: Move this to async task eventually
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                imageHeight = bitmap.getHeight();
                imageWidth = bitmap.getWidth();
                boolean isPortrait = false;

                if(imageHeight > imageWidth) {
                    isPortrait = true;
                }
                porOrLan[index] = isPortrait;

            } catch (IOException e) {
                // Log exception
            }

        }

        reorganize();

    }

    private void reorganize() {
        animals = new ArrayList<Animal>();
        boolean firstRow = false;

        if(porOrLan[0] == true && porOrLan[1] == true && porOrLan[2] == true) {
            // PPP
            Log.v("HERE", "P,P,P");
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, false, true));
            ///////////////////

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, true, false));


        } else if(porOrLan[0] == true && porOrLan[1] == true && porOrLan[2] == false) {
            Log.v("HERE", "P1L,P2");
            //P1 L
            //P2
        } else if(porOrLan[0] == true && porOrLan[1] == false && porOrLan[2] == false) {
            Log.v("HERE", "PL,L");
            //PL
            //L

            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, false, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, true, true));


        } else if(porOrLan[0] == true && porOrLan[1] == false && porOrLan[2] == true) {
            Log.v("HERE", "PL,P3");
            // PL
            // P3
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, false, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, false, true));


        } else if(porOrLan[0] == false && porOrLan[1] == false && porOrLan[2] == false) {
            Log.v("HERE", "L,L,L");
            //L
            //L
            //L
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, false, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, true, true));


        } else if(porOrLan[0] == false && porOrLan[1] == false && porOrLan[2] == true) {
            Log.v("HERE", "L,LP");
            //L
            //LP

            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, false, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, true, true));


        } else if(porOrLan[0] == false && porOrLan[1] == true && porOrLan[2] == false) {
            Log.v("HERE", "LP,L");
            //LP
            //L

            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, false, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, false, true));


        } else if(porOrLan[0] == false && porOrLan[1] == true && porOrLan[2] == true) {
            Log.v("HERE", "LP,P3");
            //LP
            //P3

            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.cuteturtle);
            animals.add(new Animal(icon, 1, true, false, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.kappa), 2, true, true, true));

            animals.add(new Animal(BitmapFactory.decodeResource(this.getResources(), R.drawable.richelle), 3, false, false, true));
        }


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

            threeAnimals = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> animal1 = animalsList.get(0);
            HashMap<String, String> animal2 = animalsList.get(1);
            HashMap<String, String> animal3 = animalsList.get(2);

            threeAnimals.add(animal1);
            threeAnimals.add(animal2);
            threeAnimals.add(animal3);;


        }

    }
}