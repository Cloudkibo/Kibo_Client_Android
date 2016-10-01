package com.cloudkibo.kiboengage;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudkibo.database.DatabaseHandler;


import android.content.Context;


public class UserFunctions {


    /////////////////////////////////////////////////////////////////////
    // VARIABLES                                                       //
    /////////////////////////////////////////////////////////////////////

    private ConnectionManager connection;

    private static String baseURL = "https://api.cloudkibo.com";

    //URL of the NODEJS API
    private static String loginURL =                baseURL + "/auth/local";
    private static String registerURL =             baseURL + "/api/users/";
    private static String userDataURL =             baseURL + "/api/users/me";
    private static String saveChatURL =             baseURL + "/api/userchat/save";
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
    private static String sendChatURL =             baseURL + "/api/userchat/save2";
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







    /**
     * Function to Login
     * @throws Exception
     **/

    public String loginUser(String username, String password) throws Exception{
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        String authtoken = connection.getTokenFromServer(loginURL, params);
        return authtoken;
    }









    /**
     * Function to change password
     * @throws Exception
     **/

    public String chgPass(String newpas, String email) throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        String result = connection.getTokenFromServer(chgpassURL, params);
        return result;
        // Add this feature in future
    }





    /**
     * Function to request server for password reset link. It takes the user email address.
     * @throws Exception
     **/

    public JSONObject forgotPass(String email) throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("email", email));
        JSONObject response = connection.sendObjectToServerNoAuth(forpassURL, params);
        return response;
    }

    public JSONObject sendLog(String data) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", data));
        JSONObject response = connection.sendObjectToServerNoAuth(sendLogURL, params);
        return response;
    }






    /**
     * Function to Register
     * @throws Exception
     **/
    public String registerUser(String fname, String lname, String email, String uname, String password, String phone) throws Exception{
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("firstname", fname));
        params.add(new BasicNameValuePair("lastname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("username", uname));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("phone", phone));
        String authtoken = connection.getTokenFromServer(registerURL, params);
        return authtoken;
    }







    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }







    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }





    public JSONObject setDisplayName(List<NameValuePair> params, String authtoken) {
        JSONObject response = connection.sendObjectToServer(saveDisplayNameURL, authtoken, params);
        return response;
    }

    public JSONObject getUserData(String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject userdata = connection.getDataFromServer(userDataURL, authtoken);
        return userdata;
    }


    public JSONArray getContactsList(String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONArray contactslist = connection.getArrayFromServer(getContactsURL, authtoken);
        return contactslist;
    }

    public JSONObject sendChatMessageToServer(JSONObject data, String authtoken) {
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
        JSONObject response = connection.sendObjectToServer(sendChatURL, authtoken, params);
        return response;
    }

    public JSONObject sendChatMessageStatusToServer(JSONObject data, String authtoken) {
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
    }

    public JSONObject getAllChatList(String user1, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        JSONObject response = connection.sendObjectToServer(getAllChatURL, authtoken, params);
        return response;
    }

    public JSONObject getPartialChatList(String user1, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        JSONObject response = connection.sendObjectToServer(getPartialChatURL, authtoken, params);
        return response;
    }

    public JSONObject getSingleChat(String id, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uniqueid", id));
        JSONObject response = connection.sendObjectToServer(getSingleChatURL, authtoken, params);
        return response;
    }

    public JSONArray getPendingContactsList(String authtoken) {
        JSONArray contactslist = connection.getArrayFromServer(getPendingContactsURL, authtoken);
        return contactslist;
    }

    public JSONObject saveChat(List<NameValuePair> params, String authtoken) {
        JSONObject userchatresponse = connection.sendObjectToServer(saveChatURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject sendAddressBookNumberContactsToServer(List<NameValuePair> params, String authtoken) {
        JSONObject contactresponse = connection.sendObjectToServer(phoneContactsURL, authtoken, params);
        return contactresponse;
    }

    public JSONObject sendAddressBookEmailContactsToServer(List<NameValuePair> params, String authtoken) {
        JSONObject contactresponse = connection.sendObjectToServer(emailContactsURL, authtoken, params);
        return contactresponse;
    }

    public JSONObject sendAddressBookPhoneContactsToServer(List<NameValuePair> params, String authtoken) {
        JSONObject contactresponse = connection.sendObjectToServer(phoneContactsURL, authtoken, params);
        return contactresponse;
    }

    public JSONObject sendEmailsOfInvitees(List<NameValuePair> params, String authtoken) {
        JSONObject contactresponse = connection.sendObjectToServer(inviteContactsURL, authtoken, params);
        return contactresponse;
    }

    public JSONObject markChatAsRead(String user1, String user2, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        params.add(new BasicNameValuePair("user2", user2));
        JSONObject userchatresponse = connection.sendObjectToServer(markChatReadURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject removeChat(String user1, String user2, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", user2));
        JSONObject userchatresponse = connection.sendObjectToServer(removeChatURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject removeContact(String user1, String user2, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", user2));
        JSONObject userchatresponse = connection.sendObjectToServer(removeContactURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject acceptFriendRequest(List<NameValuePair> param, String authtoken) {
        List<NameValuePair> params = param;
        JSONObject userchatresponse = connection.sendObjectToServer(approveContactURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject rejectFriendRequest(List<NameValuePair> param, String authtoken) {
        List<NameValuePair> params = param;
        JSONObject userchatresponse = connection.sendObjectToServer(rejectContactURL, authtoken, params);
        return userchatresponse;
    }

    public JSONObject getUserChat(String user1, String user2, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user1", user1));
        params.add(new BasicNameValuePair("user2", user2));
        JSONObject response = connection.sendObjectToServer(getChatURL, authtoken, params);
        return response;
    }

    public JSONObject saveContact(String username, String authtoken) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("searchusername", username));
        JSONObject response = connection.sendObjectToServer(saveContactURL, authtoken, params);
        return response;
    }

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

