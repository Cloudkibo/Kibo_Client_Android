package com.cloudkibo.kiboengage;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudkibo.kiboengage.azure.MyHandler;
import com.cloudkibo.kiboengage.azure.NotificationSettings;
import com.cloudkibo.kiboengage.azure.RegistrationIntentService;
import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.model.GroupItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Push Notification Work
    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // List Work
    private ArrayList<GroupItem> groupList;
    private GroupAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiboengage_sdk_group_list);

        // Push Notifications work
        mainActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();

        loadGroupList();
        ListView list = (ListView) findViewById(R.id.list);
        adp = new GroupAdapter(getApplicationContext());
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3)
            {
                startActivity(new Intent(getApplicationContext(), Channels.class));
            }
        });
        adp.notifyDataSetChanged();

    }

    public void loadGroupList()
    {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        try{

            ArrayList<GroupItem> tempGroupList = new ArrayList<GroupItem>();

            JSONArray chats = db.getGroups();

            for (int i=0; i < chats.length(); i++) {
                JSONObject row = chats.getJSONObject(i);

                tempGroupList.add(new GroupItem(
                        row.getString("deptname")
                        ));

            }

            this.groupList = new ArrayList<GroupItem>(tempGroupList);

            if(adp != null)
                adp.notifyDataSetChanged();

        } catch(JSONException e){
            e.printStackTrace();
        }


    }

    private class GroupAdapter extends BaseAdapter
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


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                //TextView helloText = (TextView) findViewById(R.id.text_hello);
                //helloText.setText(notificationMessage);
            }
        });
    }
}
