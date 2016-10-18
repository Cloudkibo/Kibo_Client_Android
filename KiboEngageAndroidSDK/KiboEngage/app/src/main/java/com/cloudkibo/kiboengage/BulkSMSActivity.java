package com.cloudkibo.kiboengage;

/**
 * Created by sojharo on 18/10/2016.
 */

import android.content.Context;
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


    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kiboengage_sdk_bulk_sms);

		/*contactName = this.getArguments().getString("contactusername");

		contactPhone = this.getArguments().getString("contactphone");

		authtoken = this.getArguments().getString("authtoken");*/

		/*DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());

		user = db.getUserDetails();*/

        loadConversationList();

        ListView list = (ListView) findViewById(R.id.list);
        adp = new BulkSMSAdapter(getApplicationContext());
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);
        adp.notifyDataSetChanged();


    }

	/* (non-Javadoc)
	 * @see com.socialshare.custom.CustomFragment#onClick(android.view.View)
	 */
    //@Override
	/*public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnSend)
		{
			sendMessage();
		} else if (v.getId() == R.id.btnCamera) {
			MainActivity act1 = (MainActivity)getActivity();

			act1.callThisPerson(contactPhone,
					 contactName);
		}

	}*/


    public void receiveMessage(String msg, String uniqueid, String from, String date) {

		/*try {

			final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
			mp.start();

			// todo see if this really needs the uniqueid and status
			convList.add(new Conversation(msg, Utility.convertDateToLocalTimeZoneAndReadable(date), false, true, "seen", uniqueid));

			adp.notifyDataSetChanged();

			sendMessageStatusUsingAPI("seen", uniqueid, from);
		} catch (ParseException e){
			e.printStackTrace();
		}*/

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

            //JSONArray jsonA = db.getChat(act1.getUserPhone(), contactPhone);

            ArrayList<BulkSMS> chatList1 = new ArrayList<BulkSMS>();

            chatList1.add(new BulkSMS("Summer Sale", "We are delighted to let our customers know about Summer Sale 2016. You would be happy to know that with coupon you can avail upto 60% discount on our products.",
                    Utility.convertDateToLocalTimeZoneAndReadable(Utility.getCurrentTimeInISO()), -1));

            chatList1.add(new BulkSMS("Summer Sale", "We are delighted to let our customers know about Summer Sale 2016. You would be happy to know that with coupon you can avail upto 60% discount on our products.",
                    Utility.convertDateToLocalTimeZoneAndReadable(Utility.getCurrentTimeInISO()), R.drawable.avatar));

            chatList1.add(new BulkSMS("Summer Sale", "We are delighted to let our customers know about Summer Sale 2016. You would be happy to know that with coupon you can avail upto 60% discount on our products.",
                    Utility.convertDateToLocalTimeZoneAndReadable(Utility.getCurrentTimeInISO()), -1));


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

}
