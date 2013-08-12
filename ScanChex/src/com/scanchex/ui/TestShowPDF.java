package com.scanchex.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class TestShowPDF extends Activity {
	
	WebView webview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.test_pdf_screen);
		/*webview = (WebView)findViewById(R.id.pdf);
        webview.getSettings().setJavaScriptEnabled(true); 
        Log.v("....hello....","");
        webview.loadUrl(getIntent().getExtras().getString("PATH"));*/
        
        
        WebView mWebView=new WebView(TestShowPDF.this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+getIntent().getExtras().getString("PATH"));
        setContentView(mWebView);
	}

}
