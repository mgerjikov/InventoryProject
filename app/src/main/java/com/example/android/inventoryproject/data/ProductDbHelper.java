package com.example.android.inventoryproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Martin on 17.7.2017 г..
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    // Here we create a String that contains the SQL statement to create the pets table
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ( " +
            ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            ProductContract.ProductEntry.COLUMN_PRICE + " REAL NOT NULL, " +
            ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
            ProductContract.ProductEntry.COLUMN_IMAGE + " BLOB NOT NULL ) " +
            ";";
    /**
     * Database version. If the schema of the database is changed the version also
     * have to be changed incrementally. The convention is that the version starts from 1
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v("SQLite Entries: ", SQL_CREATE_ENTRIES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table or read from existent one
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
