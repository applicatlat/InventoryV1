package com.example.latek.inventoryv1;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.latek.inventoryv1.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

        public BookCursorAdapter(Context context, Cursor c) {
            super(context, c, 0 /* flags */);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Inflate a list item view using the layout specified in list_item.xml
            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        @Override
        public void bindView(final View view, final Context context, Cursor cursor) {

            TextView bookNameTextView = view.findViewById(R.id.name_book_text_view);
            TextView bookPriceTextView = view.findViewById(R.id.book_price_text_view);
            TextView bookQuantityTextView = view.findViewById(R.id.book_quantity_text_view);
            Button bookSaleButton = view.findViewById(R.id.sale_button);

            final int columnIdIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int bookNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int bookPriceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int bookQuantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);

            final String bookID = cursor.getString(columnIdIndex);
            String bookName = cursor.getString(bookNameColumnIndex);
            String bookPrice = cursor.getString(bookPriceColumnIndex);
            final String bookQuantity = cursor.getString(bookQuantityColumnIndex);

            bookSaleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.BookActivity Activity = (MainActivity.BookActivity) context;
                    Activity.bookSaleCount(Integer.valueOf(bookID), Integer.valueOf(bookQuantity));
                }
            });

            bookNameTextView.setText(bookID + " ) " + bookName);
            bookPriceTextView.setText(context.getString(R.string.book_price) + " : " + bookPrice + "  " + context.getString(R.string.book_price_currency));
            bookQuantityTextView.setText(context.getString(R.string.book_quantity) + " : " + bookQuantity);

            Button bookEditButton = view.findViewById(R.id.edit_button);
            bookEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), EditorActivity.class);
                    Uri currentProdcuttUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, Long.parseLong(bookID));
                    intent.setData(currentProdcuttUri);
                    context.startActivity(intent);
                }
            });




        }
    }
