package com.cloudkibo.kiboengage.azure;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cloudkibo.kiboengage.BulkSMSActivity;
import com.cloudkibo.kiboengage.MainActivity;
import com.cloudkibo.kiboengage.R;
import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.library.Utility;
import com.cloudkibo.kiboengage.model.BulkSMS;
import com.cloudkibo.kiboengage.network.UserFunctions;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sojharo on 09/09/2016.
 */
public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        processNotification(nhMessage);
        if (MainActivity.isVisible) {
            MainActivity.mainActivity.ToastNotify(nhMessage);
        }
    }

    private void processNotification(String msg){
        JSONObject payload;
        try{
            payload = new JSONObject(msg);
            payload = payload.getJSONObject("data");
            if(payload.has("type")) {
                if(payload.getString("type").equals("bulksms"))
                    loadBulkSmsFromServer(payload);
            } else if(payload.has("tablename") && payload.has("operation")) {
                if(payload.getString("tablename").equals("Channels")){
                    Utility.handleChannelNotification(ctx, payload);
                } else {
                    Utility.handleGroupNotification(ctx, payload);
                }
            } else if(payload.has("uniqueid") && payload.has("request_id")) {
                Utility.fetchChatMessage(ctx, payload);
            } else {
                sendNotification(msg);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotification(String type, String title, String msg) {

        Intent intent = new Intent(ctx, MainActivity.class);

        if(type.equals("bulksms")) intent = new Intent(ctx, BulkSMSActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void loadBulkSmsFromServer(JSONObject payload) {

        try {
            final String uniqueid = payload.getString("uniqueid");

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
                                    row.getString("agent_id"), row.getString("hasImage"),
                                    (row.has("image_url")) ? row.getString("image_url") : "",
                                    row.getString("companyid"), row.getString("datetime"));

                            String description = row.getString("description");
                            if(description.length() > 30) description = description.substring(0, 29);
                            if(BulkSMSActivity.isVisible) BulkSMSActivity.bulkSMSActivity.ToastNotify(row);
                            else sendNotification("bulksms", description, row.getString("title"));

                        } else {
                            //Utility.sendLogToServer(""+ userDetail.get("phone") +" did not get message from API. SERVER gave NULL");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }.execute();
        }catch(JSONException e){
            e.printStackTrace();
        }

    }
}
