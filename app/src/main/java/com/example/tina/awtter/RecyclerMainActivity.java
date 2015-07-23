package com.example.tina.awtter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecyclerMainActivity extends AppCompatActivity {

    List<Animal> animals;
    Animal[] posts = new Animal[3];
    boolean[] porOrLan =  new boolean[3];

    public int height;
    public int width;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<HashMap<String, String>> animals3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);

        Intent intent = getIntent();
        animals3 = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("3 animals");


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        initializeData();

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);

        final GridLayoutManager glm = new GridLayoutManager(this, 3);
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

        RVAdapter adapter = new RVAdapter(animals, this, glm);
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
                new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // STUB:
                        // The click on the item must be handled
                        Toast.makeText(getApplicationContext(), "itemclick: " + position, Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // This method creates an ArrayList that has three Animal objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData() {
        this.animals = new ArrayList<>();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.cuteturtle, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        boolean isPortrait = false;

        if(imageHeight > imageWidth) {
            isPortrait = true;
        }
        porOrLan[0] = isPortrait;


        BitmapFactory.decodeResource(getResources(), R.drawable.kappa, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;

        isPortrait = false;

        if(imageHeight > imageWidth) {
            isPortrait = true;
        }
        porOrLan[1] = isPortrait;


        BitmapFactory.decodeResource(getResources(), R.drawable.richelle, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;

        isPortrait = false;

        if(imageHeight > imageWidth) {
            isPortrait = true;
        }
        porOrLan[2] = isPortrait;

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



}
