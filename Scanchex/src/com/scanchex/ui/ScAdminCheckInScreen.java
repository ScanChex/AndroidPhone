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
import java.util.Vector;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.ui.ScAdminCheckOutScreen.CheckOutAsyncTask;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.CustomScrollView;
import com.scanchex.utils.GPSTracker;
import com.scanchex.utils.JSONParser;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class ScAdminCheckInScreen extends BaseActivity {

	ImageView imageView;
	EditText editTextRefrence, editTextNOTES;
	TextView des_id, asset_serial, asset_id, departmentName, add_id,
			signatureSave;

	TextView textviewEmployee, textViewDepartment, textViewDateAndTime,
			textViewDueIn, textViewTolerance, textViewClient, textViewAddress,
			textViewcheckoutreference, textViewcheckoutnotes;

	CheckBox checkBox1;
	Context mContext;
	private DrawingView drawView;
	private float smallBrush;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "ScanChex";
	String signaturePath = "";
	JSONObject jsonObject;
	String checkOutId = "", checkToOut = "", department = "", dateTime = "",
			dueIn = "", tolerance = "", forClient = "", address = "",
			refrence = "", notes = "", link = "", asset_id_string = "", employeeFullName="";
	GPSTracker gps;
	String latitude, longitude;
	CustomScrollView scrolViewLayout;
	private String contentType;
	String imageUrl;
	LinearLayout drawLayout, mainLayoutCheckOut;
	Button checkinButton;
	String assetID;
	Vector<AssetsTicketsInfo> vector;
	// int ticketPosition = getIntent().getExtras().getInt("position");
	int ticketPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkin_screen);
		mContext = this;
		imageView = (ImageView) findViewById(R.id.imageView1);
		drawView = (DrawingView) findViewById(R.id.drawing);
		int ticketPosition = getIntent().getIntExtra("position", 0);
		Log.v("tickect info", "position \t" + ticketPosition);
		smallBrush = 5;
		drawView.setBrushSize(smallBrush);
		des_id = (TextView) findViewById(R.id.des_id);
		asset_serial = (TextView) findViewById(R.id.asset_serial);
		asset_id = (TextView) findViewById(R.id.asset_id);
		departmentName = (TextView) findViewById(R.id.departmentName);
		add_id = (TextView) findViewById(R.id.add_id);
		signatureSave = (TextView) findViewById(R.id.signatureSave);
		textViewcheckoutnotes = (TextView) findViewById(R.id.textViewcheckoutnotes);
		textViewcheckoutreference = (TextView) findViewById(R.id.textViewcheckoutreference);
		scrolViewLayout = (CustomScrollView) findViewById(R.id.scrolViewLayout);
		drawLayout = (LinearLayout) findViewById(R.id.drawLayout);
		mainLayoutCheckOut = (LinearLayout) findViewById(R.id.mainLayoutCheckOut);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkinButton = (Button) findViewById(R.id.checkinButton);
		drawView.setVisibility(View.INVISIBLE);
		// EditText Fields
		textviewEmployee = (TextView) findViewById(R.id.textViewEmployee);
		textViewDepartment = (TextView) findViewById(R.id.textViewDepartment);
		textViewClient = (TextView) findViewById(R.id.textViewClient);
		textViewDateAndTime = (TextView) findViewById(R.id.textViewDateAndTime);
		textViewDueIn = (TextView) findViewById(R.id.textViewDueIn);
		textViewTolerance = (TextView) findViewById(R.id.editTextTolerance);
		textViewAddress = (TextView) findViewById(R.id.textViewAddress);
		editTextRefrence = (EditText) findViewById(R.id.editTextRefrence);
		editTextNOTES = (EditText) findViewById(R.id.editTextNOTES);

		vector = new Vector<AssetsTicketsInfo>();
		vector = Resources.getResources().getAssetsTicketData();
		AssetsTicketsInfo tInfo = (AssetsTicketsInfo) vector
				.get(ticketPosition);
		try {
			imageUrl = tInfo.thumbPhotoUrl;
			Picasso.with(mContext).load(tInfo.thumbPhotoUrl)
					.placeholder(R.drawable.photo_not_available)
					.error(R.drawable.photo_not_available).into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		drawView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrolViewLayout.setEnableScrolling(false);
				return false;
			}
		});
		
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked && isClick == false) {

					drawView.startNew();
					drawView.setVisibility(View.VISIBLE);
				    scrolViewLayout.setEnableScrolling(false);

				} else {
					drawView.setVisibility(View.INVISIBLE);
				}
			}
		});
		String timeStamp = new SimpleDateFormat("MM/dd/yy hh:mm aa",
				Locale.getDefault()).format(new Date());
		// textViewDateAndTime.setText(timeStamp);

		// Set Layout info
		checkinButton.setText("CHECK-IN TICKET " + tInfo.ticketId);
		des_id.setText(tInfo.assetDescription);
		asset_serial.setText(tInfo.assetSerialKey);
		asset_id.setText(tInfo.assetUNAssetId);
		add_id.setText(tInfo.addressStreet + " , " + tInfo.addressCity + "\n"
				+ tInfo.addressState + " , " + tInfo.addressCountry);
		// editTextNOTES.setText(tInfo.notes);
		departmentName.setText(tInfo.assetDepartment);
		tolerance = tInfo.assetTolerance;
		asset_id_string = tInfo.assetId;
		checkOutId = tInfo.ticketTableId;
		latitude = tInfo.assetlatitude;
		longitude = tInfo.assetLongitude;
		department = tInfo.assetDepartment;
		link = tInfo.assetPhotoUrl;
		forClient = tInfo.assetClientName;
		notes = tInfo.notes;
		refrence = tInfo.reference;
		try {
			imageUrl = tInfo.thumbPhotoUrl;
			Picasso.with(mContext).load(tInfo.thumbPhotoUrl)
					.placeholder(R.drawable.photo_not_available)
					.error(R.drawable.photo_not_available).into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateTimeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a",
				Locale.getDefault()).format(new Date());
		// dueIn = tInfo.ticketOverDue;
		dueIn = dateTimeStamp;
		dateTime = tInfo.ticketStartDate;
		//address = tInfo.assetAddressTwo;
		address =tInfo.addressStreet + " , " + tInfo.addressCity + "\n"
		+ tInfo.addressState + " , " + tInfo.addressCountry;
		// Set Fields

		String name = tInfo.assetTechnician;
		String lastName;
		if (name.contains("-")) {
			int start = name.indexOf("-");
			employeeFullName = name.substring(start + 1);
			checkToOut = name.substring(0, (start));
			
		} else {
			employeeFullName = tInfo.assetTechnician;
			checkToOut = tInfo.assetTechnician;
		}
		textviewEmployee.setText(employeeFullName);
		textViewDepartment.setText(tInfo.assetDepartment);
	
		textViewDateAndTime.setText(tInfo.ticketTimeStamp);
		textViewDueIn.setText(dueIn);
		textViewTolerance.setText(tolerance);
		textViewClient.setText(tInfo.assetClientName);
		textViewAddress.setText(tInfo.addressStreet);
		textViewcheckoutnotes.setText(tInfo.notes);
		textViewcheckoutreference.setText(tInfo.reference);
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked && isClick == false) {

					drawView.startNew();
					drawView.setVisibility(View.VISIBLE);
					// scrolViewLayout.setEnableScrolling(false);

				} else {
					drawView.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	public void onClickCheckOut(View v) {

		if (checkBox1.isChecked()  && isClick == true ) {

			new CheckOutAsyncTask().execute();

		} else {
			showToast("Please sign & accept conditions first");
		}

	}

	public void onClickBack(View v) {
		finish();
	}

	ProgressDialog pdialog;

	public void showProgressDialog() {
		pdialog = new ProgressDialog(mContext);
		pdialog.setIcon(R.drawable.info_icon);
		pdialog.setTitle("Loading");
		pdialog.setMessage("Working...");
		pdialog.show();
	}

	public void hideProgressDialog() {
		pdialog.dismiss();
	}

	public void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	boolean isClick = false;

	// Drawing Buttons
	public void onClickAccept(View v) {
		scrolViewLayout.setEnableScrolling(true);
		if (isClick == false && checkBox1.isChecked()) {
			isClick = true;

			drawView.setDrawingCacheEnabled(true);
			// attempt to save
			String imgSaved = MediaStore.Images.Media.insertImage(
					getContentResolver(), drawView.getDrawingCache(), UUID
							.randomUUID().toString() + ".png", "scanchex");
			// feedback

			if (imgSaved != null) {
				signaturePath = getPath(Uri.parse(imgSaved));
				contentType = getContentType(Uri.parse(signaturePath));
				new UploadTask().execute();
			} else {
				Toast unsavedToast = Toast
						.makeText(
								getApplicationContext(),
								"Oops! Signature could not be uploaded please try again.",
								Toast.LENGTH_SHORT);
				unsavedToast.show();
			}
			drawView.destroyDrawingCache();
		} else {
			Toast.makeText(getApplicationContext(),
					"Check box needs to checked to accept signature",
					Toast.LENGTH_LONG).show();

		}
	}

	public void onClickClear(View v) {

		if (isClick == false) {
			drawView.destroyDrawingCache();
			drawView.startNew();
		}
	}

	public void onClickCancel(View v) {
		if (isClick == false) {
			drawView.destroyDrawingCache();
			drawView.startNew();
		}
		// scrolViewLayout.setEnableScrolling(false);
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);

	}

	private class UploadTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog pdialog;
		private String serverResp;

		private String status;

		@Override
		protected Boolean doInBackground(String... path) {

			String url = "http://scanchex.net/modules/cron/veriscanAPI.php";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			Log.i("URL", "<><><>" + url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs
					.add(new BasicNameValuePair("master_key", ""
							+ SCPreferences.getPreferences().getUserMasterKey(
									mContext)));
			nameValuePairs.add(new BasicNameValuePair("action", "signature"));
			nameValuePairs.add(new BasicNameValuePair("upload_array", ""));
			nameValuePairs.add(new BasicNameValuePair("file", signaturePath));
			try {
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				for (int index = 0; index < nameValuePairs.size(); index++) {
					if (nameValuePairs.get(index).getName()
							.equalsIgnoreCase("file")) {
						// If the key equals to "image", we use FileBody to
						// transfer the data

						entity.addPart(nameValuePairs.get(index).getName(),
								new FileBody(new File(nameValuePairs.get(index)
										.getValue()), "image/jpeg"));
					} else {
						// Normal string data
						entity.addPart(nameValuePairs.get(index).getName(),
								new StringBody(nameValuePairs.get(index)
										.getValue()));
					}
				}

				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				StringBuilder sb = null;
				String line = null;
				if (response != null) {
					InputStream in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				}
				serverResp = sb.toString();
				Log.i("SERVER RESP", "<><><>" + serverResp);
				JSONObject obj = new JSONObject(serverResp);
				link = obj.getString("path");
				if (serverResp.contains("error")) {
					status = "fail";
					return false;
				} else {
					status = "success";
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Boolean.FALSE;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			File file = new File(signaturePath);
			file.delete();
			if (result) {
				isClick = true;
				signatureSave.setVisibility(View.VISIBLE);
				drawView.setVisibility(View.GONE);
				Toast.makeText(mContext, "Saved Successfully",
						Toast.LENGTH_LONG).show();
			} else {
				isClick = false;
				Toast.makeText(getApplicationContext(),
						"Please give the signature", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

	}

	public class CheckOutAsyncTask extends
			AsyncTask<JSONObject, JSONObject, JSONObject> {

		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(JSONObject... strings) {
			jsonObject = new JSONObject();
			try {

				JSONParser jsonParser = new JSONParser();

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("master_key", ""
						+ SCPreferences.getPreferences().getUserMasterKey(
								mContext)));
				nameValuePairs.add(new BasicNameValuePair("ticket_id",
						checkOutId));
				nameValuePairs.add(new BasicNameValuePair("employee",
						checkToOut));
				nameValuePairs.add(new BasicNameValuePair("department",
						department));
				nameValuePairs.add(new BasicNameValuePair("date_time_out",
						dateTime));
				nameValuePairs.add(new BasicNameValuePair("date_time_due_in",
						dueIn));
				nameValuePairs.add(new BasicNameValuePair("tolerance", "0"));
				nameValuePairs.add(new BasicNameValuePair("client_id",
						forClient));
				nameValuePairs
						.add(new BasicNameValuePair("reference", refrence));
				nameValuePairs.add(new BasicNameValuePair("address", address));
				nameValuePairs.add(new BasicNameValuePair("notes", notes));
				nameValuePairs.add(new BasicNameValuePair("received_condition",
						"1"));
				nameValuePairs
						.add(new BasicNameValuePair("latitude", latitude));
				nameValuePairs.add(new BasicNameValuePair("longitude",
						longitude));
				nameValuePairs.add(new BasicNameValuePair("signature", link));
				nameValuePairs.add(new BasicNameValuePair("asset_id",
						asset_id_string));
				nameValuePairs.add(new BasicNameValuePair("user_id",
						SCPreferences.getPreferences().getUserName(mContext)));
				Log.v("ticket info", "ticket info1 \t " + "checkout \t"
						+ checkOutId + "checkToOut \t" + checkToOut + "\n"
						+ "department \t" + department + "\n"
						+ "date_time_out \t" + dateTime + "\n"
						+ "date_time_due_in \t" + dueIn + "\n" + "client_id\t"
						+ forClient + "address \t" + address + "signature \t"
						+ link + "notes \t " + notes);
				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/checkin", "POST",
						nameValuePairs);
				Log.v("ticket info", "ticket info2 \t " + jsonObject);
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			hideProgressDialog();
			JSONObject data = new JSONObject();
			Log.v("ticket info", "ticket info \t" + data);
			JSONArray checkOutObject = new JSONArray();
			Log.v("ticket info", "ticket info \t" + checkOutObject);
			try {
				data = json.getJSONObject("data");
				checkOutObject = data.getJSONArray("checkout");

				// data = checkOutObject.getJSONObject(0);
				String ticket_id = data.getString("ticket_id");
				String ticket_number = data.getString("ticket_number");

				data = checkOutObject.getJSONObject(0);
				if (!(ticket_id.equals(""))) {

					Intent checkoutView = new Intent(mContext,
							ScAdminCheckOutConfirmationScreen.class);
					checkoutView.putExtra("manualResponce", data.toString());
					checkoutView.putExtra("imageUrl", imageUrl);
					checkoutView.putExtra("employeename", employeeFullName);
					checkoutView.putExtra("duein", dueIn);
					checkoutView.putExtra("ticketId", ticket_id);
					checkoutView.putExtra("ticket_number", ticket_number);
					checkoutView.putExtra("title", "CHECK-IN CONFIRMATION");
					checkoutView.putExtra("department", department);
					checkoutView.putExtra("address", address);
			
					Log.v("to next activity", "to next activity2\t" + dueIn);
					startActivity(checkoutView);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();

			}

		}
	}

	private String getContentType(Uri uri) {
		ContentResolver cR = getContentResolver();
		return cR.getType(uri);
	}

}
