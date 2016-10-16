package com.cloudkibo.kiboengage.database;

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

        String CREATE_USER_TABLE = "CREATE TABLE USERS ("
                + "id INTEGER PRIMARY KEY,"
                + "appId TEXT,"
                + "clientId TEXT,"
                + "appSecret TEXT,"
                + "customerId TEXT,"
                + "customerName TEXT,"
                + "customerPhone TEXT,"
                + "customerEmail TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_GROUPS_TABLE = "CREATE TABLE GROUPS ("
                + "id INTEGER PRIMARY KEY,"
                + "deptname TEXT,"
                + "deptdescription TEXT,"
                + "companyid TEXT,"
                + "createdby TEXT,"
                + "creationdate TEXT,"
                + "groupid TEXT,"
                + "deleteStatus TEXT" + ")";
        db.execSQL(CREATE_GROUPS_TABLE);

        String CREATE_MESSAGE_CHANNEL_TABLE = "CREATE TABLE MESSAGECHANNELS ("
                + "id INTEGER PRIMARY KEY,"
                + "msg_channel_name TEXT,"
                + "msg_channel_description TEXT,"
                + "companyid TEXT,"
                + "groupid TEXT,"
                + "createdby TEXT,"
                + "creationdate TEXT,"
                + "activeStatus TEXT,"
                + "channelid TEXT" + ")";
        db.execSQL(CREATE_MESSAGE_CHANNEL_TABLE);

        String CREATE_CHAT_TABLE = "CREATE TABLE CHATS ("
                + "id INTEGER PRIMARY KEY,"
                + "to_user TEXT,"
                + "from_user TEXT,"
                + "uniqueid TEXT,"
                + "visitoremail TEXT," // TODO test the need
                + "agentemail TEXT," // TODO test the need
                + "agentid TEXT," // TODO test the need
                + "is_seen TEXT," // TODO test the need
                + "type TEXT,"
                + "status TEXT,"
                + "msg TEXT,"
                + "request_id TEXT,"
                + "messagechannel TEXT,"
                + "companyid TEXT,"
                + "datetime TEXT" + ")";
        db.execSQL(CREATE_CHAT_TABLE);

        String CREATE_SESSIONS_TABLE = "CREATE TABLE SESSIONS ("
                + "id INTEGER PRIMARY KEY,"
                + "group_id TEXT,"
                + "msg_channel_id TEXT,"
                + "request_id TEXT,"
                + "agent_email TEXT,"
                + "agent_id TEXT,"
                + "agent_name TEXT,"
                + "datetime TEXT" + ")";
        db.execSQL(CREATE_SESSIONS_TABLE);

    }




    /////////////////////////////////////////////////////////////////////
    // Upgrading Tables                                                //
    /////////////////////////////////////////////////////////////////////


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS GROUPS");
        db.execSQL("DROP TABLE IF EXISTS MESSAGECHANNELS");
        db.execSQL("DROP TABLE IF EXISTS CHATS");
        db.execSQL("DROP TABLE IF EXISTS SESSIONS");

        // Create tables again
        onCreate(db);
    }





    /////////////////////////////////////////////////////////////////////
    // Storing user details in database                                //
    /////////////////////////////////////////////////////////////////////


    public void addUser(String appId, String clientId, String appSecret, String customerId,
                        String customerName, String customerPhone, String customerEmail) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("appId", appId); //
        values.put("clientId", clientId); //
        values.put("appSecret", appSecret); //
        values.put("customerId", customerId); //

        if(customerName != null) values.put("customerName", customerName);
        if(customerPhone != null) values.put("customerPhone", customerPhone);
        if(customerEmail != null) values.put("customerEmail", customerEmail);

        // Inserting Row
        db.insert("USERS", null, values);
        db.close(); // Closing database connection
    }



    /////////////////////////////////////////////////////////////////////
    // Storing group details in database                             //
    /////////////////////////////////////////////////////////////////////


    public void addGroup(String deptname, String deptdescription, String companyid, String createdby, String creationdate, String deleteStatus, String groupid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("deptname", deptname);
        values.put("deptdescription", deptdescription);
        values.put("companyid", companyid);
        values.put("createdby", createdby);
        values.put("creationdate", creationdate);
        values.put("deleteStatus", deleteStatus);
        values.put("groupid", groupid);

        db.insert("GROUPS", null, values);
        db.close(); // Closing database connection
    }




    /////////////////////////////////////////////////////////////////////
    // Storing messagechannel details in database                            //
    /////////////////////////////////////////////////////////////////////


    public void addMessageChannel(String msg_channel_name, String msg_channel_description, String companyid, String groupid, String createdby, String creationdate,
                        String activeStatus, String channelid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("msg_channel_name", msg_channel_name);
        values.put("msg_channel_description", msg_channel_description);
        values.put("companyid", companyid);
        values.put("groupid", groupid);
        values.put("createdby", createdby);
        values.put("creationdate", creationdate);
        values.put("activeStatus", activeStatus);
        values.put("channelid", channelid);

        // Inserting Row
        db.insert("MESSAGECHANNELS", null, values);
        db.close(); // Closing database connection
    }

    /////////////////////////////////////////////////////////////////////
    // Storing chat details in database                            //
    /////////////////////////////////////////////////////////////////////

    public void addChat(String to, String from, String uniqueid, String visitoremail, String agentemail, String agentid,
                        String is_seen, String type, String status, String msg, String request_id, String messagechannel,
                        String companyid, String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("to_user", to);
        values.put("from_user", from);
        values.put("uniqueid", uniqueid);
        values.put("visitoremail", visitoremail);
        values.put("agentemail", agentemail);
        values.put("agentid", agentid);
        values.put("is_seen", is_seen);
        values.put("type", type);
        values.put("status", status);
        values.put("msg", msg);
        values.put("request_id", request_id);
        values.put("messagechannel", messagechannel);
        values.put("companyid", companyid);
        values.put("datetime", datetime);

        // Inserting Row
        db.insert("CHATS", null, values);
        db.close(); // Closing database connection
    }


    /////////////////////////////////////////////////////////////////////
    // Storing session details in database                            //
    /////////////////////////////////////////////////////////////////////

    public void addSession(String group_id, String msg_channel_id, String request_id, String agent_email, String agent_id, String agent_name,
                        String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("group_id", group_id);
        values.put("msg_channel_id", msg_channel_id);
        values.put("request_id", request_id);
        values.put("agent_email", agent_email);
        values.put("agent_id", agent_id);
        values.put("agent_name", agent_name);
        values.put("datetime", datetime);

        // Inserting Row
        db.insert("SESSIONS", null, values);
        db.close(); // Closing database connection
    }


