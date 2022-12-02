package com.mad.contactsbuddy.Controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.contactsbuddy.Views.ContactDetailsActivity;
import com.mad.contactsbuddy.Helpers.ContactSearchFilter;
import com.mad.contactsbuddy.Models.Contact;
import com.mad.contactsbuddy.R;

import java.util.ArrayList;
import java.util.Random;

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

        char firstLetter = name.charAt(0);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        if (image.equals("") || image.equals("null")) {
            holder.contactImage_iv.setVisibility(View.INVISIBLE);

            holder.badgeLetter_tv.setText("" + firstLetter);
            holder.contactBadge_iv.setColorFilter(color);

            holder.badgeLetter_tv.setVisibility(View.VISIBLE);
            holder.contactBadge_iv.setVisibility(View.VISIBLE);

        } else {
            holder.badgeLetter_tv.setVisibility(View.INVISIBLE);
            holder.contactBadge_iv.setVisibility(View.INVISIBLE);

            holder.contactImage_iv.setImageURI(Uri.parse(image));
            holder.contactImage_iv.setVisibility(View.VISIBLE);
        }

        holder.contactName_tv.setText(name);
        holder.contactPhone_tv.setText(PhoneNumberUtils.formatNumber(phone_no));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContactDetailsActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("COLOR", color);
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

        ImageView contactImage_iv, contactBadge_iv;
        TextView contactName_tv, contactPhone_tv, badgeLetter_tv;

        public HolderContact (@Nullable View itemView) {
            super(itemView);

            contactImage_iv = itemView.findViewById(R.id.contactImage_iv);
            contactName_tv = itemView.findViewById(R.id.contactName_tv);
            contactPhone_tv = itemView.findViewById(R.id.contactPhone_tv);
            contactBadge_iv = itemView.findViewById(R.id.contactBadge_iv);
            badgeLetter_tv = itemView.findViewById(R.id.badgeLetter_tv);
        }
    }
}
