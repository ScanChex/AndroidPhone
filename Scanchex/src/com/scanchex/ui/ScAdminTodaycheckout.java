package com.scanchex.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.scanchex.adapters.SCAdminCheckinTicketsAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.network.HttpWorker;

import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ScAdminTodaycheckout extends ListActivity {
	private SCAdminCheckinTicketsAdapter adapter;
	ListView listView;
	Vector<AssetsTicketsInfo> vector;
	Context mContext;
	String asset_id;
	String title_string;
	TextView title_view;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sc_admin_checkin_ticketsview_screen);

		listView = (ListView) findViewById(android.R.id.list);
		title_view = (TextView) findViewById(R.id.title_textview);
		mContext = this;
		vector = new Vector<AssetsTicketsInfo>();
		adapter = new SCAdminCheckinTicketsAdapter(this, vector);
		title_string = getIntent().getExtras().getString("title");
		title_view.setText(title_string);
		setListAdapter(adapter);
		new AssetTicketTask().execute(CONSTANTS.BASE_URL);

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

	private class AssetTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				// Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("username", SCPreferences
						.getPreferences()
						.getUserName(ScAdminTodaycheckout.this)));
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								ScAdminTodaycheckout.this)));
				listParams.add(new BasicNameValuePair("action",
						"show_checkin_out_tickets"));
				listParams.add(new BasicNameValuePair("today", "YES"));
				response = new HttpWorker().getData(params[0], listParams);

				JSONObject jObject = new JSONObject(response);
				Log.v("show_checkin_out_tickets", "show_checkin_out_tickets"
						+ response);
				Iterator<?> keys = jObject.keys();
				vector.clear();
				while (keys.hasNext()) {
					String key = (String) keys.next();

					if (jObject.get(key) instanceof JSONObject) {
						if (!key.equalsIgnoreCase("route_order")
								&& !key.equalsIgnoreCase("default_address")) {
							// Log.i("Key", "<> "+key);
							JSONObject mainJObj = jObject.getJSONObject(key);
							Log.v(" mainJObj", " mainJObj \t" + mainJObj);
							asset_id = mainJObj.getString("asset_id");
							String assetId = mainJObj.getString("asset_id");
							String assetAddressTwo = mainJObj
									.getString("address2");
							String assetlatitude = mainJObj
									.getString("latitude");
							String assetLongitude = mainJObj
									.getString("longitude");
							String assetTechnician = mainJObj
									.getString("technician");
							String assetType = mainJObj.getString("asset_type");
							String assetPhotoUrl = mainJObj
									.getString("asset_photo");
							String assetDescription = mainJObj
									.getString("description");
							String assetCode = mainJObj.getString("asset_code");
							String assetUNAssetId = mainJObj
									.getString("un_asset_id");
							String assetTolerance = mainJObj
									.getString("tolerance");
							String assetClientName = mainJObj
									.getString("client_name");
							String assetContact = mainJObj.getString("contact");
							String assetPosition = mainJObj
									.getString("position");
							String assetPhone = mainJObj.getString("phone");
							String assetDepartment = mainJObj
									.getString("department");
							String assetSerialKey = mainJObj
									.getString("serial_number");

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
							Log.v("ticketArray", "ticketArray \t" + ticketArray);
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
									assetTicketInfo.assetDepartment = assetDepartment;
									assetTicketInfo.assetSerialKey = assetSerialKey;

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

									assetTicketInfo.checkPoints = arrayCheck;

									JSONObject ticketObj = ticketArray
											.getJSONObject(i);
									assetTicketInfo.ticketTableId = ticketObj
											.getString("tbl_ticket_id");
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
									assetTicketInfo.employee = ticketObj
											.getString("employee");
									assetTicketInfo.notes = ticketObj
											.getString("notes");
									assetTicketInfo.reference = ticketObj
											.getString("reference");
									assetTicketInfo.ticket_type = ticketObj
											.getString("ticket_type");
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
									Resources.getResources()
											.setAssetsTicketData(vector);
									adapter.setAssetTicetData(vector);

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
			pdialog = new ProgressDialog(ScAdminTodaycheckout.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Assets & Tickets");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	public void onClickBack(View v) {
		finish();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new AssetTicketTask().execute(CONSTANTS.BASE_URL);
	}
}
