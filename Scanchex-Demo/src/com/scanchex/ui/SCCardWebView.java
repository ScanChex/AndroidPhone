package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.scanchex.bo.SCMessageInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class SCCardWebView extends BaseActivity {

	WebView webView;
	ProgressBar progressBar1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_webview_activity);
		webView = (WebView) findViewById(R.id.webViewScanChex);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		new CardTask().execute(CONSTANTS.BASE_URL);
		WebSettings s = webView.getSettings();
		s.setBuiltInZoomControls(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		s.setGeolocationEnabled(true);
		s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
		s.setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			// load url
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// when finish loading page
			public void onPageFinished(WebView view, String url) {
				if (progressBar1.getVisibility() == view.VISIBLE) {
					progressBar1.setVisibility(View.GONE);
				}
			}
		});

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
	

	private class CardTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String message;

		private Vector<SCMessageInfo> vector;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("MESSAGE URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("username", SCPreferences
						.getPreferences().getUserName(SCCardWebView.this)));
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCCardWebView.this)));

				listParams.add(new BasicNameValuePair("action", "card"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				
				message = obj.getString("msg");

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
			if (result) {
				webView.loadData(message, "text/html", null);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCCardWebView.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Card");
			pdialog.setMessage("Loading...");
			pdialog.show();
		}
	}

}
