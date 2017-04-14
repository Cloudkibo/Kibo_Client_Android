package com.cloudkibo.kiboengage.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



import org.apache.http.NameValuePair;


public class UserFunctions {


    /////////////////////////////////////////////////////////////////////
    // VARIABLES                                                       //
    /////////////////////////////////////////////////////////////////////

    private ConnectionManager connection;

    private static String baseURL = "https://api.kibosupport.com";

    private static String fetchGroupsURL =          baseURL + "/api/departments/";
    private static String fetchChannelsURL =        baseURL + "/api/messagechannels/";
    private static String createSessionsURL =       baseURL + "/api/visitorcalls/createbulksession/";
    private static String getSessionsURL =          baseURL + "/api/visitorcalls/getcustomersessions";
    private static String getBulkSMSURL =           baseURL + "/api/notifications/fetchbulksms";
    private static String getBulkSMSListURL =       baseURL + "/api/notifications/";
    private static String saveChatURL =             baseURL + "/api/userchats/create";
    private static String sendChatURL =             "https://kiboengage.kibosupport.com/api/getchat";
    private static String fetchChatURL =            baseURL + "/api/userchats/fetchChat";


    //URL of the NODEJS API
    private static String loginURL =                baseURL + "/auth/local";
    private static String registerURL =             baseURL + "/api/users/";
    private static String userDataURL =             baseURL + "/api/users/me";
    private static String getChatURL =              baseURL + "/api/userchat";
    private static String markChatReadURL =         baseURL + "/api/userchat/markasread";
    private static String saveContactURL =          baseURL + "/api/contactslist/addbyusername";
    private static String forpassURL =              baseURL + "/api/users/resetpasswordrequest";
    private static String chgpassURL =              baseURL + "/learn2crack_login_api/";
    private static String getContactsURL =          baseURL + "/api/contactslist/";
    private static String getPendingContactsURL =   baseURL + "/api/contactslist/pendingcontacts/";
    private static String approveContactURL =       baseURL + "/api/contactslist/approvefriendrequest/";
    private static String rejectContactURL =        baseURL + "/api/contactslist/rejectfriendrequest/";
    private static String removeChatURL =           baseURL + "/api/userchat/removechathistory/";
    private static String removeContactURL =        baseURL + "/api/contactslist/removefriend/";
    private static String phoneContactsURL =        baseURL + "/api/users/searchaccountsbyphone/";
    private static String emailContactsURL =        baseURL + "/api/users/searchaccountsbyemail/";
    private static String inviteContactsURL =       baseURL + "/api/users/invitebymultipleemail/";
    private static String saveDisplayNameURL =      baseURL + "/api/users/newuser";
    private static String getAllChatURL =           baseURL + "/api/userchat/alluserchat";
    private static String sendChatStatusURL =             baseURL + "/api/userchat/updateStatus";
    private static String getPartialChatURL =       baseURL + "/api/userchat/partialchatsync";
    private static String getSingleChatURL =        baseURL + "/api/userchat/getsinglechat";
    private static String sendLogURL =              baseURL + "/api/users/log";







    /////////////////////////////////////////////////////////////////////
    // Constructor                                                     //
    /////////////////////////////////////////////////////////////////////

    public UserFunctions(){
        connection = new ConnectionManager();
    }






