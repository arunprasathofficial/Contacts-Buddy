package com.mad.contactsbuddy.Helpers;

import android.widget.Filter;

import com.mad.contactsbuddy.Controllers.ContactsListAdapter;
import com.mad.contactsbuddy.Models.Contact;

import java.util.ArrayList;

public class ContactSearchFilter extends Filter {

    private ContactsListAdapter contactsListAdapter;
    private ArrayList<Contact> filterList;

    public ContactSearchFilter(ContactsListAdapter contactsListAdapter, ArrayList<Contact> filterList) {
        this.contactsListAdapter = contactsListAdapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults =new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<Contact> contactsList = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getName().toUpperCase().contains(charSequence)) {
                    contactsList.add(filterList.get(i));
                }
            }
            filterResults.count = contactsList.size();
            filterResults.values = contactsList;
        } else {
            filterResults.count = filterList.size();
            filterResults.values = filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        contactsListAdapter.contactList = (ArrayList<Contact>) filterResults.values;
        contactsListAdapter.notifyDataSetChanged();
    }
}
