package com.example.android.inventoryproject;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryproject.data.ProductContract;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter mProductCursorAdapter;
    private static final int URI_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference to the ListView
        initializeListView();

        // Start LoaderManager
        getLoaderManager().initLoader(URI_LOADER,null,this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case URI_LOADER:
                // Defining projection for the Cursor so that it contains all rows from the table
                String projection[] = {
                        ProductContract.ProductEntry._ID,
                        ProductContract.ProductEntry.COLUMN_NAME,
                        ProductContract.ProductEntry.COLUMN_PRICE,
                        ProductContract.ProductEntry.COLUMN_QUANTITY,
                        ProductContract.ProductEntry.COLUMN_IMAGE // TOVA GO NQMA ?? ZASHTO
                };
                // Define sort order
                String sortOrder =
                        ProductContract.ProductEntry._ID + " DESC ";
                // Return cursor loader
                return new CursorLoader(
                        this,
                        ProductContract.ProductEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        try {
            mProductCursorAdapter.swapCursor(data);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }

    private void initializeListView(){
        // Find the ListView
        ListView listView = (ListView) findViewById(R.id.list_view);
        // Define empty view so a specific layout can be displayed when
        // there is no data to be shown in the UI
        View emptyView = findViewById(R.id.empty_state_view);
        // Attach the empty view to the list view when there is no data to be shown
        listView.setEmptyView(emptyView);
        // Initialize the Cursor Adapter
        mProductCursorAdapter = new ProductCursorAdapter(this,null,false);
        // Attach the adapter to the list view
        listView.setAdapter(mProductCursorAdapter);
        // Set Click Listener to the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from the res/menu/menu_main_activity.xml file
        // This adds the given menu to the app bar
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

