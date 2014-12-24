package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class ScPaynowScreen extends BaseActivity implements OnClickListener{
	private DrawingView drawView;
	private Button newBtn, saveBtn;
	private float smallBrush;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "ScanChex";
 	
	String comment = "";
	String additionalComment = "";
	String signaturePath = "";	
	EditText editTextComment;
	String ticketIdString = "";
	
	AssetsTicketsInfo tInfo;
	RelativeLayout layout;
	ImageView ticketStatusIcon;
	ImageView image;
	TextView clientName;
	TextView phoneNumber;
	TextView address1;
	TextView address2;

	TextView ticketId;
	TextView assetId;
	TextView assetName;
	TextView ticketStartDate;
	TextView ticketStartTime;
	ImageView mapIcon;
	ImageView detailIcon;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_paynow_first_screen);
		drawView = (DrawingView)findViewById(R.id.drawing);
		smallBrush =8;
		drawView.setBrushSize(smallBrush);
		newBtn = (Button)findViewById(R.id.new_btn);
		saveBtn = (Button)findViewById(R.id.save_btn);		
		editTextComment = (EditText)findViewById(R.id.editTextComment);
		ticketIdString = getIntent().getStringExtra("ticketId");
		newBtn.setOnClickListener(this);				
		saveBtn.setOnClickListener(this);
		
		layout = (RelativeLayout) findViewById(R.id.tickets_layout);
		image = (ImageView) findViewById(R.id.image_view);
		ticketStatusIcon = (ImageView) findViewById(R.id.ticket_status_icon);
		clientName = (TextView) findViewById(R.id.text1);
		phoneNumber = (TextView) findViewById(R.id.text2);
		address1 = (TextView) findViewById(R.id.text3);
		address2 = (TextView) findViewById(R.id.text4);
		ticketId = (TextView) findViewById(R.id.text5);
		assetId = (TextView) findViewById(R.id.text6);
		assetName = (TextView) findViewById(R.id.text7);
		ticketStartDate = (TextView) findViewById(R.id.text8);
		ticketStartTime = (TextView) findViewById(R.id.text9);

		tInfo = Resources.getResources().getAssetTicketInfo();

		
		if (tInfo.ticketOverDue.equals("1")) {

			layout.setBackgroundColor(this.getResources().getColor(R.color.red));
			ticketStatusIcon.setImageResource(R.drawable.excalamation_icon);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("Assigned")
				&& tInfo.ticketOverDue.equals("0")) {

			layout.setBackgroundColor(this.getResources().getColor(
					R.color.green));
			ticketStatusIcon.setVisibility(View.GONE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("complete")) {

			layout.setBackgroundColor(this.getResources()
					.getColor(R.color.grey));
			ticketStatusIcon.setImageResource(R.drawable.accept_ticket);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("pending")) {

			layout.setBackgroundColor(this.getResources()
					.getColor(R.color.blue));
			ticketStatusIcon.setVisibility(View.VISIBLE);
			ticketStatusIcon.setBackgroundResource(R.drawable.lightning_image);
		} else {
			layout.setBackgroundColor(this.getResources().getColor(R.color.red));
			ticketStatusIcon.setImageResource(R.drawable.excalamation_icon);
		}

		clientName.setText(tInfo.assetClientName);
		phoneNumber.setText(tInfo.assetPhone);
		address1.setText(tInfo.addressStreet);
		address2.setText(tInfo.addressCity + "," + tInfo.addressState);

		ticketId.setText(tInfo.ticketId);
		assetId.setText(tInfo.assetUNAssetId);
		assetName.setText(tInfo.assetDescription);
		ticketStartDate.setText(tInfo.ticketStartDate);
		ticketStartTime.setText(tInfo.ticketStartTime);
		
		try{
		Picasso.with(this) //
		.load(tInfo.thumbPhotoUrl) //
		.placeholder(R.drawable.photo_not_available) //
		.error(R.drawable.photo_not_available) //
		.into(image);
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	@Override
	public void onClick(View view){
	  if(view.getId()==R.id.new_btn){
			//new button
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("New Singnature");
			newDialog.setMessage("Are you want to write new Signature ?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					drawView.startNew();
					dialog.dismiss();
				}
			});
			
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.cancel();
				}
			});
			newDialog.show();
		}
	  
		else if(view.getId()==R.id.save_btn){
			//save drawing
		
					drawView.setDrawingCacheEnabled(true);
					//attempt to save
					String imgSaved = MediaStore.Images.Media.insertImage(
							getContentResolver(), drawView.getDrawingCache(),
							UUID.randomUUID().toString()+".png", "scanchex");
					//feedback
					 
					if(imgSaved!=null){
						signaturePath = getPath(Uri.parse(imgSaved));
					}
					else{
						Toast unsavedToast = Toast.makeText(getApplicationContext(), 
								"Oops! Signature could not be uploaded please try again.", Toast.LENGTH_SHORT);
						unsavedToast.show();
					}
					drawView.destroyDrawingCache();
			
		}
	}
	
	public String getPath(Uri uri) {

		  String result;
		    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		    if (cursor == null) { 
		        result = uri.getPath();
		    } else { 
		        cursor.moveToFirst(); 
		        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		        result = cursor.getString(idx);
		        cursor.close();
		    }
		    return result;
	}
	
	String contentType = "";
	public void onClickPayNow(View view){
		comment = editTextComment.getText().toString();
		if(signaturePath.equals("") || comment.equals("")){
			if(comment.equals("")){
				Toast.makeText(getApplicationContext(), "Please write your Comment", Toast.LENGTH_LONG).show();
			}
			Toast.makeText(getApplicationContext(), "Please write your signature", Toast.LENGTH_LONG).show();
		}else{
			contentType = getContentType(Uri.parse(signaturePath));
			
			 Intent payment= new Intent(ScPaynowScreen.this,ScPaymentScreen.class);
			 payment.putExtra("ticketId", ticketIdString);
			 payment.putExtra("imageFIle", signaturePath);
			 payment.putExtra("comment", comment);
			 payment.putExtra("AdditionalComment", additionalComment);
			 payment.putExtra("contentType", contentType);
			 startActivity(payment);
		}
	}
	
	public void onClickReturn(View v) {
		finish();
	}
	
	private String getContentType(Uri uri){
		ContentResolver cR = getContentResolver();
		return cR.getType(uri);
	}
	
	public void onCLickAdditionalComment(View v) {
		showMessgaeDialog("Add Additional Comment");
	}
	
	
	private void showMessgaeDialog(String title) {
		final Dialog dialog = new Dialog(ScPaynowScreen.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.sc_popup_sendmessage_dialog);
		TextView textViewPopUp = (TextView)dialog.findViewById(R.id.textViewPopUp);
		textViewPopUp.setText(title);
		final EditText messageText = (EditText)dialog.findViewById(R.id.editTextMessage);
		Button cancel = (Button)dialog.findViewById(R.id.buttonCancel);
		Button send = (Button)dialog.findViewById(R.id.buttonSend);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		send.setText("Submit");
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				additionalComment = messageText.getText().toString();
				dialog.cancel();
			}
		});
		
		dialog.show();
	}

		
	 
}
