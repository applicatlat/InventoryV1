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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.latek.inventoryv1.data.BookContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_INVENTORY_LOADER = 0;
    public String mSupplierName = "Nonyet";
    private Uri mCurrentBookUri;
    private EditText mBookNameEditText;
    private EditText mBookPriceEditText;
    private EditText mBookQuantityEditText;
    private EditText mBookSupplierPhoneNumberEditText;
    private EditText mBookSupplierName;
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            Log.d("message", "onTouch");
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Log.d("message", "onCreate");
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        mBookNameEditText = findViewById(R.id.book_name_edit_text);
        mBookPriceEditText = findViewById(R.id.book_price_edit_text);
        mBookQuantityEditText = findViewById(R.id.book_quantity_edit_text);
        mBookSupplierPhoneNumberEditText = findViewById(R.id.book_supplier_phone_number_edit_text);
        mBookSupplierName = findViewById(R.id.book_name_supplier);
        mBookNameEditText.setOnTouchListener(mTouchListener);
        mBookPriceEditText.setOnTouchListener(mTouchListener);
        mBookQuantityEditText.setOnTouchListener(mTouchListener);
        mBookSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);
    }

    private void saveBook() {
        String bookNameString = mBookNameEditText.getText().toString().trim();
        String bookPriceString = mBookPriceEditText.getText().toString().trim();
        String bookQuantityString = mBookQuantityEditText.getText().toString().trim();
        String bookNameSupplier = mBookSupplierName.getText().toString().trim();
        String bookSupplierPhoneNumberString = mBookSupplierPhoneNumberEditText.getText().toString().trim();
        if (mCurrentBookUri == null) {
            if (TextUtils.isEmpty(bookNameString)) {
                Toast.makeText(this, getString(R.string.book_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookNameSupplier)) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookNameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, bookPriceString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantityString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER, bookNameSupplier);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER, bookSupplierPhoneNumberString);

            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{

            if (TextUtils.isEmpty(bookNameString)) {
                Toast.makeText(this, getString(R.string.book_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookNameSupplier)) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(bookSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, bookNameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, bookPriceString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantityString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER, mSupplierName);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER, bookSupplierPhoneNumberString);


            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Log.d("message", "open Editor Activity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
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
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PHONE_NUMBER);
            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);
            mBookNameEditText.setText(currentName);
            mBookPriceEditText.setText(Integer.toString(currentPrice));
            mBookQuantityEditText.setText(Integer.toString(currentQuantity));
            mBookSupplierPhoneNumberEditText.setText(Integer.toString(currentSupplierPhone));
            mBookSupplierName.setText(Integer.toString(currentSupplierName));
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookNameEditText.setText("");
        mBookPriceEditText.setText("");
        mBookQuantityEditText.setText("");
        mBookSupplierPhoneNumberEditText.setText("");
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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
