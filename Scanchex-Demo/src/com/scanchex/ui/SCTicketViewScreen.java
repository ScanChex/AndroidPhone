package com.scanchex.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.scanchex.adapters.SCTicketsAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCTicketViewScreen extends ListActivity implements
		OnItemLongClickListener {

	private SCTicketsAdapter adapter;
	ListView listView;
	private TextView noMessageText;
	private ImageView newMessageIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources.getResources().setFirstScanDone(false);// Need to check
		Resources.getResources().setCurrentContext(this);
		setContentView(R.layout.sc_ticketsview_screen);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ticketViewScreen);
		layout.setBackgroundColor((SCPreferences
				.getColor(SCTicketViewScreen.this)));

		listView = (ListView) findViewById(android.R.id.list);
		Resources.getResources().setAssetsTicketData(null);
		noMessageText = (TextView) findViewById(R.id.nonew_message);
		newMessageIcon = (ImageView) findViewById(R.id.push_icon);
		adapter = new SCTicketsAdapter(this, Resources.getResources()
				.getAssetsTicketData());
		setListAdapter(adapter);
		new AssetTicketTask().execute(CONSTANTS.BASE_URL);
		listView.setOnItemLongClickListener(this);

	}
	
	   @Override
	    protected void onStart() {
	        super.onStart();       
	     
	        	if ( SCPreferences.getPreferences().getUserFullName(this).length()>0) {
	        		if (Resources.getResources().isLaunchloginactivity()  && Resources.getResources().isFromBackground())  {
	        	//	fireAlarm();
	        			Log.i("Base Activity", "App in foreground after 10 mins ");
	        			 Resources.getResources().setLaunchloginactivity(false);
	        			 Resources.getResources().setFromBackground(false);
	        			Intent i = new Intent(this, SCLoginScreen.class);
	        			startActivity(i);
	        		   
	        		}
	        	    	
	        		}
	      
	    }

	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
 
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int position,
			long id) {

		return false;
	}

	public void onPhoneClick(View view) {
		int position = (Integer) view.getTag();
		AssetsTicketsInfo tInfo = (AssetsTicketsInfo) listView
				.getItemAtPosition(position);
		// Log.i("PHONE NUMBER", "<> "+tInfo.assetPhone);
		Resources.getResources().setAssetTicketInfo(tInfo);
		String phoneNumber = "tel:" + tInfo.assetPhone;
		Intent DialIntent = new Intent(Intent.ACTION_CALL,
				Uri.parse(phoneNumber));
		DialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(DialIntent);

	}

	public void onTicketMapClick(View view) {

		int position = (Integer) view.getTag();
		// Log.i("Position", "<<<<<<<<>>>>>>>>"+position);
		AssetsTicketsInfo tInfo = (AssetsTicketsInfo) listView
				.getItemAtPosition(position);
		Resources.getResources().setAssetTicketInfo(tInfo);
		Intent i = new Intent(this, SCViewMapDirectionsScreen.class);
		startActivity(i);
	}

	public void onTicketDetailsClick(View view) {

		Resources.getResources().setFirstScanDone(false);
		int position = (Integer) view.getTag();
		AssetsTicketsInfo tInfo = (AssetsTicketsInfo) listView
				.getItemAtPosition(position);
		ArrayList<ScCheckPoints> checkPointArray = new ArrayList<ScCheckPoints>();
		try {
			checkPointArray.addAll(tInfo.checkPoints);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// (checkPoint);
		Resources.getResources().setAssetTicketInfo(tInfo);
		Resources.getResources().setCheckPointModelArray(null);
		Resources.getResources().setCheckPointModelArray(checkPointArray);
		Resources.getResources().setTicketExtraData(null);
		Resources.getResources().setDocumentsData(null);
		Resources.getResources().setHistoryData(null);

		Intent details = new Intent(SCTicketViewScreen.this,
				SCDetailsFragmentScreen.class);
		startActivity(details);
		// Log.i("TICKET SCAN", "<><> "+tInfo.ticketNumberOfScans);

	}

	public void onNewMessageClick(View v) {

		newMessageIcon.setVisibility(View.GONE);
		noMessageText.setVisibility(View.VISIBLE);
		Intent ticketView = new Intent(this, SCMessageViewScreen.class);
		startActivity(ticketView);
	}

	public void onNoNewMessage(View v) {
		Intent ticketView = new Intent(this, SCMessageViewScreen.class);
		startActivity(ticketView);
	}

	public void showPushNotificationAlert(String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				newMessageIcon.setVisibility(View.VISIBLE);
				noMessageText.setVisibility(View.GONE);
			}
		});
	}

	private class AssetTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		private Vector<AssetsTicketsInfo> vector;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				// Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams
						.add(new BasicNameValuePair("username", SCPreferences
								.getPreferences().getUserName(
										SCTicketViewScreen.this)));
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCTicketViewScreen.this)));
				listParams
						.add(new BasicNameValuePair("action", "show_tickets"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				// Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject jObject = new JSONObject(response);

				Iterator<?> keys = jObject.keys();
				vector = new Vector<AssetsTicketsInfo>();
				while (keys.hasNext()) {
					String key = (String) keys.next();

					if (jObject.get(key) instanceof JSONObject) {
						if (!key.equalsIgnoreCase("route_order")
								&& !key.equalsIgnoreCase("default_address")) {
							// Log.i("Key", "<> "+key);
							JSONObject mainJObj = jObject.getJSONObject(key);

							String assetId = mainJObj.getString("asset_id");
							String assetAddressTwo = mainJObj
									.getString("address2");
							String assetlatitude = mainJObj
									.getString("latitude");
							String assetLongitude = mainJObj
									.getString("longitude");
							String assetTechnician = mainJObj
									.getString("technician");
							String tolerance = mainJObj
									.getString("tolerance");
							String assetType = mainJObj.getString("asset_type");
							String assetPhotoUrl = mainJObj
									.getString("asset_photo");
							String assetDescription = mainJObj
									.getString("description");
							String assetCode = mainJObj.getString("asset_code");
							String assetUNAssetId = mainJObj
									.getString("un_asset_id");
						
							String assetClientName = mainJObj
									.getString("client_name");
							String assetContact = mainJObj.getString("contact");
							String assetPosition = mainJObj
									.getString("position");
							String assetPhone = mainJObj.getString("phone");

							// ********************Address***************
							JSONObject addressObject = mainJObj
									.getJSONObject("address1");
							String addressStreet = addressObject
									.getString("street");
							String addressCity = addressObject
									.getString("city");
							String addressState = addressObject
									.getString("state");
							String addressPostalCode = addressObject
									.getString("postal_code");
							String addressCountry = addressObject
									.getString("country");

							// ********************Tickets***************
							JSONArray ticketArray = mainJObj
									.getJSONArray("ticket_info");
							if (ticketArray != null && ticketArray.length() > 0) {
								for (int i = 0; i < ticketArray.length(); i++) {
									AssetsTicketsInfo assetTicketInfo = new AssetsTicketsInfo();
									assetTicketInfo.assetId = assetId;
									assetTicketInfo.assetAddressTwo = assetAddressTwo;
									assetTicketInfo.assetLongitude = assetLongitude;
									assetTicketInfo.assetlatitude = assetlatitude;
									assetTicketInfo.assetTechnician = assetTechnician;
									assetTicketInfo.assetType = assetType;
									assetTicketInfo.assetPhotoUrl = assetPhotoUrl;
									assetTicketInfo.assetDescription = assetDescription;
									assetTicketInfo.assetCode = assetCode;
									assetTicketInfo.assetUNAssetId = assetUNAssetId;
								 
									assetTicketInfo.assetClientName = assetClientName;
									assetTicketInfo.assetContact = assetContact;
									assetTicketInfo.assetPosition = assetPosition;
									assetTicketInfo.assetPhone = assetPhone;

									assetTicketInfo.addressStreet = addressStreet;
									assetTicketInfo.addressCity = addressCity;
									assetTicketInfo.addressState = addressState;
									assetTicketInfo.addressPostalCode = addressPostalCode;
									assetTicketInfo.addressCountry = addressCountry;

									// ********************CheckPoints***************

									JSONArray checkArray = mainJObj
											.getJSONArray("checkpoints");
									JSONObject checkObject = new JSONObject();

									ArrayList<ScCheckPoints> arrayCheck = new ArrayList<ScCheckPoints>();
									for (int check = 0; check < checkArray
											.length(); check++) {
										checkObject = checkArray
												.getJSONObject(check);
										ScCheckPoints checkModel = new ScCheckPoints();
										checkModel.checkpoint_id = checkObject
												.getString("checkpoint_id");
										checkModel.qr_code = checkObject
												.getString("qr_code");
										checkModel.description = checkObject
												.getString("description");
										checkModel.time = checkObject
												.getString("time");										
										arrayCheck.add(checkModel);
										
									}
									//Log.d("checkpoints", "checkpoints -- " + checkArray.toString());
//									Resources.getResources()
//									.setCheckPoints(arrayCheck);
									assetTicketInfo.checkPoints = arrayCheck;
									
									JSONObject ticketObj = ticketArray
											.getJSONObject(i);
									assetTicketInfo.ticketTableId = ticketObj
											.getString("tbl_ticket_id");
									String asset_tolerance= ticketObj.getString("ticket_tolerance");
									if(asset_tolerance.equalsIgnoreCase("")){
										assetTicketInfo.assetTolerance = tolerance;
									}else{
										assetTicketInfo.assetTolerance = asset_tolerance;
									}
									 
									
									assetTicketInfo.ticketId = ticketObj
											.getString("ticket_id");
									assetTicketInfo.ticketStartDate = ticketObj
											.getString("start_date");
									assetTicketInfo.ticketTimeStamp = ticketObj
											.getString("ticket_start_date");
									assetTicketInfo.ticketStartTime = ticketObj
											.getString("start_time");
									assetTicketInfo.ticketStatus = ticketObj
											.getString("ticket_status");
									assetTicketInfo.ticketOverDue = ticketObj
											.getString("over_due");
									assetTicketInfo.notes = ticketObj
											.getString("notes");
									assetTicketInfo.is_questions = ticketObj
											.getString("is_questions");
									assetTicketInfo.allowIdCardScan = ticketObj
											.getString("allow_id_card_scan");
									assetTicketInfo.thumbPhotoUrl = ticketObj
											.getString("photo");
									assetTicketInfo.ticket_start_time = ticketObj
											.getString("ticket_start_time");
									assetTicketInfo.ticket_end_time = ticketObj
											.getString("ticket_end_time");
									assetTicketInfo.ticket_total_time = ticketObj
											.getString("ticket_total_time");
									Log.e("PATH ", ""
											+ assetTicketInfo.thumbPhotoUrl);
									assetTicketInfo.ticketNumberOfScans = Integer
											.parseInt(ticketObj
													.getString("no_of_scans"));
									assetTicketInfo.ticketIsService = ticketObj
											.getString("is_service");
									vector.add(assetTicketInfo);
									Collections.sort(vector);
									Resources.getResources()
											.setAssetsTicketData(vector);
									adapter.setAssetTicetData(vector);
									// Log.i("TT Start Date> "+assetTicketInfo.ticketOverDue,
									// "Asset ID> "+assetTicketInfo.assetCode);
								}
							}
						}
					}
				}

				return true;
			} catch (Exception e) {
				Log.e("Exception", e.getMessage(), e);
			}
			return Boolean.FALSE;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pdialog.dismiss();
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCTicketViewScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Assets & Tickets");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}


}
