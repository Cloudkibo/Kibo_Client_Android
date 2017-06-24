package com.cloudkibo.kiboengage;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.library.Utility;
import com.cloudkibo.kiboengage.network.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sojharo on 09/09/2016.
 */
public class KiboEngage {

    private static String appId;

    private static String clientId;

    private static String appSecret;

    private static String customerId;

    private static String customerName;

    private static String customerPhone;

    private static String customerEmail;

    private static Context appContext;

    private static JSONArray sessions;

    /**
     * Initializes the KiboEngage Widget for use. Must be called from onCreate() of Main Activity.
     *
     * @param context Provide Application context using getApplicationContext()
     * @param app_id application id provided to customer company on dashboard settings
     * @param client_id client id provided to customer company on dashboard settings
     * @param app_secret app secret provided to customer company on dashboard settings
     * @param customer_id unique id of the customer/client/user of the company's application
     * @param customer_name name of the customer/client/user of the company's application (optional, pass null if don't want to use it)
     * @param customer_phone phone of the customer/client/user of the company's application (optional, pass null if don't want to use it)
     * @param customer_email email of the customer/client/user of the company's application (optional, pass null if don't want to use it)
     */
    public static void initialize(Context context, String app_id, String client_id, String app_secret,
                                  String customer_id, String customer_name, String customer_phone,
                                  String customer_email){

        appContext = context;
        appId = app_id;
        clientId = client_id;
        appSecret = app_secret;
        customerId = customer_id;
        customerName = customer_name;
        customerPhone = customer_phone;
        customerEmail = customer_email;

        initializeUserCredentials(context, app_id, client_id, app_secret,
                customer_id, customer_name, customer_phone,
                customer_email);

        loadGroups();

    }

    private static void initializeUserCredentials(Context context, String app_id, String client_id, String app_secret,
                                           String customerId, String customer_name, String customer_phone,
                                           String customer_email){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetUserTables();

        db = new DatabaseHandler(context);
        db.addUser(app_id, client_id, app_secret, customerId, customer_name, customer_phone, customer_email);

        db = new DatabaseHandler(context);
        Log.d("KIBO_ENGAGE", "Registered App with ID: "+ db.getUserDetails().get("appId"));
    }

    /*public static void startSync(Context ctx) {
        DatabaseHandler db = new DatabaseHandler(ctx);
        HashMap<String, String> user = db.getUserDetails();
        appContext = ctx;
        appId = user.get("appId");
        clientId = user.get("clientId");
        appSecret = user.get("appSecret");
        customerId = user.get("customerId");
        loadGroups();
    }*/

    private static void loadGroups(){

        new AsyncTask<String, String, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... args) {
                UserFunctions userFunction = new UserFunctions();
                JSONArray json = userFunction.getGroupsList(appId, clientId, appSecret);
                return json;
            }

            @Override
            protected void onPostExecute(JSONArray jsonA) {
                try {

                    if (jsonA != null) {

                        DatabaseHandler db = new DatabaseHandler(
                                appContext);

                        db.resetGroupsTable();

                        db = new DatabaseHandler(appContext);

                        for (int i=0; i < jsonA.length(); i++) {
                            JSONObject row = jsonA.getJSONObject(i);
                            Log.d("KIBO_ENGAGE", "Groups: "+ row.toString());

                            db.addGroup(row.getString("deptname"), row.getString("deptdescription"),
                                    row.getString("companyid"), row.getJSONObject("createdby").getString("email"),
                                    row.getString("creationdate"), row.getString("deleteStatus"), row.getString("_id"));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadChannels();

            }

        }.execute();

    }

    private static void loadChannels(){

        new AsyncTask<String, String, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... args) {
                UserFunctions userFunction = new UserFunctions();
                JSONArray json = userFunction.getChannelsList(appId, clientId, appSecret);
                return json;
            }

            @Override
            protected void onPostExecute(JSONArray jsonA) {
                try {

                    if (jsonA != null) {

                        DatabaseHandler db = new DatabaseHandler(
                                appContext);

                        db.resetMessageChannelsTable();

                        db = new DatabaseHandler(appContext);

                        for (int i=0; i < jsonA.length(); i++) {
                            JSONObject row = jsonA.getJSONObject(i);
                            Log.d("KIBO_ENGAGE", "Message Channels: "+ row.toString());

                            db.addMessageChannel(row.getString("msg_channel_name"), row.getString("msg_channel_description"),
                                    row.getString("companyid"), row.getString("groupid"), row.getString("createdby"),
                                    row.getString("creationdate"), row.getString("activeStatus"), row.getString("_id"));

                        }

                        getSessions();
                        loadBulkSMS();

                        //createSessions();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }.execute();

    }

    private static void getSessions(){

        new AsyncTask<String, String, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... args) {
                UserFunctions userFunction = new UserFunctions();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("customerid", customerId));
                JSONArray json = userFunction.getSessions(params, appId, clientId, appSecret);
                return json;
            }

            @Override
            protected void onPostExecute(JSONArray jsonA) {

                if (jsonA != null) {
                    Log.d("KIBO_ENGAGE", "Get Sessions: "+ jsonA.toString());

                    sessions = jsonA;

                    createSessions();
                }

            }

        }.execute();

    }

