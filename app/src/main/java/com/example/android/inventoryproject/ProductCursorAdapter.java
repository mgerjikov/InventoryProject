package com.example.android.inventoryproject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryproject.data.ProductContract;

/**
 * Created by Martin on 17.7.2017 г..
 */

public class ProductCursorAdapter extends CursorAdapter {

    Context mContext;

    public ProductCursorAdapter( Context context, Cursor cursor, boolean autoReQuery){
        super(context,cursor,autoReQuery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate and return a new view without binding any data
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }

    /**
     * This method bind the product data (in the current row pointed by the cursor) to the given
     * list item layout
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find product name field, so it can be populated when inflated
        TextView productName = (TextView) view.findViewById(R.id.text_view_name);
        // Find product price field, so it can be populated when inflated
        TextView productPrice = (TextView) view.findViewById(R.id.text_view_price);
        // Find product quantity field, so it can be populated when inflated
        TextView productQuantity = (TextView) view.findViewById(R.id.text_view_quantity);
        ImageView productImage = (ImageView) view.findViewById(R.id.product_image);

        // Extract values from the Cursor object
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_NAME));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_QUANTITY));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_IMAGE));
        if (imageBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
            productImage.setImageBitmap(bitmap);
        }
        final Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,
                cursor.getInt(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry._ID)));

        // Populate the text views with values extracted from the Cursor object
        productName.setText(name);
        productPrice.setText(context.getString(R.string.label_price)+ " " + price);
        productQuantity.setText(quantity + " " + context.getString(R.string.label_quantity));

        // Find sale button
        Button saleButton = (Button) view.findViewById(R.id.button_sale);
        // Set listener to the button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if quantity in stock is higher than zero
                if (quantity > 0){
                    // Assign a new quantity value of minus one to represent one item sold
                    int newQuantity = quantity - 1;
                    // Create and initialize a new Content Values object with the new quantity
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, newQuantity);
                    // Update the database
                    context.getContentResolver().update(uri,values,null,null);
                } else {
                    // Notify the user that quantity is less than zero and cannot be updated
                    Toast.makeText(context,context.getString(R.string.product_out_of_stock_toast),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
