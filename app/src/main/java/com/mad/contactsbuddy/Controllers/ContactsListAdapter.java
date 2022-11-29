package com.mad.contactsbuddy.Controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.contactsbuddy.Views.ContactDetailsActivity;
import com.mad.contactsbuddy.Helpers.ContactSearchFilter;
import com.mad.contactsbuddy.Models.Contact;
import com.mad.contactsbuddy.R;

import java.util.ArrayList;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.HolderContact> implements Filterable {

    private Context context;
    public ArrayList<Contact> contactList, filterList;
    private ContactSearchFilter contactSearchFilter;

    public ContactsListAdapter(Context context, ArrayList<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.filterList = contactList;
    }

    @NonNull
    @Override
    public ContactsListAdapter.HolderContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.contact_view_layout, parent, false);

        return new HolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsListAdapter.HolderContact holder, int position) {

        Contact contact = contactList.get(position);
        String id = contact.getId();
        String name = contact.getName();
        String phone_no = contact.getPhone_no();
        String image = contact.getImage();

        if (image.equals("") || image.equals("null")) {
            holder.contactImage_iv.setImageResource(R.drawable.placeholder);
        } else {
            holder.contactImage_iv.setImageURI(Uri.parse(image));
        }

        holder.contactName_tv.setText(name);
        holder.contactPhone_tv.setText(PhoneNumberUtils.formatNumber(phone_no));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContactDetailsActivity.class);
                intent.putExtra("ID", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        if (contactSearchFilter == null) {
            contactSearchFilter = new ContactSearchFilter(this, filterList);
        }
        return contactSearchFilter;
    }

    class HolderContact extends RecyclerView.ViewHolder {

        ImageView contactImage_iv;
        TextView contactName_tv, contactPhone_tv;

        public HolderContact (@Nullable View itemView) {
            super(itemView);

            contactImage_iv = itemView.findViewById(R.id.contactImage_iv);
            contactName_tv = itemView.findViewById(R.id.contactName_tv);
            contactPhone_tv = itemView.findViewById(R.id.contactPhone_tv);
        }
    }
}
