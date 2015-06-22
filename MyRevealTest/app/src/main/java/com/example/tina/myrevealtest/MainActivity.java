package com.example.tina.myrevealtest;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Path;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    public void setup() {
        View button = findViewById(R.id.button);
        View button2 = findViewById(R.id.button2);
        View button3 = findViewById(R.id.button3);
        View button4 = findViewById(R.id.button4);
        View button5 = findViewById(R.id.button5);
        View button6 = findViewById(R.id.button6);
        View button7 = findViewById(R.id.button7);
        View button8 = findViewById(R.id.button8);
        View button9 = findViewById(R.id.button9);
        View button10 = findViewById(R.id.button10);
        View button11 = findViewById(R.id.button11);
        View button12 = findViewById(R.id.button12);
        View button13 = findViewById(R.id.button13);
        View buttonR = findViewById(R.id.buttonR);

        // AccelerateDecelerateInterpolator());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new AccelerateDecelerateInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // AccelerateInterpolator();
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new AccelerateInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // AnticipateInterpolator
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new AnticipateInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        //  AnticipateOvershootInterpolator
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new AnticipateOvershootInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        //  BounceInterpolator
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new BounceInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // CycleInterpolator
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new CycleInterpolator(1));

                // Finally start the animation
                animator.start();
            }
        });

        // DecelerateInterpolator
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new DecelerateInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        //  FastOutLinearInInterpolator
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new FastOutLinearInInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        //  FastOutSlowInInterpolator
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new FastOutSlowInInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        //  LinearInterpolator
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new LinearInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // LinearOutSlowInInterpolator
        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new LinearOutSlowInInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // OvershootInterpolator
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new OvershootInterpolator());

                // Finally start the animation
                animator.start();
            }
        });

        // PathInterpolator
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View shape = findViewById(R.id.circle);

                // Create a reveal {@link Animator} that starts clipping the view from
                // the top left corner until the whole view is covered.
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        shape.getWidth()/2,
                        shape.getHeight()/2,
                        0,
                        ((float) Math.hypot(shape.getWidth(), shape.getHeight()))/2);

                Path path = new Path();
                path.lineTo(0.25f, 0.25f);
                path.moveTo(0.25f, 0.5f);
                path.lineTo(1f, 1f);

                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new PathInterpolator(path));

                // Finally start the animation
                animator.start();
            }
        });
        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.readme);
            }
        });
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

    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);
        setup();
    }
}
