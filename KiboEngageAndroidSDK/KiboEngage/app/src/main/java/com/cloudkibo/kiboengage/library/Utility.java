package com.cloudkibo.kiboengage.library;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.cloudkibo.kiboengage.GroupChat;
import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.network.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by sojharo on 20/09/2016.
 */
public class Utility {

    public static String convertDateToLocalTimeZoneAndReadable(String dStr) throws ParseException {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        Date date = format.parse(dStr);

        TimeZone tZ = TimeZone.getDefault();
        tZ = TimeZone.getTimeZone(tZ.getID());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tZ);
        int currentOffsetFromUTC = tZ.getRawOffset() + (tZ.inDaylightTime(date) ? tZ.getDSTSavings() : 0);
        String result = df.format(date.getTime() + currentOffsetFromUTC);

        return result;
    }

    public static String getCurrentTimeInISO(){
        TimeZone tZ = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tZ);
        return df.format(new Date());
    }

    public static String generateUniqueId(){
        String uniqueid = Long.toHexString(Double.doubleToLongBits(Math.random()));
        uniqueid += (new Date().getYear()) + "" + (new Date().getMonth()) + "" + (new Date().getDay());
        uniqueid += (new Date().getHours()) + "" + (new Date().getMinutes()) + "" + (new Date().getSeconds());

        return uniqueid;
    }

    public static void handleChannelNotification(Context ctx, JSONObject payload){
        try{
            DatabaseHandler db = new DatabaseHandler(ctx);
            String workType = payload.getString("operation");
            payload = payload.getJSONObject("obj");
            if (workType.equals("EditChannel")) {
                db.updateMessageChannel(payload.getString("_id"), payload.getString("activeStatus"),
                        payload.getString("groupid"), payload.getString("msg_channel_name"),
                        payload.getString("msg_channel_description"));
            } else if (workType.equals("CreateChannel")) {
                db.addMessageChannel(payload.getString("msg_channel_name"), payload.getString("msg_channel_description"),
                        payload.getString("companyid"), payload.getString("groupid"), payload.getString("createdby"),
                        payload.getString("creationdate"), payload.getString("activeStatus"), payload.getString("_id"));
                db = new DatabaseHandler(ctx);
                HashMap<String, String> userDetail = db.getUserDetails();
                createSessions(ctx, userDetail.get("customerId"), payload.getString("companyid"), payload.getString("groupid"),
                        payload.getString("_id"), userDetail.get("appId"), userDetail.get("clientId"),
                        userDetail.get("appSecret"));
            } else {
                db.resetSpecificChatsOfChannel(payload.getString("_id"));
                db = new DatabaseHandler(ctx);
                db.resetSpecificSessionsOfChannel(payload.getString("_id"));
                db = new DatabaseHandler(ctx);
                db.resetSpecificMessageChannel(payload.getString("_id"));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void handleGroupNotification(Context ctx, JSONObject payload){
        try{
            DatabaseHandler db = new DatabaseHandler(ctx);
            String workType = payload.getString("operation");
            payload = payload.getJSONObject("obj");
            if (workType.equals("EditTeam")) {
                db.updateGroup(payload.getString("_id"), payload.getString("deptname"), payload.getString("deptdescription"));
            } else if (workType.equals("CreateTeam")) {
                db.addGroup(payload.getString("deptname"), payload.getString("deptdescription"),
                        payload.getString("companyid"), payload.getString("createdby"),
                        payload.getString("creationdate"), payload.getString("deleteStatus"), payload.getString("_id"));
            } else {
                JSONArray channels = db.getMessageChannels(payload.getString("_id"));
                db = new DatabaseHandler(ctx);
                for(int i=0; i<channels.length(); i++){
                    JSONObject channel = channels.getJSONObject(i);
                    db.resetSpecificChatsOfChannel(channel.getString("channelid"));
                    db = new DatabaseHandler(ctx);
                    db.resetSpecificSessionsOfChannel(channel.getString("channelid"));
                    db = new DatabaseHandler(ctx);
                    db.resetSpecificMessageChannel(channel.getString("channelid"));
                }
                db = new DatabaseHandler(ctx);
                db.resetSpecificGroup(payload.getString("_id"));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private static void createSessions(final Context appContext, final String customerId, final String companyId,
                                       final String groupId, final String channelId, final String appId,
                                       final String clientId, final String appSecret){

        new AsyncTask<String, String, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... args) {

                JSONObject jParams = new JSONObject();

                JSONArray sessionInfo = new JSONArray();
                try {
                    jParams.put("customerID", customerId);
                    jParams.put("companyid", companyId);
                    jParams.put("platform", "mobile");
                    jParams.put("isMobile", "true");
                    jParams.put("status", "new");

                    String uniqueid = Utility.generateUniqueId();

                    JSONObject sessionObj = new JSONObject();
                    sessionObj.put("departmentid", groupId);
                    sessionObj.put("messagechannel", channelId);
                    sessionObj.put("request_id", uniqueid);

                    sessionInfo.put(sessionObj);

                    DatabaseHandler db = new DatabaseHandler(appContext);
                    int count = db.getRowCountForSpecificSessions(groupId, channelId);
                    if(count < 1) {
                        db = new DatabaseHandler(appContext);
                        db.addSession(groupId, channelId,
                                uniqueid, "", "", "", Utility.getCurrentTimeInISO());
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

    public static void fetchChatMessage(final Context appContext, final JSONObject payload){

        new AsyncTask<String, String, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... args) {

                UserFunctions userFunction = new UserFunctions();
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                try {
                    params.add(new BasicNameValuePair("uniqueid", payload.getString("uniqueid")));
                    params.add(new BasicNameValuePair("request_id", "request_id"));
                } catch (JSONException e){
                    e.printStackTrace();
                }

                DatabaseHandler db = new DatabaseHandler(appContext);
                HashMap<String, String> user = db.getUserDetails();
                return userFunction.fetchChat(params, user.get("appId"), user.get("clientId"), user.get("appSecret`"));
            }

            @Override
            protected void onPostExecute(JSONArray jsonA) {
                Log.d("KIBO_ENGAGE", "Fetch Chat: "+ jsonA.toString());
                try {
                    JSONObject row = jsonA.getJSONObject(0);
                    DatabaseHandler db = new DatabaseHandler(appContext);
                    String agentemail = row.getJSONArray("agentemail").getString(row.getJSONArray("agentemail").length()-1);
                    String agentid = row.getJSONArray("agentid").getString(row.getJSONArray("agentid").length()-1);
                    db.addChat(row.getString("to"), row.getString("from"), row.getString("uniqueid"),
                            row.getString("visitoremail"), agentemail, agentid, row.getString("is_seen"),
                            row.getString("type"), row.getString("status"), row.getString("msg"),
                            row.getString("request_id"), row.getString("messagechannel"), row.getString("companyid"),
                            row.getString("datetime"));
                    if (GroupChat.groupChat.isVisible) {
                        GroupChat.groupChat.receiveMessage(row.getString("msg"),
                                row.getString("uniqueid"), row.getString("from"),
                                row.getString("datetime"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute();

    }

}
