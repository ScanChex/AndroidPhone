package com.scanchex.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scanchex.adapters.SCTicketsAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCViewMapScreen extends FragmentActivity{
	
	private ListView listView;
	private SCTicketsAdapter adapter;
	private GoogleMap map;
	
	 private LocationManager locManager;
	 private double longitude;
	 private double latitude;
	  
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.sc_viewmap_screen);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		listView = (ListView)findViewById(android.R.id.list);
		Resources.getResources().setAssetsTicketData(null);
		adapter = new SCTicketsAdapter(this, Resources.getResources().getAssetsTicketData());
		listView.setAdapter(adapter);
		new AssetTicketTask().execute(CONSTANTS.BASE_URL);
		
		
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Criteria criteria = new Criteria();
        String provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("LOCATION LAT>>"+latitude,"Longitute" +longitude);
        }
        map.setMyLocationEnabled(true);
		
	}
	
	
	public void onMinusClick(View view){
//		setupMaps();
	}
	
	public void onPlusClick(View view){
		
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
				listParams.add(new BasicNameValuePair("username", SCPreferences.getPreferences().getUserName(SCViewMapScreen.this)));
				listParams.add(new BasicNameValuePair("master_key",SCPreferences.getPreferences().getUserMasterKey(SCViewMapScreen.this)));
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
							 String assetlatitude = mainJObj.getString("latitude");
							 String assetlongitude = mainJObj.getString("longitude");
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
									 assetTicketInfo.assetLongitude = assetlongitude;
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
									 Resources.getResources().setAssetsTicketData(vector);
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
			setupMaps();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCViewMapScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Login");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
	
	private void setupMaps(){
		
		// Hide the zoom controls as the button panel will cover it.
		map.getUiSettings().setZoomControlsEnabled(false);

	    // Add lots of markers to the map.
	    addMarkersToMap();

	    // Setting an info window adapter allows us to change the both the contents and look of the info window.
//	    map.setInfoWindowAdapter(new InfoWindowAdapter());//gul

	    // Set listeners for marker events. See the bottom of this class for their behavior.
//	    map.setOnMarkerClickListener(this);//gul
//	    map.setOnInfoWindowClickListener(this);//gul
//	    map.setOnMarkerDragListener(this);//gul

	    // Pan to see all markers in view.
	    // Cannot zoom to bounds until the map has a size.
	    final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
//	    if (mapView.getViewTreeObserver().isAlive()) {
	        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @SuppressLint("NewApi")
	            // We check which build version we are using.
	            @Override
	            public void onGlobalLayout() { 
	            	LatLngBounds.Builder bld = new LatLngBounds.Builder();
	            	Vector<AssetsTicketsInfo> v = Resources.getResources().getAssetsTicketData();
	            	for (int i = 0; i < v.size(); i++) {           
	            		AssetsTicketsInfo atInfo = v.get(i);
	        			Log.i("Inner Latitude: "+atInfo.assetlatitude, "Inner Longitude: "+atInfo.assetLongitude);
	        			LatLng latLng = new LatLng(Double.parseDouble(atInfo.assetlatitude), Double.parseDouble(atInfo.assetLongitude));
	            		bld.include(latLng);            
	            	}
	            	LatLngBounds bounds = bld.build();          
	            	map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
	                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	            }
	        });
//	    }
	}
	
	
	private void addMarkersToMap() {
		
		map.clear();
		
		Vector<AssetsTicketsInfo> v = Resources.getResources().getAssetsTicketData();
    	for (int i = 0; i < v.size(); i++) {      
    		AssetsTicketsInfo atInfo = v.get(i);
			Log.i("Latitude: "+atInfo.assetlatitude, "Longitude: "+atInfo.assetLongitude);			
			LatLng latLng = new LatLng(Double.parseDouble(atInfo.assetlatitude), Double.parseDouble(atInfo.assetLongitude));
//			BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED); 
			map.addMarker(new MarkerOptions().position(latLng).title(atInfo.assetUNAssetId).icon(BitmapDescriptorFactory.fromResource(R.drawable.other_locations)));
    	}
	}

}
