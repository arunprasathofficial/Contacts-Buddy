package com.mad.contactsbuddy.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mad.contactsbuddy.Models.Contact;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insertRecord(String name, String phone_no, String email, String image,
                             String created_on, String last_updated_on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.NAME, name);
        values.put(Constants.PHONE_NO, phone_no);
        values.put(Constants.EMAIL, email);
        values.put(Constants.IMAGE, image);
        values.put(Constants.CREATED_ON, created_on);
        values.put(Constants.LAST_UPDATED_ON, last_updated_on);

        long result = db.insert(Constants.TABLE_NAME, null, values);

        db.close();

        return result;
    }

    public ArrayList<Contact> getAllContacts (String orderBy) {
        ArrayList<Contact> contactsList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        "" + cursor.getInt(cursor.getColumnIndexOrThrow(Constants.ID)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.NAME)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.PHONE_NO)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.EMAIL)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.IMAGE)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.CREATED_ON)),
                        "" + cursor.getString(cursor.getColumnIndexOrThrow(Constants.LAST_UPDATED_ON)));

                contactsList.add(contact);

            } while (cursor.moveToNext());
        }

        db.close();

        return contactsList;
    }

    public void updateRecord(String id, String name, String phone_no, String email, String image, String last_updated_on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.NAME, name);
        values.put(Constants.PHONE_NO, phone_no);
        values.put(Constants.EMAIL, email);
        values.put(Constants.IMAGE, image);
        values.put(Constants.LAST_UPDATED_ON, last_updated_on);

        db.update(Constants.TABLE_NAME, values, Constants.ID + "=?", new String[]{id});

        db.close();
    }

    public void deleteContact(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.ID + "=?", new String[]{id});
        db.close();
    }

    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }
}
