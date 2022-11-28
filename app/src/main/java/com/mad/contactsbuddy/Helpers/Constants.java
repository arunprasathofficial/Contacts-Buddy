package com.mad.contactsbuddy.Helpers;

public class Constants {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "CONTACTS_BUDDY_DB";
    public static final String TABLE_NAME = "CONTACTS_TBL";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String PHONE_NO = "PHONE_NO";
    public static final String EMAIL = "EMAIL";
    public static final String IMAGE = "IMAGE";
    public static final String CREATED_ON = "CREATED_ON";
    public static final String LAST_UPDATED_ON = "LAST_UPDATED_ON";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NAME + " TEXT," +
            PHONE_NO + " TEXT," +
            EMAIL + " TEXT," +
            IMAGE + " TEXT," +
            CREATED_ON + " TEXT," +
            LAST_UPDATED_ON + " TEXT" + ")";
}
