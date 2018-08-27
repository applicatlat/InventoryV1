package com.example.latek.inventoryv1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BookProvider extends ContentProvider {
    private DBHelper connectorDbHelper;
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS+ "/#", BOOKS);    }
        @Override
        public boolean onCreate() {
        connectorDbHelper = new DBHelper(getContext());
            return true;
        }
        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            SQLiteDatabase database = connectorDbHelper.getReadableDatabase();
            Cursor cursor;
            int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    break;
                case BOOK_ID:
                    selection = BookContract.BookEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                            null, null, sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }
            return cursor;
        }
        @Override
        public Uri insert(Uri uri, ContentValues contentValues) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return insertBook(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
            }
        }
     private Uri insertBook(Uri uri, ContentValues values) {
            SQLiteDatabase database = connectorDbHelper.getWritableDatabase();
         String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
         if (name == null) {
             throw new IllegalArgumentException("Book Name Required");
         }
         long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

         if (id == -1) {
             return null;
         }
        return ContentUris.withAppendedId(uri, id);
    }

        @Override
        public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return updateBooks(uri, contentValues, selection, selectionArgs);
                case BOOK_ID:
                    selection = BookContract.BookEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    return updateBooks(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }
         private int updateBooks(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
             if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME)) {
                 String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
                 if (name == null) {
                     throw new IllegalArgumentException("Type in a book name");
                 }
             }

             if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)) {
                 Integer weight = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
                 if (weight != null && weight < 0) {
                     throw new IllegalArgumentException("A valid quantity must be entered");
                 }
             }
             if (values.size() == 0) {
                 return 0;
             }
             SQLiteDatabase database = connectorDbHelper.getWritableDatabase();
             int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
             if (rowsUpdated != 0) {
                 getContext().getContentResolver().notifyChange(uri, null);
             }
             return rowsUpdated;
      }
        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            SQLiteDatabase database = connectorDbHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                case BOOK_ID:
                    selection = BookContract.BookEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    return database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }
        }

        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BOOKS:
                    return BookContract.BookEntry.CONTENT_LIST_TYPE;
                case BOOK_ID:
                    return BookContract.BookEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }        }
    }

