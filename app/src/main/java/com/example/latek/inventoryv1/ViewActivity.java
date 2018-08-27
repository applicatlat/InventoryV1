package com.example.latek.inventoryv1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.latek.inventoryv1.data.BookContract;

public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentBookUri;

    private TextView mBookNameViewText;
    private TextView mBookPriceViewText;
    private TextView mBookQuantityViewText;
    private TextView mBookSupplieNameSpinner;
    private TextView mBookSupplierPhoneNumberViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mBookNameViewText = findViewById(R.id.name_book_text_view);
        mBookPriceViewText = findViewById(R.id.book_price_text_view);
        mBookQuantityViewText = findViewById(R.id.book_quantity_text_view);
        mBookSupplieNameSpinner = findViewById(R.id.book_supplier_name_view_text);
        mBookSupplierPhoneNumberViewText = findViewById(R.id.book_supplier_phone_number_view_text);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        Log.d("message", "onCreate ViewActivity");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER,
                BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            final int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER);

            String currentName = cursor.getString(nameColumnIndex);
            final int currentPrice = cursor.getInt(priceColumnIndex);
            final int currentQuantity = cursor.getInt(quantityColumnIndex);
            final int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mBookNameViewText.setText(currentName);
            mBookPriceViewText.setText(Integer.toString(currentPrice));
            mBookQuantityViewText.setText(Integer.toString(currentQuantity));
            mBookSupplierPhoneNumberViewText.setText(Integer.toString(currentSupplierPhone));

            Button bookDecreaseButton = findViewById(R.id.decrease_button);
            bookDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseCount(idColumnIndex, currentQuantity);
                }
            });

            Button bookIncreaseButton = findViewById(R.id.increase_button);
            bookIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseCount(idColumnIndex, currentQuantity);
                }
            });

            Button bookDeleteButton = findViewById(R.id.delete_button);
            bookDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            Button phoneButton = findViewById(R.id.phone_button);
            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(currentSupplierPhone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void decreaseCount(int bookID, int bookQuantity) {
        bookQuantity = bookQuantity - 1;
        if (bookQuantity >= 0) {
            updateBook(bookQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();

            Log.d("Log msg", " - bookID " + bookID + " - quantity " + bookQuantity + " , decreaseCount has been called.");
        } else {
            Toast.makeText(this, getString(R.string.quantity_finish_msg), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseCount(int bookID, int bookQuantity) {
        bookQuantity = bookQuantity + 1;
        if (bookQuantity >= 0) {
            updateBook(bookQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();

            Log.d("Log msg", " - bookID " + bookID + " - quantity " + bookQuantity + " , decreaseCount has been called.");
        }
    }


    private void updateBook(int bookQuantity) {
        Log.d("message", "updateBook at ViewActivity");

        if (mCurrentBookUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
