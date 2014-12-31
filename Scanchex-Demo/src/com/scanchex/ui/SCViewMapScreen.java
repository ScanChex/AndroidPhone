package com.scanchex.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scanchex.adapters.SCTicketsAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCViewMapScreen extends FragmentActivity implements LocationListener{
	
	private ListView listView;
	private SCTicketsAdapter adapter;
	private GoogleMap map;
	private String provider;
	private Marker userLoc;

	private LocationManager locManager;
	private double longitude;
	private double latitude;
	String dated = "";
	Date pdates,pdate;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.sc_viewmap_screen);
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		LinearLayout layout = (LinearLayout)findViewById(R.id.viewMapScreen);
		layout.setBackgroundColor((SCPreferences.getColor(SCViewMapScreen.this)));
		
		listView = (ListView)findViewById(android.R.id.list);
		Resources.getResources().setAssetsTicketData(null);
		adapter = new SCTicketsAdapter(this, Resources.getResources().getAssetsTicketData());
		listView.setAdapter(adapter);
		new AssetTicketTask().execute(CONSTANTS.BASE_URL);
		
		
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Criteria criteria = new Criteria();
         provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("LOCATION LAT>>"+latitude,"Longitute" +longitude);
            onLocationChanged(location);
        }
        map.setMyLocationEnabled(true);
		
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
	  
	  /* Request updates at startup */
	    @Override
	    protected void onResume() {
	        super.onResume();
	        locManager.requestLocationUpdates(provider, 10000, 1, this);
	    	new AssetTicketTask().execute(CONSTANTS.BASE_URL);
	    	
	    }

	    /* Remove the locationlistener updates when Activity is paused */
	    @Override
	    protected void onPause() {
	        super.onPause();
	        locManager.removeUpdates(this);
	    }
	    
	    @Override
	    public void onLocationChanged(Location location) {
	        double lat =  location.getLatitude();
	        double lng = location.getLongitude();
	        LatLng coordinate = new LatLng(lat, lng);
	        if(userLoc!=null) userLoc.remove();
	         userLoc =  map.addMarker(new MarkerOptions()
	        .position(coordinate)
	        .title("You are here")
	        .snippet("")
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fblue_flag_32)));
	    }
	
	// Pass the desired latitude and longitude to this method
	  public void showMarker(Double lat, Double lon) {
	      map.clear();
	      // Create a LatLng object with the given Latitude and Longitude
	      LatLng markerLoc = new LatLng(lat, lon);

	      //Add marker to map
	      map.addMarker(new MarkerOptions()
	              .position(markerLoc)                                                                        // at the location you needed
	              .title("User Location")                                                                     // with a title you needed
	              .snippet("")                                                           // and also give some summary of available
	              .icon(BitmapDescriptorFactory.fromResource(R.drawable.fblue_flag_32))); // and give your animation drawable as icon
	  }
	  
	  @Override
	    public void onProviderDisabled(String provider) {
	        Toast.makeText(this, "Enabled new provider " + provider,
	                Toast.LENGTH_SHORT).show();

	    }


	    @Override
	    public void onProviderEnabled(String provider) {
	        Toast.makeText(this, "Disabled provider " + provider,
	                Toast.LENGTH_SHORT).show();

	    }


	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	        // TODO Auto-generated method stub

	    }
	
	public void onTicketMapClick(View view){
		
		int position = (Integer)view.getTag();
		Log.i("Position", "<<<<<<<<>>>>>>>>"+position);
		AssetsTicketsInfo tInfo = (AssetsTicketsInfo)listView.getItemAtPosition(position);
		Resources.getResources().setAssetTicketInfo(tInfo);
		Intent i = new Intent(this, SCViewMapDirectionsScreen.class);
		startActivity(i);
	}
	
	public void onTicketDetailsClick(View view){
		
//		int position = (Integer)view.getTag();
//		
//		AssetsTicketsInfo tInfo = (AssetsTicketsInfo)listView.getItemAtPosition(position);
//		Resources.getResources().setAssetTicketInfo(tInfo);
//		Resources.getResources().setTicketExtraData(null);
//		Resources.getResources().setDocumentsData(null);
//		Resources.getResources().setHistoryData(null);
//		Intent details = new Intent(this, SCDetailsFragmentScreen.class);
//		startActivity(details);
//		Log.i("TICKET SCAN", "<><> "+tInfo.ticketNumberOfScans);

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

		Intent details = new Intent(SCViewMapScreen.this,
				SCDetailsFragmentScreen.class);
		startActivity(details);
		
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

				// Log.i("LOGIN URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams
						.add(new BasicNameValuePair("username", SCPreferences
								.getPreferences().getUserName(
										SCViewMapScreen.this)));
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCViewMapScreen.this)));
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
							String assetType = mainJObj.getString("asset_type");
							String assetPhotoUrl = mainJObj
									.getString("asset_photo");
							String assetDescription = mainJObj
									.getString("description");
							String assetCode = mainJObj.getString("asset_code");
							String assetUNAssetId = mainJObj
									.getString("un_asset_id");
							 String assetTolerance = mainJObj.getString("tolerance");
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
									assetTicketInfo.assetTolerance =assetTolerance;
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
			setupMaps();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCViewMapScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Map");
			pdialog.setMessage("Loading...");
			pdialog.show();
		}
	}
	
	private void setupMaps(){
		map.getUiSettings().setZoomControlsEnabled(true); 
	    addMarkersToMap();
	    final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
	        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            @SuppressLint("NewApi")
	            // We check which build version we are using.
	            LatLng latLng = new LatLng(latitude, longitude);
	            @Override
	            public void onGlobalLayout() { 
	            	LatLngBounds.Builder bld = new LatLngBounds.Builder();
	            	Vector<AssetsTicketsInfo> v = Resources.getResources().getAssetsTicketData();
	            	if ( !isEmpty(v)) {
	            	for (int i = 0; i < v.size(); i++) {           
	            		AssetsTicketsInfo atInfo = v.get(i);
	        			Log.i("Inner Latitude: "+atInfo.assetlatitude, "Inner Longitude: "+atInfo.assetLongitude);
	        			latLng = new LatLng(Double.parseDouble(atInfo.assetlatitude), Double.parseDouble(atInfo.assetLongitude));
	            		bld.include(latLng);            
	            	}
	            	
	 //           	CameraPosition cameraPosition = new CameraPosition.Builder()
	//				.target(latLng).zoom(10).build();
	            	  LatLngBounds bounds = bld.build();
	                  int padding = 3; // offset from edges of the map in pixels
	                  CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
	                  map.moveCamera(cu);
	                  map.animateCamera(cu);
	            	}
//			map.animateCamera(CameraUpdateFactory
//					.newCameraPosition(cameraPosition));
//	                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	            }
	        });
