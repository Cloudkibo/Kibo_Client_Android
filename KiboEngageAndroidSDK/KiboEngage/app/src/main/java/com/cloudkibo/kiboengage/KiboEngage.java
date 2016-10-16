package com.cloudkibo.kiboengage;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.network.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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

                        createSessions();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }.execute();

    }

    private static void createSessions(){

        new AsyncTask<String, String, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... args) {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", customerEmail));
                params.add(new BasicNameValuePair("customerID", customerId));
                params.add(new BasicNameValuePair("phone", customerPhone));
                params.add(new BasicNameValuePair("country", "Pakistan"));
                params.add(new BasicNameValuePair("companyid", clientId));
                params.add(new BasicNameValuePair("platform", "mobile"));
                params.add(new BasicNameValuePair("customerName", customerName));
                params.add(new BasicNameValuePair("isMobile", "true"));
                params.add(new BasicNameValuePair("status", "new"));
                JSONArray sessionInfo = new JSONArray();
                try {
                    DatabaseHandler db = new DatabaseHandler(appContext);
                    JSONArray groupsData = db.getGroups();
                    for (int i=0; i < groupsData.length(); i++) {
                        db = new DatabaseHandler(appContext);
                        JSONArray messageChannelsData = db.getMessageChannels(groupsData.getJSONObject(i).getString("groupid"));
                        for(int j=0; j< messageChannelsData.length(); j++){
                            JSONObject sessionObj = new JSONObject();
                            sessionObj.put("departmentid", groupsData.getJSONObject(i).getString("groupid"));
                            sessionObj.put("messagechannel", messageChannelsData.getJSONObject(j).getString("channelid"));

                            String uniqueid = Long.toHexString(Double.doubleToLongBits(Math.random()));
                            uniqueid += (new Date().getYear()) + "" + (new Date().getMonth()) + "" + (new Date().getDay());
                            uniqueid += (new Date().getHours()) + "" + (new Date().getMinutes()) + "" + (new Date().getSeconds());

                            sessionObj.put("request_id", uniqueid);

                            sessionInfo.put(sessionObj);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                params.add(new BasicNameValuePair("sessionInfo", sessionInfo.toString()));
                Log.d("KIBO_ENGAGE", "Object for create sessions: "+ params.toString());
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.createSession(params, appId, clientId, appSecret);
                return json;
            }

            @Override
            protected void onPostExecute(JSONObject jsonA) {
                Log.d("KIBO_ENGAGE", "Create Sessions: "+ jsonA.toString());
                /*try {

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
                                    row.getString("creationdate"), row.getString("activeStatus"));

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            }

        }.execute();

    }

    public static String getCustomerId(){
        return customerId;
    }

}