/*

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
*/


    /////////////////////////////////////////////////////////////////////
    // Getting user data from database                                 //
    /////////////////////////////////////////////////////////////////////


    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM USERS";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("appId", cursor.getString(1));
            user.put("clientId", cursor.getString(2));
            user.put("appSecret", cursor.getString(3));
            user.put("customerId", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }





    /////////////////////////////////////////////////////////////////////
    // Getting groups data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getGroups() throws JSONException {
        JSONArray groups = new JSONArray();
        String selectQuery = "SELECT  * FROM GROUPS";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject group = new JSONObject();
                group.put("deptname", cursor.getString(1));
                group.put("deptdescription", cursor.getString(2));
                group.put("companyid", cursor.getString(3));
                group.put("createdby", cursor.getString(4));
                group.put("creationdate", cursor.getString(5));
                group.put("groupid", cursor.getString(6));
                group.put("deleteStatus", cursor.getString(7));

                groups.put(group);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return groups;
    }

    /////////////////////////////////////////////////////////////////////
    // Getting message channel data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getMessageChannels() throws JSONException {
        JSONArray messageChannels = new JSONArray();
        String selectQuery = "SELECT  * FROM MESSAGECHANNELS";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject messageChannel = new JSONObject();
                messageChannel.put("msg_channel_name", cursor.getString(1));
                messageChannel.put("msg_channel_description", cursor.getString(2));
                messageChannel.put("companyid", cursor.getString(3));
                messageChannel.put("groupid", cursor.getString(4));
                messageChannel.put("createdby", cursor.getString(5));
                messageChannel.put("creationdate", cursor.getString(6));
                messageChannel.put("activeStatus", cursor.getString(7));
                messageChannel.put("channelid", cursor.getString(8));

                messageChannels.put(messageChannel);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return messageChannels;
    }

    public JSONArray getMessageChannels(String groupid) throws JSONException {
        JSONArray messageChannels = new JSONArray();
        String selectQuery = "SELECT  * FROM MESSAGECHANNELS WHERE groupid='"+groupid+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject messageChannel = new JSONObject();
                messageChannel.put("msg_channel_name", cursor.getString(1));
                messageChannel.put("msg_channel_description", cursor.getString(2));
                messageChannel.put("companyid", cursor.getString(3));
                messageChannel.put("groupid", cursor.getString(4));
                messageChannel.put("createdby", cursor.getString(5));
                messageChannel.put("creationdate", cursor.getString(6));
                messageChannel.put("activeStatus", cursor.getString(7));
                messageChannel.put("channelid", cursor.getString(8));

                messageChannels.put(messageChannel);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return messageChannels;
    }

    /////////////////////////////////////////////////////////////////////
    // Getting chats data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getChat(String request_id) throws JSONException {
        JSONArray chats = new JSONArray();
        String selectQuery = "SELECT * FROM CHATS WHERE request_id='"+ request_id +"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject chat = new JSONObject();
                chat.put("to_user", cursor.getString(1));
                chat.put("from_user", cursor.getString(2));
                chat.put("uniqueid", cursor.getString(3));
                chat.put("visitoremail", cursor.getString(4));
                chat.put("agentemail", cursor.getString(5));
                chat.put("agentid", cursor.getString(6));
                chat.put("is_seen", cursor.getString(7));
                chat.put("type", cursor.getString(8));
                chat.put("status", cursor.getString(9));
                chat.put("msg", cursor.getString(10));
                chat.put("request_id", cursor.getString(11));
                chat.put("messagechannel", cursor.getString(12));
                chat.put("companyid", cursor.getString(13));
                chat.put("datetime", cursor.getString(14));

                chats.put(chat);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return chats;
    }

    /////////////////////////////////////////////////////////////////////
    // Getting sessions data from database                             //
    /////////////////////////////////////////////////////////////////////


    public JSONArray getSessions() throws JSONException {
        JSONArray sessions = new JSONArray();
        String selectQuery = "SELECT  * FROM SESSIONS";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){

            while (cursor.isAfterLast() != true) {

                JSONObject session = new JSONObject();
                session.put("group_id", cursor.getString(1));
                session.put("msg_channel_id", cursor.getString(2));
                session.put("request_id", cursor.getString(3));
                session.put("agent_email", cursor.getString(4));
                session.put("agent_id", cursor.getString(5));
                session.put("agent_name", cursor.getString(6));
                session.put("datetime", cursor.getString(7));

                sessions.put(session);

                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        // return user
        return sessions;
    }


    /////////////////////////////////////////////////////////////////////
    // Other functions                                                 //
    /////////////////////////////////////////////////////////////////////


    ///**
    // * Getting user login status
    // * return true if rows are there in table
    // * *
    public int getRowCount() {
        String countQuery = "SELECT  * FROM USERS";
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
    public void resetUserTables(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete("USERS", null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    ///**
    // * Delete all contacts Table
    // *
    public void resetGroupsTable(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete("GROUPS", null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    public void resetMessageChannelsTable(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete("MESSAGECHANNELS", null, null);
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
            db.delete("CHATS", null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    public void resetSessionsTable(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            db.delete("SESSIONS", null, null);
            db.close();
        }catch(SQLiteDatabaseLockedException e){
            e.printStackTrace();
        }
    }

    /*public void resetSpecificChat(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + UserChat.TABLE_USERCHAT + " WHERE ("+
                UserChat.USERCHAT_TO +" = '"+ user1 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user2 +"') OR ("+
                UserChat.USERCHAT_TO +" = '"+ user2 +"' AND "+
                UserChat.USERCHAT_FROM +" = '"+ user1 +"')";

        db.execSQL(deleteQuery);
        db.close();
    }*/

    /*public void resetSpecificContact(String user1, String user2){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + Contacts.TABLE_CONTACTS + " WHERE "+ Contacts.CONTACT_USERNAME + "='"+ user2 +"'";

        db.execSQL(deleteQuery);
        db.close();
    }*/

    /*public void resetSpecificChatHistorySync(String uniqueid){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM chat_history_sync WHERE uniqueid='"+ uniqueid +"'";

        db.execSQL(deleteQuery);
        db.close();
    }*/

}
