package com.scanchex.ui;

import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class TestShowPDF extends BaseActivity {

	String url = "";
	WebView webView;
	ProgressBar progressBar1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_webview_activity);
		webView = (WebView) findViewById(R.id.webViewScanChex);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		url = "http://docs.google.com/gview?embedded=true&url="
				+ getIntent().getExtras().getString("PATH");

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
		// set url for webview to load
		webView.loadUrl(url);
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

}
