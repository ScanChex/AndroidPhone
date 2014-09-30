package com.scanchex.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**

  @author Salman Ashraf
  
 */
public class Network {
	private static Network instance;

	List<NameValuePair> params = new ArrayList<NameValuePair>();

	private Network() {
	}

	public static Network getInstance() {
		if (instance == null) {
			instance = new Network();
		}
		return instance;
	}
 
	public static boolean isNetworkAvailable(Context context) {
		boolean value = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			value = true;
		}
		return value;
	}
 
 
	public static boolean isWiFiAvaiable(Context context) {
		boolean isWifiConnected = false;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			isWifiConnected = true;
		}
		return isWifiConnected;
	}

	 
	public static boolean is3GAvaiable(Context context) {
		boolean is3GConnected = false;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo Mobile3G = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (Mobile3G.isConnected())
			is3GConnected = true;
		return is3GConnected;
	}

	 
	public static boolean isGPSEnabled(Context context) {
		boolean GPSenabled = false;
		LocationManager GPSLocation = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (GPSLocation.isProviderEnabled(LocationManager.GPS_PROVIDER))
			GPSenabled = true;
		return GPSenabled;
	}

	public static boolean haveNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			Log.i("Value", "Yes");
			return true;
		}
		Log.i("Value", "No");
		return false;
	}
}
