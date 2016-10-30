package com.cloudkibo.kiboengage;

/**
 * Created by sojharo on 18/10/2016.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.library.Utility;
import com.cloudkibo.kiboengage.model.BulkSMS;
import com.cloudkibo.kiboengage.model.GroupItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


/**
 * The Class GroupChat is the Fragment class that is launched when the user
 * clicks on a Chat item in ChatList fragment. The current implementation simply
 * shows dummy conversations and when you send a chat message it will show a
 * dummy auto reply message. You can write your own code for actual Chat.
 */
public class BulkSMSActivity extends AppCompatActivity
{

    /** The Conversation list. */
    private ArrayList<BulkSMS> convList;

    /** The chat adapter. */
    private BulkSMSAdapter adp;

    public static BulkSMSActivity bulkSMSActivity;
    public static Boolean isVisible = false;


    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiboengage_sdk_bulk_sms);

        bulkSMSActivity = this;

        loadConversationList();

        ListView list = (ListView) findViewById(R.id.list);
        adp = new BulkSMSAdapter(getApplicationContext());
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);
        adp.notifyDataSetChanged();


    }


    public void receiveMessage(JSONObject row) {

		try {

			final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
			mp.start();

			convList.add(new BulkSMS(row.getString("title"), row.getString("description"),
                    Utility.convertDateToLocalTimeZoneAndReadable(row.getString("datetime")), -1));

            if(adp != null)
			    adp.notifyDataSetChanged();

		} catch (JSONException e){
			e.printStackTrace();
		} catch (ParseException e){
            e.printStackTrace();
        }

    }



    /**
     * This method currently loads a dummy list of conversations. You can write the
     * actual implementation of loading conversations.
     */
    public void loadConversationList()
    {
        convList = new ArrayList<BulkSMS>();

        loadChatFromDatabase();

    }


    public void loadChatFromDatabase(){
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        try {

            JSONArray jsonA = db.getBulkSMS();

            ArrayList<BulkSMS> chatList1 = new ArrayList<BulkSMS>();

            for (int i=0; i < jsonA.length(); i++) {
                JSONObject row = jsonA.getJSONObject(i);
                chatList1.add(new BulkSMS(row.getString("title"), row.getString("description"),
                        Utility.convertDateToLocalTimeZoneAndReadable(row.getString("datetime")), -1));
            }

            convList.clear();

            convList.addAll(chatList1);

            if(adp != null)
                adp.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The Class CutsomAdapter is the adapter class for Chat ListView. The
     * currently implementation of this adapter simply display static dummy
     * contents. You need to write the code for displaying actual contents.
     */
    private class BulkSMSAdapter extends BaseAdapter
    {

        Context context;

        public BulkSMSAdapter(Context appContext){
            context = appContext;
        }
        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return convList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public BulkSMS getItem(int arg0)
        {
            return convList.get(arg0);
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
            BulkSMS c = getItem(pos);

            v = LayoutInflater.from(this.context).inflate(
                    R.layout.kiboengage_sdk_bulk_sms_item, null);

            TextView lbl = (TextView) v.findViewById(R.id.lblContactDisplayName);
            lbl.setText(c.getMsg());

            lbl = (TextView) v.findViewById(R.id.lbl2);
            lbl.setText(c.getTitle());

            lbl = (TextView) v.findViewById(R.id.lblContactPhone);
            lbl.setText(c.getDate().replaceAll("-", "/").split("/",2)[1]);

            ImageView img = (ImageView) v.findViewById(R.id.imageView);
            if(c.getImage() < 0) img.setVisibility(View.GONE);

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

    public void ToastNotify(JSONObject row) {
        receiveMessage(row);
    }

}