    private static Boolean isSessionOnServer(String groupid, String channelid){

        for (int i=0; i < sessions.length(); i++) {
            try {
                JSONObject row = sessions.getJSONObject(i);

                if(row.getString("departmentid").equals(groupid)){
                     if(row.getJSONArray("messagechannel").getString(row.getJSONArray("messagechannel").length()-1).equals(channelid))
                         return true;
                }


            }catch(JSONException e){
                e.printStackTrace();
            }

        }

        return false;
    }

    private static JSONObject getSessionFromServerList(String groupid, String channelid){

        for (int i=0; i < sessions.length(); i++) {
            try {
                JSONObject row = sessions.getJSONObject(i);

                if(row.getString("departmentid").equals(groupid)){
                    if(row.getJSONArray("messagechannel").getString(row.getJSONArray("messagechannel").length()-1).equals(channelid))
                        return row;
                }


            }catch(JSONException e){
                e.printStackTrace();
            }

        }

        return null;
    }

    private static void createSessions(){

        new AsyncTask<String, String, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... args) {

                JSONObject jParams = new JSONObject();

                JSONArray sessionInfo = new JSONArray();
                try {
                    jParams.put("email", customerEmail);
                    jParams.put("customerID", customerId);
                    jParams.put("phone", customerPhone);
                    //jParams.put("country", "Pakistan");
                    jParams.put("companyid", clientId);
                    jParams.put("platform", "mobile");
                    jParams.put("customerName", customerName);
                    jParams.put("isMobile", "true");
                    jParams.put("status", "new");

                    DatabaseHandler db = new DatabaseHandler(appContext);
                    JSONArray groupsData = db.getGroups();
                    for (int i=0; i < groupsData.length(); i++) {
                        db = new DatabaseHandler(appContext);
                        JSONArray messageChannelsData = db.getMessageChannels(groupsData.getJSONObject(i).getString("groupid"));
                        for(int j=0; j< messageChannelsData.length(); j++){

                            String uniqueid = Utility.generateUniqueId();

                            String groupId = groupsData.getJSONObject(i).getString("groupid");
                            String channelId = messageChannelsData.getJSONObject(j).getString("channelid");

                            if(!isSessionOnServer(groupId, channelId)) {
                                JSONObject sessionObj = new JSONObject();
                                sessionObj.put("departmentid", groupId);
                                sessionObj.put("messagechannel", channelId);
                                sessionObj.put("request_id", uniqueid);

                                sessionInfo.put(sessionObj);
                            } else {
                                JSONObject sessionFromServerList = getSessionFromServerList(groupId, channelId);
                                uniqueid = sessionFromServerList.getString("request_id");
                            }

                            db = new DatabaseHandler(appContext);
                            int count = db.getRowCountForSpecificSessions(groupId, channelId);
                            if(count < 1) {
                                db = new DatabaseHandler(appContext);
                                db.addSession(groupId, channelId,
                                        uniqueid, "", "", "", Utility.getCurrentTimeInISO());
                            }
                        }
                    }
                    jParams.put("sessionInfo", sessionInfo);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.createSession(jParams, appId, clientId, appSecret);
                return json;
            }

            @Override
            protected void onPostExecute(JSONObject jsonA) {
                Log.d("KIBO_ENGAGE", "Create Sessions: "+ jsonA.toString());
            }

        }.execute();

    }

    private static void loadBulkSMS(){

        DatabaseHandler db2 = new DatabaseHandler(appContext);
        int bulkSMSCount = db2.getRowCountForBulkSMS();

        if(bulkSMSCount < 1){
            new AsyncTask<String, String, JSONArray>() {

                @Override
                protected JSONArray doInBackground(String... args) {
                    UserFunctions userFunction = new UserFunctions();
                    JSONArray json = userFunction.getBulkSMSList(appId, clientId, appSecret);
                    return json;
                }

                @Override
                protected void onPostExecute(JSONArray jsonA) {
                    try {

                        if (jsonA != null) {

                            DatabaseHandler db = new DatabaseHandler(
                                    appContext);

                            db.resetBulkSMSTable();

                            db = new DatabaseHandler(appContext);

                            for (int i=0; i < jsonA.length(); i++) {
                                JSONObject row = jsonA.getJSONObject(i);
                                Log.d("KIBO_ENGAGE", "Bulk SMS: "+ row.toString());

                                db.addBulkSMS(row.getString("title"), row.getString("description"),
                                        row.getString("agent_id"), row.getString("hasImage"),
                                        (row.has("image_url")) ? row.getString("image_url") : "",
                                        row.getString("companyid"), row.getString("datetime"));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }.execute();
        }

    }

    public static String getCustomerId(){
        return customerId;
    }

    // todo new work done
    private class GroupAdapter2 extends BaseAdapter
    {
        Context context;

        public GroupAdapter(Context appContext){
            context = appContext;
        }
        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return groupList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public GroupItem getItem(int arg0)
        {
            return groupList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = LayoutInflater.from(this.context).inflate(
                        R.layout.kiboengage_sdk_list_item, null);

            GroupItem c = getItem(pos);
            TextView lbl = (TextView) v.findViewById(R.id.lblContactDisplayName);
            lbl.setText(c.getName());

            return v;
        }

    }

}
