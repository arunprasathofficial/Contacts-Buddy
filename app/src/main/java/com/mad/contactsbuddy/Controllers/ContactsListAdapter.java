package com.mad.contactsbuddy.Controllers;

import android.content.Context;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.contactsbuddy.Models.Contact;
import com.mad.contactsbuddy.R;

import java.util.ArrayList;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.HolderContact> {

    private Context context;
    private ArrayList<Contact> contactList;

    public ContactsListAdapter(Context context, ArrayList<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
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

        holder.contactImage_iv.setImageURI(Uri.parse(image));
        holder.contactName_tv.setText(name);
        holder.contactPhone_tv.setText(PhoneNumberUtils.formatNumber(phone_no));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
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