//	    }
	}
	
	
	public boolean isEmpty(Vector v){  
		if (v==null)  
		   return true;  
		return v.isEmpty();  
		}  
	
	private void addMarkersToMap() {
		
		map.clear();

		Vector<AssetsTicketsInfo> v = Resources.getResources()
				.getAssetsTicketData();
		for (int i = 0; i < v.size(); i++) {
			AssetsTicketsInfo atInfo = v.get(i);
			String status = v.get(i).getTicketStatus();
			dated = v.get(i).getTicketTimeStamp();
			String overdue = v.get(i).getTicketOverDue();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd-MM-yyyy hh:mm:ss");
			Date date = new Date();
			System.out.println("Datestamp" + dateFormat.format(date));
			try {

				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd-MM-yyyy hh:mm:ss");
				pdate = sdf.parse(dated);
				Log.v("date from api", "date from api" + pdate);
				SimpleDateFormat sdfs = new SimpleDateFormat(
						"dd-MM-yyyy hh:mm:ss");
				 pdates = sdfs.parse(dateFormat.format(date));
				Log.v("date from system", "date from system" + pdate);

				if (pdate.after(pdates)) {

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Latitude: " + atInfo.assetlatitude, "Longitude: "
					+ atInfo.assetLongitude);
			LatLng latLng = new LatLng(
					Double.parseDouble(atInfo.assetlatitude),
					Double.parseDouble(atInfo.assetLongitude));

			if (status.equals("Completed")) {

				map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(atInfo.ticketId+"\n"+atInfo.addressCity+", "+atInfo.addressState+", \n"+atInfo.assetDescription)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.checkered32)));
			} else if (overdue.equals("1")) {

				map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(atInfo.ticketId+"\n"+atInfo.addressCity+", "+atInfo.addressState+",\n"+atInfo.assetDescription)
			
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.fred_flag_32)));
			} else if (status.equals("assigned")) {

				map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(atInfo.ticketId+"\n"+atInfo.addressCity+", "+atInfo.addressState+",\n"+atInfo.assetDescription)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.fblue_flag_32)));
			} else if (status.equals("pending")) {

				map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(atInfo.ticketId+"\n"+atInfo.addressCity+", "+atInfo.addressState+",\n"+atInfo.assetDescription)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.fyellow_flag32)));
			} else if (status.equals("Current") && (pdates.after(pdate))  ) {

				map.addMarker(new MarkerOptions()
						.position(latLng)
						.title(atInfo.ticketId+"\n"+atInfo.addressCity+", "+atInfo.addressState+",\n"+atInfo.assetDescription)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.fgreen_flag_32)));
			}

		}

	}
}
