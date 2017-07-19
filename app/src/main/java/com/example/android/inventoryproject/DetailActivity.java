package com.example.android.inventoryproject;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryproject.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Martin on 17.7.2017 г..
 */

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Final for the image intent request code
    private final static int SELECT_PHOTO = 200;
    // COnstant to be used when asking for storage read
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 666;
    private static final int IMAGE_PICK = 1;
    // Constant field for email intent
    private static final String URI_EMAIL = "mailto:";
    // Uri loader
    private static final int URI_LOADER = 0;
    // EditText field to enter product name
    private EditText mNameEditText;
    // EditText field to enter product Large quantity
    private EditText mQuantityEditText;
    // EditText field to enter product price
    private EditText mPriceEditText;
    // TextView to show the current product quantity
    private TextView mQuantityTextView;
    //Product information variables
    private String mProductName;
    private int mProductQuantity;
    // Buttons that will be used to modify quantity
    private Button mIncreaseQuantityByOneButton; // Increase by one
    private Button mDecreaseQuantityByOneButton; // Decrease by one
    private Button mIncreaseQuantityLargeButton; // Increase by many (n)
    private Button mDecreaseQuantityLargeButton; // Decrease by many (m)
    // Button to select image
    private Button mSelectImageButton;
    // ImageView to display selected image
    private ImageView mProductImageView;
    // Bitmap to store/retrieve from the database
    private Bitmap mProductBitmap;
    // button to order more quantity from supplier
    private Button mOrderButton;
    // Uri received with the Intent from MainActivity
    private Uri mProductUri;

    // Boolean to check whether or not the register has changed
    private boolean mProductHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    /**
     * Helper method for converting from byte array to bitmap
     *
     * @param image BLOB from the database converted to a Bitmap
     *              in order to be displayed in the UI
     * @return
     */
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 70, image.length);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Receive Uri data from Intent
        Intent intent = getIntent();
        mProductUri = intent.getData();

        // Check if Uri is null or not
        if (mProductUri == null) {
            // If its null that means a new product
            setTitle(getString(R.string.add_new_product));
            // Invalidate options menu (delete button), since there is nothing to delete
            invalidateOptionsMenu();
        } else {
            // If its not null that means a product register will be edited
            setTitle(getString(R.string.edit_existing_product));
            // Kick off LoaderManager
            getLoaderManager().initLoader(URI_LOADER, null, this);
        }

        // Find all relevant views that we will need to read or show user input onto
        initialiseViews();

        // Set  OnTouchListener to all relevant vies
        setOnTouchListener();
    }

    private void initialiseViews() {
        // Check if there is an existing product to make the button visible so
        // the user can order more from it
        if (mProductUri != null) {
            // Initialize button to order more from supplier
            mOrderButton = (Button) findViewById(R.id.button_order_from_supplier);
            // Make button visible
            mOrderButton.setVisibility(View.VISIBLE);
            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.setType("text/plain");
                    // Defining supplier's email.
                    intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.product_supplier_email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, mProductName);
                    startActivity(Intent.createChooser(intent, "Send Mail..."));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }

        // Initialize EditText's
        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);

        // Initialize TextView
        mQuantityTextView = (TextView) findViewById(R.id.text_view_quantity_final);

        // Initialize increase button and set listener to it
        mIncreaseQuantityByOneButton = (Button) findViewById(R.id.button_increase_one);
        mIncreaseQuantityByOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add one to product
                mProductQuantity++;
                // Update UI
                mQuantityTextView.setText(String.valueOf(mProductQuantity));
            }
        });

        // Initialize decrease button and set listener to it
        mDecreaseQuantityByOneButton = (Button) findViewById(R.id.button_decrease_one);
        mDecreaseQuantityByOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the product quantity is higher thatn 0
                if (mProductQuantity > 0) {
                    // If its higher than 0 we can decrease by one
                    mProductQuantity--;
                    // Update the UI
                    mQuantityTextView.setText(String.valueOf(mProductQuantity));
                } else {
                    // If it's not higher than 0 notify the user
                    Toast.makeText(DetailActivity.this, getString(R.string.invalid_product_quantity_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialise increase large quantity Button and set click listener
        mIncreaseQuantityLargeButton = (Button) findViewById(R.id.button_increase_n);
        mIncreaseQuantityLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if quantity edit text is empty and higher than zero
                if (!TextUtils.isEmpty(mQuantityEditText.getText()) && Integer.valueOf(mQuantityEditText.getText().toString()) > 0) {
                    // Add the quantity in the edit text to the variable keeping track of product stock quantity
                    mProductQuantity += Integer.valueOf(mQuantityEditText.getText().toString());
                    // Update the UI
                    mQuantityTextView.setText(String.valueOf(mProductQuantity));
                } else {
                    // Show toast asking user to fill out edit text
                    Toast.makeText(DetailActivity.this, getString(R.string.invalid_product_quantity_modifier_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialise decrease large quantity Button and set click listener
        mDecreaseQuantityLargeButton = (Button) findViewById(R.id.button_decrease_n);
        mDecreaseQuantityLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if quantity edit text is empty and higher than zero
                if (!TextUtils.isEmpty(mQuantityEditText.getText()) && Integer.valueOf(mQuantityEditText.getText().toString()) > 0) {
                    int newQuantity = mProductQuantity - Integer.valueOf(mQuantityEditText.getText().toString());
                    if (newQuantity < 0) {
                        Toast.makeText(DetailActivity.this, getString(R.string.invalid_product_quantity_toast), Toast.LENGTH_SHORT).show();
                    } else {
                        // Decrease the quantity in the edit text to the variable keeping track of product stock quantity
                        mProductQuantity -= Integer.valueOf(mQuantityEditText.getText().toString());
                        // Update the UI
                        mQuantityTextView.setText(String.valueOf(mProductQuantity));
                    }
                } else {
                    // Show toast asking user to fill out edit text
                    Toast.makeText(DetailActivity.this, getString(R.string.invalid_product_quantity_modifier_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initializethe image view to show preview of the product image
        mProductImageView = (ImageView) findViewById(R.id.detail_image);

        // Initialize button to select image for the product
        mSelectImageButton = (Button) findViewById(R.id.button_select_image);
        mSelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageSelector();

                // Ask for user permission to explore image gallery
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // If not authorized, ask for authorization
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                // Do something
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                        return;
                    }
                    // If permission granted, create a new intent and prompt
                    // user to pick image from Gallery
                    Intent getIntent = new Intent(Intent.ACTION_PICK);
                    getIntent.setType("image/*");
                    startActivityForResult(getIntent, SELECT_PHOTO);
                }
            }
        });
    }

    // Handle the result of the image chooser intent launch
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if request code, result and intent match the image chooser
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            // Get image Uri
            Uri selectedImage = data.getData();
            Log.v("DetailActivity", "Uri: " + selectedImage.toString());
            // Get image file path
            String[] filePatchColumn = {MediaStore.Images.Media.DATA};
            // Create cursor object and query image
            Cursor cursor = getContentResolver().query(selectedImage, filePatchColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePatchColumn[0]);
            // Get image path from cursor
            String picturePath = cursor.getString(columnIndex);
            // Close cursor to avoid memory leaks
            cursor.close();
            // Set the image to a Bitmap object

            mProductBitmap = BitmapFactory.decodeFile(picturePath);

            mProductBitmap = getBitmapFromUri(selectedImage);
            // Set Bitmap to the image view
            mProductImageView = (ImageView) findViewById(R.id.detail_image);
            mProductImageView.setImageBitmap(mProductBitmap);
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty()) {
            return null;
        }

        // Get the dimensions of the View
        mProductImageView = (ImageView) findViewById(R.id.detail_image);
        int targetW = mProductImageView.getWidth();
        int targetH = mProductImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("AddActivity", "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("AddActivity", "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main_activity.xml file
        // This adds the given menu to the app bar
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item
        if (mProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Add" menu option
            case R.id.action_add:
                if (mProductHasChanged) {
                    // Call save/edit method
                    saveProduct();
                } else {
                    // Show a toast message when no product is updated nor created
                    Toast.makeText(this, getString(R.string.no_changes_toast), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_delete:
                // Call delete confirmation dialog
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If product hasn't changed, continue with navigation up to parent activity
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                } else {
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "Discard" button , navigate to parent activity
                                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                                }
                            };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle the back button pressed on the device
    @Override
    public void onBackPressed() {
        // If the product hasn't changed , continue with closing and back to parent activity
        if (!mProductHasChanged) {
            super.onBackPressed();
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user
        // Create a click listener to handle the user confirming that changes should be discarded
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the current activity without adding/saving
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Add new product or commit changes to existing one
    private void saveProduct() {
        // Define whether or not EditText fields are empty
        boolean nameIsEmpty = checkFieldEmpty(mNameEditText.getText().toString().trim());
        boolean priceIsEmpty = checkFieldEmpty(mPriceEditText.getText().toString().trim());

        // Check if name, quantity or price are null and inform the user to change it/them to valid value(s)
        if (nameIsEmpty) {
            Toast.makeText(this, getString(R.string.invalid_product_name_add_toast), Toast.LENGTH_SHORT).show();
        } else if (mProductQuantity <= 0) {
            Toast.makeText(this, getString(R.string.invalid_product_quantity_add_toast), Toast.LENGTH_SHORT).show();
        } else if (priceIsEmpty) {
            Toast.makeText(this, getString(R.string.invalid_product_price_add_toast), Toast.LENGTH_SHORT).show();
        } else if (mProductBitmap == null) {
            Toast.makeText(this, getString(R.string.invalid_product_image_add_toast), Toast.LENGTH_SHORT).show();
        } else {
            // Assuming that all fields are valid, pass the name edit text
            // value to a String for easier manipulation
            String name = mNameEditText.getText().toString().trim();
            // Pass the price edit text value to a double for easier manipulation
            double price = Double.parseDouble(mPriceEditText.getText().toString().trim());

            // Create new Content Values and put the product info into them
            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.COLUMN_NAME, name);
            values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, mProductQuantity);
            values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);

            if (mProductBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                boolean a = mProductBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                values.put(ProductContract.ProductEntry.COLUMN_IMAGE, byteArray);
            }

            // Check if Uri is valid to determine whether is new product insertion or existing product update
            if (mProductUri == null) {
                // If Uri is null then we're inserting a new product
                Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

                if (newUri == null) {
                    // Notify user for the successful product insertion
                    Toast.makeText(this, getString(R.string.failed_insert_toast),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.successful_insert_toast),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING product, so update the product with content URI: mProductUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mProductUri will already identify the correct row in the database that
                // we want to modify.
                // If Uri is not null then we're updating an existing product
                int rowsAffected = getContentResolver().update(mProductUri, values, null, null);

                // Notify user depending on whether or not the update was successful
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update
                    Toast.makeText(this, getString(R.string.failed_update_toast), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful
                    Toast.makeText(this, getString(R.string.successful_update_toast),
                            Toast.LENGTH_SHORT).show();
                }

            }
            finish();
        }
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    /**
     * Helper method to define if any of the EditText fields are empty or contain invalid inputs
     */
    private boolean checkFieldEmpty(String string) {
        return TextUtils.isEmpty(string) || string.equals(".");
    }

    /**
     * Perform the deletion of the product in the database
     */
    private void deleteProduct() {
        if (mProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mProductUri, null, null);
            // Notify user whether or not the delete was successful
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the deletion
                Toast.makeText(this, getString(R.string.failed_delete_toast), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and notify the user
                Toast.makeText(this, getString(R.string.successful_delete_toast), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Aks for user confirmation before deleting product in database
     */
    private void showDeleteConfirmationDialog() {
        // Create a AlertDialog.Builder with confirmation message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.prompt_delete_product));
        // Set onClick Listeners for positive and negative options
        // Positive Option -> Yes! Delete!
        builder.setPositiveButton(getString(R.string.prompt_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Call deleteProduct method to delete the product
                deleteProduct();
                finish();
            }
        });
        // Negative option -> Cancel please
        builder.setNegativeButton(getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Dismiss the dialog and continue editing the product
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Ask for user confirmation to exit activity before saving
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder with confirmation message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.prompt_leave_without_save));
        // Set onClick Listeners for positive and negative options
        // Positive Option -> Yes! Leave!
        builder.setPositiveButton(getString(R.string.prompt_yes), discardButtonClickListener);
        // Negative option -> Cancel! I want to stay
        builder.setNegativeButton(getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setOnTouchListener() {
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mIncreaseQuantityByOneButton.setOnTouchListener(mTouchListener);
        mDecreaseQuantityByOneButton.setOnTouchListener(mTouchListener);
        mIncreaseQuantityLargeButton.setOnTouchListener(mTouchListener);
        mDecreaseQuantityLargeButton.setOnTouchListener(mTouchListener);
        mSelectImageButton.setOnTouchListener(mTouchListener);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_IMAGE
        };

        switch (id) {
            case URI_LOADER:
                return new CursorLoader(
                        this,
                        mProductUri,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            mProductName = data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME));
            mNameEditText = (EditText) findViewById(R.id.edit_text_name);
            mNameEditText.setText(mProductName);

            mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
            mPriceEditText.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE)));

            mQuantityTextView = (TextView) findViewById(R.id.text_view_quantity_final);
            mProductQuantity = data.getInt(data.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY));
            mQuantityTextView.setText(String.valueOf(mProductQuantity));

            byte[] bytesArray = data.getBlob(data.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_IMAGE));
            if (bytesArray != null) {
                mProductBitmap = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.length);
                mProductImageView = (ImageView) findViewById(R.id.detail_image);
                mProductImageView.setImageBitmap(mProductBitmap);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.getText().clear();
        mQuantityEditText.getText().clear();
        mQuantityTextView.setText("");
    }
}
