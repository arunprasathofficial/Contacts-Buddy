package com.mad.contactsbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.contactsbuddy.Controllers.ContactsListAdapter;
import com.mad.contactsbuddy.Helpers.Constants;
import com.mad.contactsbuddy.Helpers.DBHelper;

public class ContactsListActivity extends AppCompatActivity {

    private FloatingActionButton addContact_fab;
    private EditText searchContacts_et;
    private TextView contactsCount_tv;
    private RecyclerView contactsList_rv;

    private DBHelper dbHelper;
    private ContactsListAdapter contactsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_contacts_list);

        addContact_fab = findViewById(R.id.addContact_fab);
        searchContacts_et = findViewById(R.id.searchContacts_et);
        contactsCount_tv = findViewById(R.id.contactsCount_tv);
        contactsList_rv = findViewById(R.id.contactsList_rv);

        dbHelper = new DBHelper(this);

        loadContacts();

        addContact_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsListActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadContacts() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactsList_rv.setLayoutManager(linearLayoutManager);
        contactsListAdapter = new ContactsListAdapter(
                ContactsListActivity.this, dbHelper.getAllContacts(Constants.NAME));

        contactsList_rv.setAdapter(contactsListAdapter);
        contactsCount_tv.setText("Showing All (" + dbHelper.getContactsCount() + ")");
    }

    protected void onResume() {
        super.onResume();
        loadContacts();
    }
}