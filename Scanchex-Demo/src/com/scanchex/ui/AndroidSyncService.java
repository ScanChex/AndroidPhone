package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.SCPreferences;

//This service is used to register content observers which will running always
public class AndroidSyncService extends Service {

//	private LocationManager locManager;
//	private double longitude;
//	private double latitude;

	private String strLongitude;
	private String strLatitude;
	private String deviceId;
	private String phoneModel;
	private String androidVersion;
	public static AndroidSyncService locationservice = null;
	
	private String accurateLocation = "";
	private String speed = "";
	private String battery_status = "";
	private LocationManager locationManager;
	private Location gpsLocation = null;
	private Location networkLocation = null;	
	private Location betterLocation=null;
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Log.i("", "--------LOCATION SERVICE Sart-----------");
		if(locationservice==null){
			locationservice = this;
		}
//		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		captureUserLocation();
		/*Criteria criteria = new Criteria();
        String provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider); 
		if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            strLatitude = ""+latitude;
            strLongitude = ""+longitude;
            Log.i("LOCATION LAT>>"+latitude, "Longitute" +longitude);
        }else{
        	strLatitude = "Not Found";
        	strLongitude = "Not Found";
        }*/
        
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();      
        // Device model
        phoneModel = android.os.Build.MODEL;
        // Android version
        androidVersion = android.os.Build.VERSION.RELEASE;
        
        Log.i("DEVICE ID", ""+deviceId);
        Log.i("DEVICE MODEL", ""+phoneModel);
        Log.i("DEVICE OS Version", ""+androidVersion);
        
        Thread thread = new Thread(new MyThread());
        thread.start();
	}

	private void registerBatteryLevelReceiver() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

		registerReceiver(battery_receiver, filter);

	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			int rawlevel = intent.getIntExtra("level", -1);

		//	Log.i("BatteryLevel", "BatteryLevel" + rawlevel);
			battery_status = String.valueOf(rawlevel);

		}
	};

	class MyThread implements Runnable{

		String response;
		@Override
		public void run() {
			
			try{
				getAccurateLocation();
				registerBatteryLevelReceiver();
				Log.i("LATITUDE >> " + strLatitude, "LONGITUDE >> "
						+ strLongitude);
				Log.i("LOCATION UPDATE URL", "<><>" + CONSTANTS.BASE_URL);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getApplicationContext())));
				listParams.add(new BasicNameValuePair("username",SCPreferences.getPreferences().getUserName(getApplicationContext())));
				listParams.add(new BasicNameValuePair("asset_id", ""));
				listParams.add(new BasicNameValuePair("latitude", strLatitude));
				listParams.add(new BasicNameValuePair("longitude", strLongitude));
				listParams.add(new BasicNameValuePair("device_id", deviceId));
				Log.v("deviceId", "deviceId" + deviceId);
				listParams
						.add(new BasicNameValuePair("device_make", "Android"));

				listParams.add(new BasicNameValuePair("device_model",
						phoneModel));

				listParams.add(new BasicNameValuePair("device_os",
						androidVersion));
				Log.v("androidVersion", "androidVersion" + androidVersion);

				listParams.add(new BasicNameValuePair("action",
						"update_user_location1"));
				// Log.v("action", "action" + action);
				listParams.add(new BasicNameValuePair("speed", speed));
				Log.v("speed in run", "speed in run" + speed);
				listParams.add(new BasicNameValuePair("battery_status",
						battery_status));
				Log.v("battery in run", "battery in run" + battery_status);
				response = new HttpWorker().getData(CONSTANTS.BASE_URL,
						listParams);
				// response = response.substring(3);

			//	locationManager.removeUpdates(LocationListener);
				Log.i("RESPONSE", "LOCATION UPDATE Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
		
	}
	
	
	
	
	
	
	
	private void captureUserLocation(){
		
		//locationManager.removeUpdates(LocationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, LocationListener);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
           locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, LocationListener);
		}
	}

	private final LocationListener LocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			speed = String.valueOf(location.getSpeed());
			Log.v("speed in loc", "speed in loc " + speed);
			Log.v("location in loc","location  lat"+ location.getLatitude() + " location long "+ location.getLongitude() + "accuracy" + location.getAccuracy());
			locationManager.removeUpdates(LocationListener);
			
		 	
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	
	public String getAccurateLocation(){
		
		Log.i("GET ACCURATE LOCATION", "GET ACCURATE LOCATION");
		gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		
		if(networkLocation != null && gpsLocation != null){
			betterLocation= getBetterLocation(networkLocation, gpsLocation);
			strLatitude = ""+betterLocation.getLatitude();
			strLongitude = ""+betterLocation.getLongitude();
			return accurateLocation = betterLocation.getLatitude() + "," + betterLocation.getLongitude();
		}else if(gpsLocation != null){
			strLatitude = ""+gpsLocation.getLatitude();
			strLongitude = ""+gpsLocation.getLongitude();
			return accurateLocation = gpsLocation.getLatitude() + "," + gpsLocation.getLongitude();
		}else if(networkLocation != null){
			strLatitude = ""+networkLocation.getLatitude();
			strLongitude = ""+networkLocation.getLongitude();
			return accurateLocation = networkLocation.getLatitude() + "," + networkLocation.getLongitude();
		}else{
			strLatitude = "";
			strLongitude = "";
			return accurateLocation = "";
		}	
	}
	
	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > (1000 * 60 * 2);
		boolean isSignificantlyOlder = timeDelta < -(1000 * 60 * 2);
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}
	
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null){
			return provider2 == null;
		}
		
		return provider1.equals(provider2);
	}
	
}
