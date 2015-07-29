package com.example.tina.awtter;

/**
 * Created by richellevital on 7/19/15.
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by richellevital on 7/19/15.
 */
class Animal {
    int upAws;
    int sizeOrient;
    boolean right;
    boolean left;
    boolean top;
    int id;

    public Animal(int id, int sizeOrient, boolean top, boolean right, boolean left)

    {
        this.id = id;
        upAws = 0;
        this.sizeOrient = sizeOrient;
        this.top = top;
        this.right = right;
        this.left = left;
    }

}
