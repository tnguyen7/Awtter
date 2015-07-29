package com.example.tina.awtter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by richellevital on 7/20/15.
 */
public class RecyclerClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;
    GestureDetector mGestureDetector;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public RecyclerClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        final Context mcontext = context;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                //Toast.makeText(mcontext,"single", Toast.LENGTH_SHORT).show();
                return true;
            }
            //@Override public boolean onDoubleTapEvent(MotionEvent e) {
                //Toast.makeText(mcontext, "double", Toast.LENGTH_SHORT).show();
            //    return true;
           // }

        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

    }

}
