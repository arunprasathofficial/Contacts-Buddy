package com.mad.contactsbuddy.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.contactsbuddy.Helpers.Constants;
import com.mad.contactsbuddy.Helpers.DBHelper;
import com.mad.contactsbuddy.R;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactDetailsActivity extends AppCompatActivity {

    private ImageView back_btn, phone_iv, message_iv, email_iv, contactBadge_iv;
    private CircularImageView contactImage_iv;
    private TextView contactName_tv, phone_number_tv, email_tv, dates_tv, badgeLetter_tv;
    private FloatingActionButton removeContact_fab, editContact_fab;

    private String id, name, phone_no, email, image, created_on, last_updated_on;
    private int color;

    private DBHelper dbHelper;

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_contact_details);

        back_btn = findViewById(R.id.back_btn);
        phone_iv = findViewById(R.id.phone_iv);
        message_iv = findViewById(R.id.message_iv);
        email_iv = findViewById(R.id.email_iv);
        contactBadge_iv = findViewById(R.id.contactBadge_iv);
        contactImage_iv = findViewById(R.id.contactImage_iv);
        contactName_tv = findViewById(R.id.contactName_tv);
        phone_number_tv = findViewById(R.id.phone_number_tv);
        email_tv = findViewById(R.id.email_tv);
        dates_tv = findViewById(R.id.dates_tv);
        badgeLetter_tv = findViewById(R.id.badgeLetter_tv);
        removeContact_fab = findViewById(R.id.removeContact_fab);
        editContact_fab = findViewById(R.id.editContact_fab);

        id = getIntent().getStringExtra("ID");
        color =  getIntent().getIntExtra("COLOR", 0);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        dbHelper = new DBHelper(this);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        phone_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone_no))));
            }
        });

        message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + Uri.encode(phone_no))));
            }
        });

        email_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(email))));
            }
        });

        editContact_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactDetailsActivity.this, EditContactActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("COLOR", color);
                startActivity(intent);
            }
        });

        removeContact_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContact();
            }
        });

        loadContactDetails();
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
                created_on = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.CREATED_ON));
                last_updated_on = "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.LAST_UPDATED_ON));

                String date_created = simpleDateFormat.format(new Date(Long.parseLong(created_on)));
                String date_last_updated = simpleDateFormat.format(new Date(Long.parseLong(last_updated_on)));
                char firstLetter = name.charAt(0);

                contactName_tv.setText(name);
                phone_number_tv.setText(PhoneNumberUtils.formatNumber(phone_no));
                email_tv.setText(email);
                dates_tv.setText("Created on: " + date_created + "\n Last Updated on: " + date_last_updated);

                if (image.equals("") || image.equals("null")) {
                    contactImage_iv.setVisibility(View.INVISIBLE);

                    badgeLetter_tv.setText("" + firstLetter);
                    contactBadge_iv.setColorFilter(color);

                    badgeLetter_tv.setVisibility(View.VISIBLE);
                    contactBadge_iv.setVisibility(View.VISIBLE);

                } else {
                    badgeLetter_tv.setVisibility(View.INVISIBLE);
                    contactBadge_iv.setVisibility(View.INVISIBLE);

                    contactImage_iv.setImageURI(Uri.parse(image));
                    contactImage_iv.setVisibility(View.VISIBLE);
                }

            } while (cursor.moveToNext());
        }
    }

    private void deleteContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailsActivity.this);
        builder.setTitle("Delete")
                .setMessage("Are you sure, do you want to delete the contact '" + name + "' ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbHelper.deleteRecord(id);
                        TastyToast.makeText(ContactDetailsActivity.this, "" + name + " deleted from the list successfully!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                        onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    protected void onResume() {
        super.onResume();
        loadContactDetails();
    }
}