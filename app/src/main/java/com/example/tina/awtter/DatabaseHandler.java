package com.example.tina.awtter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tina on 7/17/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myPhotos",
    TABLE_FAVORITES = "favorites",
    KEY_ID = "id",
    KEY_FAV = "fav",
    TABLE_MY_PICTURES = "myPictures",
    KEY_MY_PIC = "mypic",
    TABLE_NAME = "myName",
    KEY_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute an SQL query
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FAV + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_MY_PICTURES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MY_PIC + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + KEY_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MY_PICTURES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);

        onCreate(db);
    }

    public void createName(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String getName() {
        String result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);


        result = cursor.getString(0);

        cursor.close();

        Log.v("Databasehandler name ", result);

        return result;
    }

    public void createFavorite(int id, int favoriteNum) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, id);
        values.put(KEY_FAV, favoriteNum);

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public int getFavorite(int id) {
        int result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.query(TABLE_FAVORITES, new String[]{KEY_ID, KEY_FAV}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();

        if (cursor.getCount() > 0) {
            result = Integer.parseInt(cursor.getString(1));
        } else {
            result = -1;
        }

        cursor.close();

        return result;
    }

    public int getFavoriteFromAnimalID(int id) {
        int result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.query(TABLE_FAVORITES, new String[]{KEY_ID, KEY_FAV}, KEY_FAV + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();

        if (cursor.getCount() > 0) {
            result = Integer.parseInt(cursor.getString(1));
        } else {
            result = -1;
        }

        cursor.close();

        return result;
    }

    public void deleteFavorite(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_FAVORITES, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteFavoriteFromAnimalID(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_FAVORITES, KEY_FAV + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getFavoriteCount() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);
        count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    public int getLastIDMyFavorites() {
        int result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);

        if (cursor != null) {
            cursor.moveToLast();
        }

        db.close();

        if (cursor.getCount() > 0) {
            result = Integer.parseInt(cursor.getString(0));
        } else {
            result = -1;
        }

        cursor.close();

        return result + 1;
    }

    public ArrayList<String> getAllFavorites(String startPoint) {
        ArrayList<String> favorites = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " LIMIT 15 OFFSET " + startPoint, null);

        if (cursor.moveToFirst()) {
            do {
                favorites.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }

        return favorites;
    }

    public void createMyPicture(int id, int myPicNum) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, id);
        values.put(KEY_MY_PIC, myPicNum);

        db.insert(TABLE_MY_PICTURES, null, values);
        db.close();
    }

    public int getMyPicture(int id) {
        int result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.query(TABLE_MY_PICTURES, new String[] {KEY_ID, KEY_MY_PIC}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();

        if (cursor.getCount() > 0) {
            result = Integer.parseInt(cursor.getString(1));
        } else {
            result = -1;
        }

        cursor.close();

        return result;
    }

    public void deleteMyPicture(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_MY_PICTURES, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteMyPictureFromAnimalID(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_MY_PICTURES, KEY_MY_PIC + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getMyPictureCount() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MY_PICTURES, null);
        count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    public int getLastIDMyPicture() {
        int result;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor  = db.rawQuery("SELECT * FROM " + TABLE_MY_PICTURES, null);

        if (cursor != null) {
            cursor.moveToLast();
        }

        db.close();

        if (cursor.getCount() > 0) {
            result = Integer.parseInt(cursor.getString(0));
        } else {
            result = -1;
        }

        cursor.close();
        return result + 1;
    }

    public ArrayList<String> getAllMyPictures(String startPoint) {
        ArrayList<String> myPictures = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MY_PICTURES + " LIMIT 15 OFFSET " + startPoint, null);

        if (cursor.moveToFirst()) {
            do {
                myPictures.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }

        return myPictures;
    }
}
