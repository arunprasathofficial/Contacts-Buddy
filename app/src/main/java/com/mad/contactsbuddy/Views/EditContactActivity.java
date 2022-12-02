package com.mad.contactsbuddy.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.contactsbuddy.Helpers.Constants;
import com.mad.contactsbuddy.Helpers.DBHelper;
import com.mad.contactsbuddy.R;
import com.sdsmdg.tastytoast.TastyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class EditContactActivity extends AppCompatActivity {

    private ImageView back_btn, contactBadge_iv;
    private CircularImageView contactImage_iv;
    private TextView changeImage_tv, badgeLetter_tv;
    private EditText name_et, phone_number_et, email_et;
    private FloatingActionButton saveContact_fab;

    private DBHelper dbHelper;

    private String id, name, phone_no, email, image;
    private int color;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_edit_contact);

        back_btn = findViewById(R.id.back_btn);
        contactBadge_iv = findViewById(R.id.contactBadge_iv);
        contactImage_iv = findViewById(R.id.contactImage_iv);
        changeImage_tv = findViewById(R.id.changeImage_tv);
        badgeLetter_tv = findViewById(R.id.badgeLetter_tv);
        name_et = findViewById(R.id.name_et);
        phone_number_et = findViewById(R.id.phone_number_et);
        email_et = findViewById(R.id.email_et);
        saveContact_fab = findViewById(R.id.saveContact_fab);

        id = getIntent().getStringExtra("ID");
        color =  getIntent().getIntExtra("COLOR", 0);

        dbHelper = new DBHelper(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        loadContactDetails();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changeImage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhotoDialog();
            }
        });

        saveContact_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadContactDetails() {
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME +
                " WHERE " + Constants.ID + "=" + id;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String id = "" + cursor.getInt(cursor.getColumnIndexOrThrow(Constants.ID));
                name = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.NAME));
                phone_no = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.PHONE_NO));
                email = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.EMAIL));
                image = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.IMAGE));

                char firstLetter = name.charAt(0);

                name_et.setText(name);
                phone_number_et.setText(phone_no);
                email_et.setText(email);

                if (image.equals("") || image.equals("null")) {
                    contactImage_iv.setVisibility(View.INVISIBLE);

                    changeImage_tv.setText("Add Photo");

                    badgeLetter_tv.setText("" + firstLetter);
                    contactBadge_iv.setColorFilter(color);

                    badgeLetter_tv.setVisibility(View.VISIBLE);
                    contactBadge_iv.setVisibility(View.VISIBLE);

                } else {
                    badgeLetter_tv.setVisibility(View.INVISIBLE);
                    contactBadge_iv.setVisibility(View.INVISIBLE);

                    changeImage_tv.setText("Change Photo");

                    contactImage_iv.setImageURI(Uri.parse(image));
                    contactImage_iv.setVisibility(View.VISIBLE);
                }

            } while (cursor.moveToNext());
        }
    }

    private void addPhotoDialog() {
        String[] options = {"Take Photo", "Choose Photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (ContextCompat.checkSelfPermission(EditContactActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(EditContactActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                addImageFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (ContextCompat.checkSelfPermission(EditContactActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                addImageFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                }).show();
    }

    private void addImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void addImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                addImageFromCamera();
            } else {
                TastyToast.makeText(this, "Please grant the camera permission!", TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
            }
        }

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addImageFromGallery();
            } else {
                TastyToast.makeText(this, "Please grant the storage permission!", TastyToast.LENGTH_LONG, TastyToast.WARNING).show();
            }
        }
    }

    private void saveToDirectory(String srcDir, String desDir) throws IOException {
        File src = new File(srcDir);
        File des = new File(desDir, src.getName());

        FileChannel source = null;
        FileChannel destination = null;

        try {
            if (!des.getParentFile().exists()) {
                des.mkdirs();
            }
            if (!des.exists()) {
                des.createNewFile();
            }

            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(des).getChannel();
            destination.transferFrom(source, 0, source.size());

            imageUri = Uri.parse(des.getPath());

        } catch (Exception e) {
            TastyToast.makeText(this, "" + e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR).show();

        } finally {
            if (source != null) {
                source.close();

            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    if (resultUri != null) {
                        imageUri = resultUri;
                        contactImage_iv.setImageURI(resultUri);

                        contactBadge_iv.setVisibility(View.INVISIBLE);
                        badgeLetter_tv.setVisibility(View.INVISIBLE);
                        contactImage_iv.setVisibility(View.VISIBLE);

                        try {
                            saveToDirectory("" + imageUri.getPath(), "" + getDir("contacts_images", MODE_PRIVATE));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    TastyToast.makeText(this, "" + error, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }
            }
        }
    }

    private void validateData() {
        String phoneRegex = "\\d{10}";

        name = name_et.getText().toString().trim();
        phone_no = phone_number_et.getText().toString().trim();
        email = email_et.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            TastyToast.makeText(this, "Please enter contact name!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            return;

        }

        if (TextUtils.isEmpty(phone_no)) {
            TastyToast.makeText(this, "Please enter your phone number!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            return;

        } else if (!phone_no.matches(phoneRegex)) {
            TastyToast.makeText(this, "invalid phone number format!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            return;

        }

        if (TextUtils.isEmpty(email)) {
            TastyToast.makeText(this, "Please enter email!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            TastyToast.makeText(this, "invalid email format!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            return;

        }

        saveContact();
    }

    private void saveContact() {
        String timestamp = "" + System.currentTimeMillis();

        if (imageUri == null) {
            dbHelper.updateRecord(
                    "" + id,
                    "" + name,
                    "" + phone_no,
                    "" + email,
                    "" + image,
                    "" + timestamp
            );

        } else {
            dbHelper.updateRecord(
                    "" + id,
                    "" + name,
                    "" + phone_no,
                    "" + email,
                    "" + imageUri,
                    "" + timestamp
            );
        }
        TastyToast.makeText(this, "" + name + " updated successfully!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
        onBackPressed();
    }
}