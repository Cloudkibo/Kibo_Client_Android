package com.cloudkibo.kiboengage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.model.ChannelItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sojharo on 16/10/2016.
 */
public class Channels extends AppCompatActivity {

    public static Boolean isVisible = false;

    // List Work
    private ArrayList<ChannelItem> channelList;
    private ChannelAdapter adp;

    private String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiboengage_sdk_message_channel);

        groupid = getIntent().getExtras().getString("groupid");

        loadChannelList();
        ListView list = (ListView) findViewById(R.id.list);
        adp = new ChannelAdapter(getApplicationContext());
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3)
            {
                startActivity(new Intent(getApplicationContext(), GroupChat.class));
            }
        });
        adp.notifyDataSetChanged();

    }

    public void loadChannelList()
    {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        try{

            ArrayList<ChannelItem> tempChannelList = new ArrayList<ChannelItem>();

            JSONArray chats = db.getMessageChannels(groupid); // todo change to just group id

            for (int i=0; i < chats.length(); i++) {
                JSONObject row = chats.getJSONObject(i);

                tempChannelList.add(new ChannelItem(
                        row.getString("msg_channel_name")
                ));

            }

            this.channelList = new ArrayList<ChannelItem>(tempChannelList);

            if(adp != null)
                adp.notifyDataSetChanged();

        } catch(JSONException e){
            e.printStackTrace();
        }


    }

    private class ChannelAdapter extends BaseAdapter
    {
        Context context;

        public ChannelAdapter(Context appContext){
            context = appContext;
        }
        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return channelList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public ChannelItem getItem(int arg0)
        {
            return channelList.get(arg0);
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

            ChannelItem c = getItem(pos);
            TextView lbl = (TextView) v.findViewById(R.id.lblContactDisplayName);
            lbl.setText(c.getName());

            return v;
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

}
