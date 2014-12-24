package com.scanchex.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scanchex.adapters.SCTicketsAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCMapFragment extends ListFragment{

	private SCTicketsAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SCTicketsAdapter(getActivity(), Resources.getResources().getAssetsTicketData());
		setListAdapter(adapter);
		if(Resources.getResources().getAssetsTicketData()==null){
			new AssetTicketTask().execute(CONSTANTS.BASE_URL);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.sc_mapfragment_screen, container, false);	
		return view;
	}
	
	
	private class AssetTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		private Vector<AssetsTicketsInfo> vector;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("username", SCPreferences.getPreferences().getUserName(getActivity())));
				listParams.add(new BasicNameValuePair("master_key",SCPreferences.getPreferences().getUserMasterKey(getActivity())));
				listParams.add(new BasicNameValuePair("action", "show_tickets"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject jObject = new JSONObject(response);
				Iterator<?> keys = jObject.keys();
				vector = new Vector<AssetsTicketsInfo>();
				while(keys.hasNext()) {
					String key = (String)keys.next();
					
					 if( jObject.get(key) instanceof JSONObject ){
						 if(!key.equalsIgnoreCase("route_order") && !key.equalsIgnoreCase("default_address")){
							 Log.i("Key", "<> "+key);
							 JSONObject mainJObj = jObject.getJSONObject(key);
							  	 
							 String assetId = mainJObj.getString("asset_id");
							 String assetAddressTwo = mainJObj.getString("address2");
							 String assetLongitude = mainJObj.getString("latitude");
							 String assetlatitude = mainJObj.getString("longitude");
							 String assetTechnician = mainJObj.getString("technician");
							 String assetType = mainJObj.getString("asset_type");
							 String assetPhotoUrl = mainJObj.getString("asset_photo");
							 String assetDescription = mainJObj.getString("description");
							 String assetCode = mainJObj.getString("asset_code");
							 String assetUNAssetId = mainJObj.getString("un_asset_id");
							 String assetClientName = mainJObj.getString("client_name");
							 String assetContact = mainJObj.getString("contact");
							 String assetPosition = mainJObj.getString("position");
							 String assetPhone = mainJObj.getString("phone");
							 
							 //********************Address***************
							 JSONObject addressObject = mainJObj.getJSONObject("address1");
							 String addressStreet = addressObject.getString("street");
							 String addressCity = addressObject.getString("city");
							 String addressState = addressObject.getString("state");
							 String addressPostalCode = addressObject.getString("postal_code");
							 String addressCountry = addressObject.getString("country");
						
							 //********************Tickets***************
							 JSONArray ticketArray = mainJObj.getJSONArray("ticket_info");					 
							 if(ticketArray!=null && ticketArray.length()>0){
								 for(int i=0; i <ticketArray.length(); i++){
									 AssetsTicketsInfo assetTicketInfo = new AssetsTicketsInfo();
									 assetTicketInfo.assetId = assetId;
									 assetTicketInfo.assetAddressTwo = assetAddressTwo;
									 assetTicketInfo.assetLongitude = assetLongitude;
									 assetTicketInfo.assetlatitude  = assetlatitude;
									 assetTicketInfo.assetTechnician = assetTechnician;
									 assetTicketInfo.assetType = assetType;
									 assetTicketInfo.assetPhotoUrl = assetPhotoUrl;
									 assetTicketInfo.assetDescription =	assetDescription;
									 assetTicketInfo.assetCode = assetCode;
									 assetTicketInfo.assetUNAssetId = assetUNAssetId;
									 assetTicketInfo.assetClientName = assetClientName;
									 assetTicketInfo.assetContact =	assetContact;		 
									 assetTicketInfo.assetPosition = assetPosition;	
									 assetTicketInfo.assetPhone = assetPhone;
									 
									 assetTicketInfo.addressStreet = addressStreet;
									 assetTicketInfo.addressCity = addressCity;
									 assetTicketInfo.addressState = addressState;
									 assetTicketInfo.addressPostalCode = addressPostalCode;
									 assetTicketInfo.addressCountry = addressCountry;
											 
									 JSONObject ticketObj = ticketArray.getJSONObject(i);
									 assetTicketInfo.ticketTableId = ticketObj.getString("tbl_ticket_id");
									 assetTicketInfo.ticketId = ticketObj.getString("ticket_id");
									 assetTicketInfo.ticketStartDate = ticketObj.getString("start_date");
									 assetTicketInfo.ticketStartTime = ticketObj.getString("start_time");
									 assetTicketInfo.ticketStatus = ticketObj.getString("ticket_status");
									 assetTicketInfo.ticketOverDue = ticketObj.getString("over_due");
									 vector.add(assetTicketInfo);
									 adapter.setAssetTicetData(vector);
									 Log.i("TT Start Date> "+assetTicketInfo.ticketOverDue, "TT Start Time> "+assetTicketInfo.ticketStatus);
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
			pdialog = new ProgressDialog(getActivity());
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Login");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	
}
