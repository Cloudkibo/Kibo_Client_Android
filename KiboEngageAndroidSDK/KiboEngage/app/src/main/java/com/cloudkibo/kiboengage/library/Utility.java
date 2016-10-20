package com.cloudkibo.kiboengage.library;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.network.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

        /*
        TimeZone tz = TimeZone.getDefault();
        String gmt1= TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.SHORT);
        String gmt2= TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.LONG);
        Log.d("Tag","TimeZone : "+gmt1+"\t"+gmt2);

        boolean additionRequired = (gmt1.contains("+"));
        String hourOffset = gmt1.substring(4, 6);
        String minuteOffset = gmt1.substring(7, 9);

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(date); // sets calendar time/date
        if(additionRequired) {
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOffset)); // adds hours
            cal.add(Calendar.MINUTE, Integer.parseInt(minuteOffset)); // adds minutes
        } else {
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(hourOffset) * (-1)); // adds hours
            cal.add(Calendar.MINUTE, Integer.parseInt(minuteOffset) * (-1)); // adds minutes
        }
        Date date2 = cal.getTime();

        cal.setTime(date2);

        return  String.format("%02d", cal.get(Calendar.DATE)) +"-"+
                String.format("%02d", (cal.get(Calendar.MONTH)+1)) +"-"+
                String.format("%02d", cal.get(Calendar.YEAR)) +" "+
                String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) +":"+
                String.format("%02d", cal.get(Calendar.MINUTE));
        */
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

    public static void loadBulkSmsFromServer(final Context ctx, final String uniqueid) {

        new AsyncTask<String, String, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... args) {
                DatabaseHandler db = new DatabaseHandler(ctx);
                HashMap<String, String> user = db.getUserDetails();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", uniqueid));
                UserFunctions userFunction = new UserFunctions();
                return userFunction.getSpecificBulkSMS(params, user.get("appId"), user.get("clientId"), user.get("appSecret"));
            }

            @Override
            protected void onPostExecute(JSONObject row) {
                try {

                    if (row != null) {
                        DatabaseHandler db = new DatabaseHandler(
                                ctx);

                        Log.i("MyHandler", row.toString());

                        db.addBulkSMS(row.getString("title"), row.getString("description"),
                                row.getString("agent_id"), row.getString("hasImage"), row.getString("image_url"),
                                row.getString("companyid"), row.getString("datetime"));

                    } else {
                        //Utility.sendLogToServer(""+ userDetail.get("phone") +" did not get message from API. SERVER gave NULL");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute();

    }

}
