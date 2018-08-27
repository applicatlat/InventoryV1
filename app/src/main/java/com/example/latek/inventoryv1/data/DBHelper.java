package com.example.latek.inventoryv1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOK_TABLE =  "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PRICE + " INTEGER,"
                + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER + " INTEGER, "
                + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
