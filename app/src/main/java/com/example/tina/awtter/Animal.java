package com.example.tina.awtter;

/**
 * Created by richellevital on 7/19/15.
 */

import android.graphics.Bitmap;

/**
 * Created by richellevital on 7/19/15.
 */
class Animal {
    Bitmap image;
    int time;
    int upAws;
    int sizeOrient;
    boolean right;
    boolean left;
    boolean top;

    Animal(Bitmap image, int sizeOrient, boolean top, boolean right, boolean left) {
        this.image = image;
        upAws = 0;
        this.sizeOrient = sizeOrient;
        this.top = top;
        this.right = right;
        this.left = left;
    }



}
