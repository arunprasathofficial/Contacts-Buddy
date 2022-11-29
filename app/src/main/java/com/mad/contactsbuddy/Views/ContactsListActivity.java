package com.mad.contactsbuddy.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.contactsbuddy.Controllers.ContactsListAdapter;
import com.mad.contactsbuddy.Helpers.Constants;
import com.mad.contactsbuddy.Helpers.DBHelper;
import com.mad.contactsbuddy.R;

public class ContactsListActivity extends AppCompatActivity {

    private FloatingActionButton addContact_fab;
    private EditText searchContacts_et;
    private TextView contactsCount_tv;
    private RecyclerView contactsList_rv;
    private LinearLayout noContactsFoundMsg_lt;
    private ImageView sortList_iv;

    private DBHelper dbHelper;

    private ContactsListAdapter contactsListAdapter;

    private String orderByLatest = Constants.CREATED_ON + " DESC";
    private String orderByOldest = Constants.CREATED_ON + " ASC";
    private String orderByNameAsc = Constants.NAME + " ASC";
    private String orderByNameDesc = Constants.NAME + " DESC";
    private String currentOrderByStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_contacts_list);

        addContact_fab = findViewById(R.id.addContact_fab);
        searchContacts_et = findViewById(R.id.searchContacts_et);
        contactsCount_tv = findViewById(R.id.contactsCount_tv);
        contactsList_rv = findViewById(R.id.contactsList_rv);
        noContactsFoundMsg_lt = findViewById(R.id.noContactsFoundMsg_lt);
        sortList_iv = findViewById(R.id.sortList_iv);

        dbHelper = new DBHelper(this);

        currentOrderByStatus = orderByNameAsc;

        loadContacts(currentOrderByStatus);

        sortList_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortList();
            }
        });

        searchContacts_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence != null && charSequence.length() > 0) {
                        contactsListAdapter.getFilter().filter(charSequence);
                        if (contactsListAdapter.getItemCount() == 0) {
                            contactsList_rv.setVisibility(View.GONE);
                            noContactsFoundMsg_lt.setVisibility(View.VISIBLE);
                        } else {
                            noContactsFoundMsg_lt.setVisibility(View.GONE);
                            contactsList_rv.setVisibility(View.VISIBLE);
                        }
                        contactsCount_tv.setText("Search Result: " + charSequence.toString() + " (" + contactsListAdapter.getItemCount() + " founded)");
                    } else {
                        loadContacts(currentOrderByStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addContact_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsListActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadContacts(String orderBy) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactsList_rv.setLayoutManager(linearLayoutManager);
        contactsListAdapter = new ContactsListAdapter(
                ContactsListActivity.this, dbHelper.getAllContacts(orderBy));

        contactsList_rv.setAdapter(contactsListAdapter);
        contactsCount_tv.setText("Showing All (" + dbHelper.getContactsCount() + ")");
    }

    private void sortList() {
        String[] options = {"Name (Ascending)", "Name (Descending)", "Latest", "Oldest"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ContactsListActivity.this);
        builder.setTitle("Sort By")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            loadContacts(orderByNameAsc);

                        } else if (i == 1) {
                            loadContacts(orderByNameDesc);

                        } else if (i == 2) {
                            loadContacts(orderByLatest);

                        } else if (i == 3) {
                            loadContacts(orderByOldest);
                        }
                    }
                }).show();
    }

    protected void onResume() {
        super.onResume();
        loadContacts(currentOrderByStatus);
    }
}