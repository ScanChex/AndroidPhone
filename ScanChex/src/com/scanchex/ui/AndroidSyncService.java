package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

//This service is used to register content observers which will running always
public class AndroidSyncService extends Service {

	private LocationManager locManager;
	private double longitude;
	private double latitude;

	private String strLongitude;
	private String strLatitude;
	private String deviceId;
	private String phoneModel;
	private String androidVersion;
	public static AndroidSyncService locationservice = null;

	private AssetsTicketsInfo tInfo;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		tInfo = Resources.getResources().getAssetTicketInfo();
		Log.i("", "--------LOCATION SERVICE Sart-----------");
		if(locationservice==null){
			locationservice = this;
		}
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Criteria criteria = new Criteria();
        String provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            strLatitude = ""+latitude;
            strLongitude = ""+longitude;
            Log.i("LOCATION LAT>>"+latitude,"Longitute" +longitude);
        }else{
        	strLatitude = "Not Found";
        	strLongitude = "Not Found";
        }
        
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();      
        // Device model
        phoneModel = android.os.Build.MODEL;
        // Android version
        androidVersion = android.os.Build.VERSION.RELEASE;
        
        Log.i("DEVICE ID", ""+deviceId);
        Log.i("DEVICE MODEL", ""+phoneModel);
        Log.i("DEVICE OS Version", ""+androidVersion);
        Log.i("ASSET ID", "<> "+tInfo.assetId);
        
        Thread thread = new Thread(new MyThread());
        thread.start();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		

	}

	class MyThread implements Runnable{

		String response;
		@Override
		public void run() {
			
			try{
				Log.i("LOCATION UPDATE URL", "<><>" + CONSTANTS.BASE_URL);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getApplicationContext())));
				listParams.add(new BasicNameValuePair("username",SCPreferences.getPreferences().getUserName(getApplicationContext())));
				listParams.add(new BasicNameValuePair("asset_id", tInfo.assetId));
				listParams.add(new BasicNameValuePair("latitude", strLatitude));
				listParams.add(new BasicNameValuePair("longitude", strLongitude));
				listParams.add(new BasicNameValuePair("device_id", deviceId));
				listParams.add(new BasicNameValuePair("device_make", "Android"));
				listParams.add(new BasicNameValuePair("device_model", phoneModel));
				listParams.add(new BasicNameValuePair("device_os", androidVersion));
				listParams.add(new BasicNameValuePair("action", "update_user_location"));
				response = new HttpWorker().getData(CONSTANTS.BASE_URL, listParams);
				response = response.substring(3);
				
				Log.i("RESPONSE", "LOCATION UPDATE Resp>> " + response);
				JSONObject obj = new JSONObject(response);
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
		
	}
}
