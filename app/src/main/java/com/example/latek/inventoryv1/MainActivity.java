package com.example.latek.inventoryv1;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.latek.inventoryv1.data.BookContract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       fab.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
        BookCursorAdapter mCursorAdapter;
        private static final int BOOK_LOADER = 0;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            TextView emptyView = findViewById(R.id.empty_text_view);
            ListView bookListView = (ListView) findViewById(R.id.list);
            bookListView.setEmptyView(emptyView);
            BookCursorAdapter adapter = new BookCursorAdapter(this, null);
            bookListView.setAdapter(adapter);
            bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
                    Intent intent = new Intent(com.example.latek.inventoryv1.MainActivity.BookActivity.this, ViewActivity.class);
                    Uri currentProdcuttUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                    intent.setData(currentProdcuttUri);
                    startActivity(intent);
                }
            });
            getSupportLoaderManager().initLoader(BOOK_LOADER, null,this);


        }
        public void bookSaleCount(int productID, int productQuantity) {
            productQuantity = productQuantity - 1;
            if (productQuantity >= 0) {
                ContentValues values = new ContentValues();
                values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, productQuantity);
                Uri updateUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, productID);
                int rowsAffected = getContentResolver().update(updateUri, values, null, null);
                Toast.makeText(this, "Quantity was change", Toast.LENGTH_SHORT).show();

                Log.d("Log msg", "rowsAffected " + rowsAffected + " - productID " + productID + " - quantity " + productQuantity + " , decreaseCount has been called.");
            } else {
                Toast.makeText(this, "Product was finish :( , buy another Product", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args){
            String[] projection = {
                    BookContract.BookEntry._ID,
                    BookContract.BookEntry.COLUMN_BOOK_NAME,
                    BookContract.BookEntry.COLUMN_BOOK_SUPPLIER};
            return new CursorLoader(this,
                    BookContract.BookEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data){
            mCursorAdapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader){
            mCursorAdapter.swapCursor(null);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete_all_entries:
                    showDeleteConfirmationDialog();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
        private void showDeleteConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteAllProducts();
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
        private void deleteAllProducts() {
            int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
            Toast.makeText(this, rowsDeleted + " " + getString(R.string.deleted_all_books_message), Toast.LENGTH_SHORT).show();

        }
    }

}
