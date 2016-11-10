package com.cloudkibo.kiboengage;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudkibo.kiboengage.database.DatabaseHandler;
import com.cloudkibo.kiboengage.library.Utility;
import com.cloudkibo.kiboengage.model.Conversation;
import com.cloudkibo.kiboengage.network.UserFunctions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * The Class GroupChat is the Fragment class that is launched when the user
 * clicks on a Chat item in ChatList fragment. The current implementation simply
 * shows dummy conversations and when you send a chat message it will show a
 * dummy auto reply message. You can write your own code for actual Chat.
 */
public class GroupChat extends AppCompatActivity
{

	/** The Conversation list. */
	private ArrayList<Conversation> convList;

	/** The chat adapter. */
	private ChatAdapter adp;

	/** The Editext to compose the message. */
	private EditText txt;

	private HashMap<String, String> user;
	
	private String groupId;
	private String channelId;
	private String channelName;
	private JSONObject session;

	private Button sendBtn;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kiboengage_sdk_group_chat);

		groupId = getIntent().getExtras().getString("groupid");
		channelId = getIntent().getExtras().getString("channelid");
		channelName = getIntent().getExtras().getString("msg_channel_name");

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		user = db.getUserDetails();
		try {
			db = new DatabaseHandler(getApplicationContext());
			session = db.getSession(groupId, channelId);
		} catch (JSONException e){
			e.printStackTrace();
		}

		loadConversationList();
		
		ListView list = (ListView) findViewById(R.id.list);
		adp = new ChatAdapter(getApplicationContext());
		list.setAdapter(adp);
		list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		list.setStackFromBottom(true);
		adp.notifyDataSetChanged();

		txt = (EditText) findViewById(R.id.txt);
		txt.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		sendBtn = (Button) findViewById(R.id.btnSend);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMessage();
			}
		});

	}

	/**
	 * Call this method to Send message to opponent. The current implementation
	 * simply add an auto reply message with each sent message.
	 * You need to write actual logic for sending and receiving the messages.
	 */
	private void sendMessage()
	{
		try {
			if (txt.length() == 0)
				return;

			String uniqueid = Utility.generateUniqueId();

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

			String messageString = txt.getText().toString();

			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			if(session.getString("agent_email").equals("")) {
				// todo add more customer information
				db.addChat("All Agents", user.get("customerId"), uniqueid, "", "", "", "no", "message", "pending", messageString,
						session.getString("request_id"), channelId, user.get("clientId"), Utility.getCurrentTimeInISO());
			} else {
				db.addChat(session.getString("agent_name"), user.get("customerId"), uniqueid, "", session.getString("agent_email"), session.getString("agent_id"), "no", "message", "pending", messageString,
						session.getString("request_id"), channelId, user.get("clientId"), Utility.getCurrentTimeInISO());
			}

			//convList.add(new Conversation(messageString, Utility.convertDateToLocalTimeZoneAndReadable(Utility.getCurrentTimeInISO()), true, true, "pending", uniqueid));
			convList.add(new Conversation(
					messageString,
					Utility.convertDateToLocalTimeZoneAndReadable(Utility.getCurrentTimeInISO()),
					true, true, "pending", uniqueid, "message"));
			adp.notifyDataSetChanged();

			saveMessageUsingAPI(messageString, uniqueid);

			txt.setText(null);
		} catch (ParseException e){
			e.printStackTrace();
		} catch (JSONException e){
			e.printStackTrace();
		}
	}
	
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

	public void saveMessageUsingAPI(final String msg, final String uniqueid){
		new AsyncTask<String, String, JSONObject>() {

			@Override
			protected JSONObject doInBackground(String... args) {
				UserFunctions userFunction = new UserFunctions();
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				try {
					// todo add more customer information
					if(session.getString("agent_email").equals("")) {
						params.add(new BasicNameValuePair("from", user.get("customerId")));
						params.add(new BasicNameValuePair("to", "All Agents"));
						params.add(new BasicNameValuePair("visitoremail", ""));
						params.add(new BasicNameValuePair("msg", msg));
						params.add(new BasicNameValuePair("uniqueid", uniqueid));
						params.add(new BasicNameValuePair("type", "message"));
						params.add(new BasicNameValuePair("datetime", Utility.getCurrentTimeInISO()));
						params.add(new BasicNameValuePair("request_id", session.getString("request_id")));
						params.add(new BasicNameValuePair("messagechannel", channelId));
						params.add(new BasicNameValuePair("companyid", user.get("clientId")));
						params.add(new BasicNameValuePair("is_seen", "no"));
						params.add(new BasicNameValuePair("time", (new Date().getHours()) + "," + (new Date().getMinutes())));
						params.add(new BasicNameValuePair("fromMobile", "yes"));
					} else {
						params.add(new BasicNameValuePair("from", user.get("customerId")));
						params.add(new BasicNameValuePair("to", session.getString("agent_name")));
						params.add(new BasicNameValuePair("visitoremail", ""));
						params.add(new BasicNameValuePair("agentemail", session.getString("agent_email")));
						params.add(new BasicNameValuePair("toagent", session.getString("agent_email")));
						params.add(new BasicNameValuePair("agentid", session.getString("agent_id")));
						params.add(new BasicNameValuePair("msg", msg));
						params.add(new BasicNameValuePair("uniqueid", uniqueid));
						params.add(new BasicNameValuePair("type", "message"));
						params.add(new BasicNameValuePair("datetime", Utility.getCurrentTimeInISO()));
						params.add(new BasicNameValuePair("request_id", session.getString("request_id")));
						params.add(new BasicNameValuePair("messagechannel", channelId));
						params.add(new BasicNameValuePair("companyid", user.get("clientId")));
						params.add(new BasicNameValuePair("is_seen", "no"));
						params.add(new BasicNameValuePair("socketid", "no socket id given, we dont use socket"));
						params.add(new BasicNameValuePair("time", (new Date().getHours()) + "," + (new Date().getMinutes())));
						params.add(new BasicNameValuePair("fromMobile", "yes"));
					}
				} catch (JSONException e){
					e.printStackTrace();
				}
				Log.d("GroupChat Widget", "Sending params to save chat "+ params.toString());

				return userFunction.saveChat(params, user.get("appId"), user.get("clientId"), user.get("appSecret"));
			}

			@Override
			protected void onPostExecute(JSONObject row) {
				try {

					Log.d("GroupChat Widget", "Got response to save chat "+ row.toString());
					if (row != null) {
						if(row.has("status")){
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							db.updateChat(row.getString("status"), row.getString("uniqueid"));
							updateStatusSentMessage(row.getString("status"), row.getString("uniqueid"));
						}
					}
					sendMessageUsingAPI(msg, uniqueid);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.execute();
	}

	public void sendMessageUsingAPI(final String msg, final String uniqueid){
		new AsyncTask<String, String, JSONObject>() {

			@Override
			protected JSONObject doInBackground(String... args) {
				UserFunctions userFunction = new UserFunctions();
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				try {
					// todo add more customer information
					if(session.getString("agent_email").equals("")) {
						params.add(new BasicNameValuePair("from", user.get("customerId")));
						params.add(new BasicNameValuePair("to", "All Agents"));
						params.add(new BasicNameValuePair("visitoremail", ""));
						params.add(new BasicNameValuePair("msg", msg));
						params.add(new BasicNameValuePair("uniqueid", uniqueid));
						params.add(new BasicNameValuePair("type", "message"));
						params.add(new BasicNameValuePair("datetime", Utility.getCurrentTimeInISO()));
						params.add(new BasicNameValuePair("request_id", session.getString("request_id")));
						params.add(new BasicNameValuePair("messagechannel", channelId));
						params.add(new BasicNameValuePair("companyid", user.get("clientId")));
						params.add(new BasicNameValuePair("is_seen", "no"));
						params.add(new BasicNameValuePair("time", (new Date().getHours()) + "," + (new Date().getMinutes())));
						params.add(new BasicNameValuePair("fromMobile", "yes"));
					} else {
						params.add(new BasicNameValuePair("from", user.get("customerId")));
						params.add(new BasicNameValuePair("to", session.getString("agent_name")));
						params.add(new BasicNameValuePair("visitoremail", ""));
						params.add(new BasicNameValuePair("agentemail", session.getString("agent_email")));
						params.add(new BasicNameValuePair("toagent", session.getString("agent_email")));
						params.add(new BasicNameValuePair("agentid", session.getString("agent_id")));
						params.add(new BasicNameValuePair("msg", msg));
						params.add(new BasicNameValuePair("uniqueid", uniqueid));
						params.add(new BasicNameValuePair("type", "message"));
						params.add(new BasicNameValuePair("datetime", Utility.getCurrentTimeInISO()));
						params.add(new BasicNameValuePair("request_id", session.getString("request_id")));
						params.add(new BasicNameValuePair("messagechannel", channelId));
						params.add(new BasicNameValuePair("companyid", user.get("clientId")));
						params.add(new BasicNameValuePair("is_seen", "no"));
						params.add(new BasicNameValuePair("socketid", "no socket id given, we dont use socket"));
						params.add(new BasicNameValuePair("time", (new Date().getHours()) + "," + (new Date().getMinutes())));
						params.add(new BasicNameValuePair("fromMobile", "yes"));
					}
				} catch (JSONException e){
					e.printStackTrace();
				}
				Log.d("GroupChat Widget", "Sending params to send chat "+ params.toString());

				return userFunction.sendChat(params, user.get("appId"), user.get("clientId"), user.get("appSecret"));
			}

			@Override
			protected void onPostExecute(JSONObject row) {
				Log.d("GroupChat Widget", "Got response to send chat "+ row.toString());
			}

		}.execute();
	}

	public void sendMessageStatusUsingAPI(final String status, final String uniqueid, final String sender){
		/*new AsyncTask<String, String, JSONObject>() {

			@Override
			protected JSONObject doInBackground(String... args) {
				UserFunctions userFunction = new UserFunctions();
				JSONObject message = new JSONObject();

				try {
					message.put("sender", sender);
					message.put("status", status);
					message.put("uniqueid", uniqueid);
				} catch (JSONException e){
					e.printStackTrace();
				}

				return userFunction.sendChatMessageStatusToServer(message, authtoken);
			}

			@Override
			protected void onPostExecute(JSONObject row) {
				try {

					Boolean gotGoodServerResponse = false;
					if (row != null) {
						if(row.has("status")){
							MainActivity act1 = (MainActivity) getActivity();
							act1.resetSpecificChatHistorySync(row.getString("uniqueid"));
							act1.updateChatStatus(row.getString("status"), row.getString("uniqueid"));
							gotGoodServerResponse = true;
						}
					}
					if(!gotGoodServerResponse){
						MainActivity act1 = (MainActivity) getActivity();
						act1.updateChatStatus("seen", row.getString("uniqueid"));
						act1.addChatHistorySync(row.getString("uniqueid"), row.getString("fromperson"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.execute();*/
	}

	public void updateStatusSentMessage(String status, String uniqueid){
		for(int i=0; i<convList.size(); i++){
			if(convList.get(i).getUniqueid().equals(uniqueid)){
				convList.get(i).setStatus(status);
				break;
			}
		}
		adp.notifyDataSetChanged();
	}
	

	/**
	 * This method currently loads a dummy list of conversations. You can write the
	 * actual implementation of loading conversations.
	 */
	public void loadConversationList()
	{
		convList = new ArrayList<Conversation>();
		
		loadChatFromDatabase();

	}

	
	public void loadChatFromDatabase(){
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
		try {

			JSONArray jsonA = db.getChat(session.getString("request_id"));

			ArrayList<Conversation> chatList1 = new ArrayList<Conversation>();

			for (int i=0; i < jsonA.length(); i++) {
				JSONObject row = jsonA.getJSONObject(i);
				
				if(row.getString("from_user").equals(user.get("customerId")))
					chatList1.add(new Conversation(
							row.getString("msg"),
							Utility.convertDateToLocalTimeZoneAndReadable(row.getString("datetime")),
							true, true, row.getString("status"), row.getString("uniqueid"), row.getString("type")));
				else
					chatList1.add(new Conversation(
							row.getString("msg"),
							Utility.convertDateToLocalTimeZoneAndReadable(row.getString("datetime")),
							false, true, row.getString("status"), row.getString("uniqueid"), row.getString("type")));

				// todo
				/*if(row.getString("fromperson").equals(contactPhone)){
					if(row.getString("status").equals("delivered")){
						sendMessageStatusUsingAPI("seen", row.getString("uniqueid"), row.getString("fromperson"));
					}
				}*/
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
	private class ChatAdapter extends BaseAdapter
	{

		Context context;

		public ChatAdapter(Context appContext){
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
		public Conversation getItem(int arg0)
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
			Conversation c = getItem(pos);

			if(c.getType().equals("message")){
				if (c.isSent())
					v = LayoutInflater.from(this.context).inflate(
							R.layout.kiboengage_sdk_chat_item_sent, null);
				else
					v = LayoutInflater.from(this.context).inflate(
							R.layout.kiboengage_sdk_chat_item_rcv, null);

				TextView lbl = (TextView) v.findViewById(R.id.lblContactDisplayName);
				lbl.setText(c.getDate().replaceAll("-", "/").split("/",2)[1]);

				lbl = (TextView) v.findViewById(R.id.lbl2);
				lbl.setText(c.getMsg());

				lbl = (TextView) v.findViewById(R.id.lblContactPhone);
				if (c.isSuccess())
					lbl.setText(c.getStatus());
				else
					lbl.setText("");

			} else {
				v = LayoutInflater.from(this.context).inflate(
						R.layout.kiboengage_sdk_log_message_item, null);

				TextView lbl = (TextView) v.findViewById(R.id.lbl2);
				lbl.setText(c.getMsg());
			}

			return v;
		}

	}

}