    public JSONObject getUserData(String appId, String clientId, String appSecret) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject userdata = connection.getDataFromServer(userDataURL, appId, clientId, appSecret);
        return userdata;
    }

    public JSONArray getGroupsList(String appId, String clientId, String appSecret) {
        JSONArray contactslist = connection.getArrayFromServer(fetchGroupsURL, appId, clientId, appSecret);
        return contactslist;
    }

    public JSONArray getChannelsList(String appId, String clientId, String appSecret) {
        JSONArray contactslist = connection.getArrayFromServer(fetchChannelsURL, appId, clientId, appSecret);
        return contactslist;
    }

    public JSONArray getBulkSMSList(String appId, String clientId, String appSecret) {
        JSONArray contactslist = connection.getArrayFromServer(getBulkSMSListURL, appId, clientId, appSecret);
        return contactslist;
    }

    public JSONArray getSessions(List<NameValuePair> params, String appId, String clientId, String appSecret) {
        JSONArray userchatresponse = connection.sendArrayToServer(getSessionsURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    public JSONObject createSession(JSONObject params, String appId, String clientId, String appSecret) {
        JSONObject userchatresponse = connection.sendJSONObjectToServer(createSessionsURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    public JSONObject getSpecificBulkSMS(List<NameValuePair> params, String appId, String clientId, String appSecret) {
        JSONObject userchatresponse = connection.sendObjectToServer(getBulkSMSURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    public JSONObject saveChat(List<NameValuePair> params, String appId, String clientId, String appSecret) {
        JSONObject userchatresponse = connection.sendObjectToServer(saveChatURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    public JSONObject sendChat(List<NameValuePair> params, String appId, String clientId, String appSecret) {
        JSONObject userchatresponse = connection.sendObjectToServer(sendChatURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    public JSONArray fetchChat(List<NameValuePair> params, String appId, String clientId, String appSecret) {
        JSONArray userchatresponse = connection.sendArrayToServer(fetchChatURL, appId, clientId, appSecret, params);
        return userchatresponse;
    }

    /*public JSONObject sendChatMessageToServer(JSONObject data, String appId, String clientId, String appSecret) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try{
            params.add(new BasicNameValuePair("from", data.getString("from")));
            params.add(new BasicNameValuePair("to", data.getString("to")));
            params.add(new BasicNameValuePair("fromFullName", data.getString("fromFullName")));
            params.add(new BasicNameValuePair("msg", data.getString("msg")));
            params.add(new BasicNameValuePair("date", data.getString("date")));
            params.add(new BasicNameValuePair("uniqueid", data.getString("uniqueid")));
            params.add(new BasicNameValuePair("type", data.getString("type")));
            params.add(new BasicNameValuePair("file_type", data.getString("file_type")));
        } catch (JSONException e){
            e.printStackTrace();
        }
        JSONObject response = connection.sendObjectToServer(saveChatURL, appId, clientId, appSecret, params);
        return response;
    }*/

    /*public JSONObject sendChatMessageStatusToServer(JSONObject data, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        try{
            params.add(new BasicNameValuePair("sender", data.getString("sender")));
            params.add(new BasicNameValuePair("status", data.getString("status")));
            params.add(new BasicNameValuePair("uniqueid", data.getString("uniqueid")));
        } catch (JSONException e){
            e.printStackTrace();
        }
        JSONObject response = connection.sendObjectToServer(sendChatStatusURL, authtoken, params);
        return response;
    }*/

    /*public JSONObject getAllChatList(String user1, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        JSONObject response = connection.sendObjectToServer(getAllChatURL, authtoken, params);
        return response;
    }*/

    /*public JSONObject getPartialChatList(String user1, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        JSONObject response = connection.sendObjectToServer(getPartialChatURL, authtoken, params);
        return response;
    }*/


    /*public JSONObject removeChat(String user1, String user2, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", user2));
        JSONObject userchatresponse = connection.sendObjectToServer(removeChatURL, authtoken, params);
        return userchatresponse;
    }*/

    public static JSONArray sortJSONArray(JSONArray jsonArr, String key) {

        JSONArray sortedJsonArray = new JSONArray();
        final String myKey = key;

        try{
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArr.length(); i++) {
                jsonValues.add(jsonArr.getJSONObject(i));
            }
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                private final String KEY_NAME = myKey;

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(KEY_NAME);
                        valB = (String) b.get(KEY_NAME);
                    } catch (JSONException e) {
                        //do something
                    }

                    return valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });

            for (int i = 0; i < jsonArr.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }
        } catch (JSONException ex ){
            ex.printStackTrace();
        }

        return sortedJsonArray;


    }

}

