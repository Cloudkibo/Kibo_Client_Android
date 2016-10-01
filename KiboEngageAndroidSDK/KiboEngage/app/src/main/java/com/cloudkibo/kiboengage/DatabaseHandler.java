package com.cloudkibo.kiboengage;

/**
 * Created by sojharo on 10/09/2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

//import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.cloudkibo.database.CloudKiboDatabaseContract.Contacts;
//import com.cloudkibo.database.CloudKiboDatabaseContract.User;
//import com.cloudkibo.database.CloudKiboDatabaseContract.UserChat;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "kiboengage";



    /////////////////////////////////////////////////////////////////////
    // Constructor                                                     //
    /////////////////////////////////////////////////////////////////////

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    /////////////////////////////////////////////////////////////////////
    // Creating Tables                                                 //
    /////////////////////////////////////////////////////////////////////


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + User.TABLE_USER_NAME + "("
                + User.KEY_ID + " INTEGER PRIMARY KEY,"
                //+ User.KEY_FIRSTNAME + " TEXT,"
                //+ User.KEY_LASTNAME + " TEXT,"
                //+ User.KEY_EMAIL + " TEXT UNIQUE,"
                //+ User.KEY_USERNAME + " TEXT,"
                + User.KEY_UID + " TEXT,"
                + "display_name TEXT,"
                + "phone TEXT,"
                + "national_number TEXT,"
                + "country_prefix TEXT,"
                + User.KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Contacts.TABLE_CONTACTS + "("
                + Contacts.CONTACT_ID + " INTEGER PRIMARY KEY,"
                //+ Contacts.CONTACT_FIRSTNAME + " TEXT,"
                //+ Contacts.CONTACT_LASTNAME + " TEXT,"
                + Contacts.CONTACT_PHONE + " TEXT UNIQUE,"
                + "display_name" + " TEXT,"
                + Contacts.CONTACT_UID + " TEXT,"
                + Contacts.SHARED_DETAILS + " TEXT,"
                + Contacts.CONTACT_STATUS + " TEXT,"
                + "on_cloudkibo" + " TEXT"+ ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_USERCHAT_TABLE = "CREATE TABLE " + UserChat.TABLE_USERCHAT + "("
                + UserChat.USERCHAT_ID + " INTEGER PRIMARY KEY, "
                + UserChat.USERCHAT_TO + " TEXT, "
                + UserChat.USERCHAT_FROM + " TEXT, "
                + UserChat.USERCHAT_FROM_FULLNAME + " TEXT, "
                + UserChat.USERCHAT_MSG + " TEXT, "
                + UserChat.USERCHAT_DATE + " TEXT, "
                + "status" + " TEXT, "
                + "uniqueid" + " TEXT, "
                + "contact_phone" + " TEXT "+ ")";
        db.execSQL(CREATE_USERCHAT_TABLE);

        String CREATE_CALL_HISTORY_TABLE = "CREATE TABLE call_history ("
                + "id INTEGER PRIMARY KEY, "
                + "call_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "type TEXT, " // values : placed, received, missed
                + "contact_phone TEXT "+ ")";
        db.execSQL(CREATE_CALL_HISTORY_TABLE);

        String CREATE_CHAT_HISTORY_SYNC_TABLE = "CREATE TABLE chat_history_sync ("
                + "id INTEGER PRIMARY KEY, "
                + "status TEXT, "
                + "uniqueid TEXT, "
                + "fromperson TEXT "+ ")";
        db.execSQL(CREATE_CHAT_HISTORY_SYNC_TABLE);


    }


/*

    /////////////////////////////////////////////////////////////////////
    // Upgrading Tables                                                //
    /////////////////////////////////////////////////////////////////////


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_USER_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contacts.TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + UserChat.TABLE_USERCHAT);
        db.execSQL("DROP TABLE IF EXISTS call_history");
        db.execSQL("DROP TABLE IF EXISTS chat_history_sync");

        // Create tables again
        onCreate(db);
    }





    /////////////////////////////////////////////////////////////////////
    // Storing user details in database                                //
    /////////////////////////////////////////////////////////////////////


    public void addUser(String id, String display_name, String phone, String national_number, String country_prefix, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User.KEY_UID, id); // FirstName    //values.put(User.KEY_FIRSTNAME, id); // FirstName
        values.put("display_name", display_name); // LastName
        values.put("phone", phone); // Email
        values.put("national_number", national_number); // UserName
        values.put("country_prefix", country_prefix); // Email
        values.put(User.KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        db.insert(User.TABLE_USER_NAME, null, values);
        db.close(); // Closing database connection
    }




    /////////////////////////////////////////////////////////////////////
    // Storing contact details in database                             //
    /////////////////////////////////////////////////////////////////////


    public void addContact(String on_cloudkibo, String lname, String phone, String uname, String uid, String shareddetails,
                           String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(Contacts.CONTACT_FIRSTNAME, fname); // FirstName
        //values.put(Contacts.CONTACT_LASTNAME, lname); // LastName
        values.put(Contacts.CONTACT_PHONE, phone); // Phone
        values.put("display_name", uname); // UserName
        values.put(Contacts.CONTACT_UID, uid); // Uid
        values.put(Contacts.CONTACT_STATUS, status); // Status
        values.put(Contacts.SHARED_DETAILS, shareddetails); // Created At
        values.put("on_cloudkibo", on_cloudkibo);

        // Inserting Row
        try {
            db.insert(Contacts.TABLE_CONTACTS, null, values);
        } catch (android.database.sqlite.SQLiteConstraintException e){
            Log.e("SQLITE_CONTACTS", uname + " - " + phone);
            ACRA.getErrorReporter().handleSilentException(e);
        }
        db.close(); // Closing database connection
    }




    /////////////////////////////////////////////////////////////////////
    // Storing userchat details in database                            //
    /////////////////////////////////////////////////////////////////////


    public void addChat(String to, String from, String from_fullname, String msg, String date, String status,
                        String uniqueid) {

        String myPhone = getUserDetails().get("phone");
        String contactPhone = "";
        if(myPhone.equals(to))  contactPhone = from;
        if(myPhone.equals(from))  contactPhone = to;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserChat.USERCHAT_TO, to); // TO
        values.put(UserChat.USERCHAT_FROM, from); // FROM
        values.put(UserChat.USERCHAT_FROM_FULLNAME, from_fullname); // FROM FULL NAME
        values.put(UserChat.USERCHAT_MSG, msg); // CHAT MESSAGE
        values.put(UserChat.USERCHAT_DATE, date); // DATE
        values.put("status", status); // status: pending, sent, delivered, seen
        values.put("uniqueid", uniqueid);
        values.put("contact_phone", contactPhone); // Contact

        // Inserting Row
        db.insert(UserChat.TABLE_USERCHAT, null, values);
        db.close(); // Closing database connection
    }

    public void updateChat(String status, String uniqueid) {

        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE " + UserChat.TABLE_USERCHAT +
                " SET status='"+ status +"' WHERE uniqueid='"+uniqueid+"'";

        try {
            db.execSQL(updateQuery);
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }


    public void updateContact(String status, String phone, String id) {

        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE " + Contacts.TABLE_CONTACTS +
                " SET "+ Contacts.CONTACT_STATUS +"='"+ status +"', " + Contacts.CONTACT_UID +"='"+ id +"' "+
                " WHERE "+ Contacts.CONTACT_PHONE +"='"+phone+"'";

        try {
            db.execSQL(updateQuery);
        } catch (Exception e){
            e.printStackTrace();
        }

        db.close();
    }


    /////////////////////////////////////////////////////////////////////
    // Storing userchat details in database                            //
    /////////////////////////////////////////////////////////////////////


    public void addCallHistory(String type, String contact_phone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("type", type); // values : placed, received, missed
        values.put("contact_phone", contact_phone); // FROM

        // Inserting Row
        db.insert("call_history", null, values);
        db.close(); // Closing database connection
    }

    public void addChatSyncHistory(String status, String uniqueid, String fromperson) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);
        values.put("uniqueid", uniqueid);
        values.put("fromperson", fromperson);

        // Inserting Row
        db.insert("chat_history_sync", null, values);
        db.close(); // Closing database connection
    }




    /////////////////////////////////////////////////////////////////////
    // Getting user data from database                                 //
    /////////////////////////////////////////////////////////////////////


    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + User.TABLE_USER_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put(User.KEY_UID, cursor.getString(1));
            user.put("display_name", cursor.getString(2));
            user.put("phone", cursor.getString(3));
            user.put("national_number", cursor.getString(4));
            user.put("country_prefix", cursor.getString(5));
            user.put(User.KEY_CREATED_AT, cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }





    /////////////////////////////////////////////////////////////////////
    // Getting contacts data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getContacts() throws JSONException {
        JSONArray contacts = new JSONArray();
        String selectQuery = "SELECT  * FROM " + Contacts.TABLE_CONTACTS +" where on_cloudkibo='true'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                //contact.put(Contacts.CONTACT_FIRSTNAME, cursor.getString(1));
                //contact.put(Contacts.CONTACT_LASTNAME, cursor.getString(2));
                contact.put(Contacts.CONTACT_PHONE, cursor.getString(1));
                contact.put("display_name", cursor.getString(2));
                contact.put(Contacts.CONTACT_UID, cursor.getString(3));
                contact.put(Contacts.SHARED_DETAILS, cursor.getString(4));
                contact.put(Contacts.CONTACT_STATUS, cursor.getString(5));
                contact.put("on_cloudkibo", cursor.getString(6));

                contacts.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return contacts;
    }

    public JSONArray getContactsOnAddressBook() throws JSONException {
        JSONArray contacts = new JSONArray();
        String selectQuery = "SELECT  * FROM " + Contacts.TABLE_CONTACTS +" where on_cloudkibo='false'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                //contact.put(Contacts.CONTACT_FIRSTNAME, cursor.getString(1));
                //contact.put(Contacts.CONTACT_LASTNAME, cursor.getString(2));
                contact.put(Contacts.CONTACT_PHONE, cursor.getString(1));
                contact.put("display_name", cursor.getString(2));
                contact.put(Contacts.CONTACT_UID, cursor.getString(3));
                contact.put(Contacts.SHARED_DETAILS, cursor.getString(4));
                contact.put(Contacts.CONTACT_STATUS, cursor.getString(5));
                contact.put("on_cloudkibo", cursor.getString(6));

                contacts.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return contacts;
    }

    public JSONArray getSpecificContact(String phone) throws JSONException {
        JSONArray contacts = new JSONArray();
        String selectQuery = "SELECT  * FROM " + Contacts.TABLE_CONTACTS +" where phone='"+ phone +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                //contact.put(Contacts.CONTACT_FIRSTNAME, cursor.getString(1));
                //contact.put(Contacts.CONTACT_LASTNAME, cursor.getString(2));
                contact.put(Contacts.CONTACT_PHONE, cursor.getString(1));
                contact.put("display_name", cursor.getString(2));
                contact.put(Contacts.CONTACT_UID, cursor.getString(3));
                contact.put(Contacts.SHARED_DETAILS, cursor.getString(4));
                contact.put(Contacts.CONTACT_STATUS, cursor.getString(5));
                contact.put("on_cloudkibo", cursor.getString(6));

                contacts.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return contacts;
    }





    /////////////////////////////////////////////////////////////////////
    // Getting userchat data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getChat(String user1, String user2) throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery = "SELECT  * FROM " + UserChat.TABLE_USERCHAT + " WHERE ("+
                UserChat.USERCHAT_TO +" = '"+ user1 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user2 +"') OR ("+
                UserChat.USERCHAT_TO +" = '"+ user2 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user1 +"')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put(UserChat.USERCHAT_TO, cursor.getString(1));
                contact.put(UserChat.USERCHAT_FROM, cursor.getString(2));
                contact.put(UserChat.USERCHAT_FROM_FULLNAME, cursor.getString(3));
                contact.put(UserChat.USERCHAT_MSG, cursor.getString(4));
                contact.put(UserChat.USERCHAT_DATE, cursor.getString(5));
                contact.put("status", cursor.getString(6));
                contact.put("uniqueid", cursor.getString(7));
                contact.put("contact_phone", cursor.getString(8));

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }

    public JSONArray getChat() throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery = "SELECT  * FROM " + UserChat.TABLE_USERCHAT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put(UserChat.USERCHAT_TO, cursor.getString(1));
                contact.put(UserChat.USERCHAT_FROM, cursor.getString(2));
                contact.put(UserChat.USERCHAT_FROM_FULLNAME, cursor.getString(3));
                contact.put(UserChat.USERCHAT_MSG, cursor.getString(4));
                contact.put(UserChat.USERCHAT_UID, cursor.getString(5));
                contact.put(UserChat.USERCHAT_DATE, cursor.getString(6));
                contact.put("contact_phone", cursor.getString(7));

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }

    public JSONArray getPendingChat() throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery = "SELECT  * FROM " + UserChat.TABLE_USERCHAT +" WHERE status='pending'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put(UserChat.USERCHAT_TO, cursor.getString(1));
                contact.put(UserChat.USERCHAT_FROM, cursor.getString(2));
                contact.put(UserChat.USERCHAT_FROM_FULLNAME, cursor.getString(3));
                contact.put(UserChat.USERCHAT_MSG, cursor.getString(4));
                contact.put(UserChat.USERCHAT_DATE, cursor.getString(5));
                contact.put("status", cursor.getString(6));
                contact.put("uniqueid", cursor.getString(7));
                contact.put("contact_phone", cursor.getString(8));

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }

    public JSONArray getChatHistoryStatus() throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery = "SELECT uniqueid, status, fromperson FROM chat_history_sync";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put("uniqueid", cursor.getString(0));
                contact.put("status", cursor.getString(1));
                contact.put("fromperson", cursor.getString(2));

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }

    public JSONArray getChatList() throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery =
                " SELECT "+ UserChat.USERCHAT_DATE +", contact_phone, " + UserChat.USERCHAT_MSG
                        +" FROM " + UserChat.TABLE_USERCHAT
                        +" GROUP BY contact_phone ORDER BY "+ UserChat.USERCHAT_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put("date", cursor.getString(0));
                contact.put("contact_phone", cursor.getString(1));
                contact.put("msg", cursor.getString(2));
                contact.put("pendingMsgs", getUnReadMessagesCount(cursor.getString(1)));
                JSONArray contactInAddressBook = getSpecificContact(cursor.getString(1));
                if(contactInAddressBook.length() > 0) {
                    contact.put("display_name", contactInAddressBook.getJSONObject(0).getString("display_name"));
                } else {
                    contact.put("display_name", cursor.getString(1));
                }

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }




    public JSONArray getCallHistory() throws JSONException {
        JSONArray chats = new JSONArray();

        String selectQuery = "SELECT * FROM call_history, "+ Contacts.TABLE_CONTACTS
                +" WHERE call_history.contact_phone = "+ Contacts.TABLE_CONTACTS +"."+ Contacts.CONTACT_PHONE
                +" ORDER BY call_date DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject contact = new JSONObject();
                contact.put("call_date", cursor.getString(1));
                contact.put("type", cursor.getString(2));
                contact.put("contact_phone", cursor.getString(3));
                contact.put("display_name", cursor.getString(6));
                contact.put("contact_id", cursor.getString(7));

                chats.put(contact);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }





    /////////////////////////////////////////////////////////////////////
    // Other functions                                                 //
    /////////////////////////////////////////////////////////////////////


    ///**
    // * Getting user login status
    // * return true if rows are there in table
    // * *
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + User.TABLE_USER_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    public int getUnReadMessagesCount(String contact_phone) {
        String countQuery = "SELECT  * FROM " + UserChat.TABLE_USERCHAT + " WHERE status = 'delivered' AND fromperson = '"+ contact_phone +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }


    ///**
    // * Recreate database
    // * Delete all tables and create them again
    // *
    public void resetTables(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(User.TABLE_USER_NAME, null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    ///**
    // * Delete all contacts Table
    // *
    public void resetContactsTable(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(Contacts.TABLE_CONTACTS, null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    ///**
    // * Delete all chats Table
    // *
    public void resetChatsTable(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete(UserChat.TABLE_USERCHAT, null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    public void resetSpecificChat(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + UserChat.TABLE_USERCHAT + " WHERE ("+
                UserChat.USERCHAT_TO +" = '"+ user1 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user2 +"') OR ("+
                UserChat.USERCHAT_TO +" = '"+ user2 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user1 +"')";

        db.execSQL(deleteQuery);
        db.close();
    }

    public void resetSpecificContact(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + Contacts.TABLE_CONTACTS + " WHERE "+ Contacts.CONTACT_USERNAME + "='"+ user2 +"'";

        db.execSQL(deleteQuery);
        db.close();
    }

    public void resetSpecificChatHistorySync(String uniqueid){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM chat_history_sync WHERE uniqueid='"+ uniqueid +"'";

        db.execSQL(deleteQuery);
        db.close();
    }
*/
}
