package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SpinnerCheckOutClientAdapter;
import com.scanchex.adapters.SpinnerClientAddressAdapter;
import com.scanchex.adapters.SpinnerToleranceAdapter;
import com.scanchex.bo.ScAdminManualLookModel;
import com.scanchex.bo.ScToleranceInfo;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.CustomScrollView;
import com.scanchex.utils.DateTimePicker;
import com.scanchex.utils.GPSTracker;
import com.scanchex.utils.JSONParser;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class ScAdminCheckOutScreen extends Activity {

	ImageView imageView;
	Spinner spinnerEmployee, spinnerTolerance, editTextForClient,
			editTextAddress;
	EditText editTextDepartment, editTextDateAndTime, editTextRefrence,
			editTextNOTES;
	TextView des_id, asset_serial, asset_id, departmentName, add_id,
			textViewDueIn, signatureSave;
	CheckBox checkBox1;

	Context mContext;
	private DrawingView drawView;
	private float smallBrush;
	static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "ScanChex";
	String signaturePath = "";
	JSONObject jsonObject;
	ArrayList<ScAdminManualLookModel> employeeArrayList;
	String checkOutId = "", employeeName = "", department = "", dateTime = "",
			dueIn = "", tolerance = "", forClient = "", address = "",
			refrence = "", notes = "", link = "", asset_id_string = "",
			employeefullname = "", assetAddress= "";
	GPSTracker gps;
	String latitude, longitude;
	CustomScrollView scrolViewLayout;
	private String contentType;
	String imageUrl;
	String clientid;
	LinearLayout drawLayout, mainLayoutCheckOut;
	ArrayList<ScToleranceInfo> toleranceArray;
	ArrayList<ScAdminManualLookModel> clientArray;
	ArrayList<ScAdminManualLookModel> clientAddressArray;
	ArrayList<ScAdminManualLookModel> departmentArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkout_screen);
		mContext = this;
		imageView = (ImageView) findViewById(R.id.imageView1);
		drawView = (DrawingView) findViewById(R.id.drawing);
		smallBrush = 5;
		drawView.setBrushSize(smallBrush);
		des_id = (TextView) findViewById(R.id.des_id);
		asset_serial = (TextView) findViewById(R.id.asset_serial);
		asset_id = (TextView) findViewById(R.id.asset_id);
		departmentName = (TextView) findViewById(R.id.departmentName);
		add_id = (TextView) findViewById(R.id.add_id);
		signatureSave = (TextView) findViewById(R.id.signatureSave);
		scrolViewLayout = (CustomScrollView) findViewById(R.id.scrolViewLayout);
		drawLayout = (LinearLayout) findViewById(R.id.drawLayout);
		mainLayoutCheckOut = (LinearLayout) findViewById(R.id.mainLayoutCheckOut);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		drawView.setVisibility(View.INVISIBLE);
		mainLayoutCheckOut.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrolViewLayout.setEnableScrolling(false);
				return false;
			}
		});
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
					// scrolViewLayout.setEnableScrolling(false);

				} else {
					drawView.setVisibility(View.INVISIBLE);
				}

			}
		});

		editTextDepartment = (EditText) findViewById(R.id.editTextDepartment);
		editTextDateAndTime = (EditText) findViewById(R.id.editTextDateAndTime);
		textViewDueIn = (TextView) findViewById(R.id.editTextDueIn);
		spinnerTolerance = (Spinner) findViewById(R.id.spinnerTolerance);
		editTextForClient = (Spinner) findViewById(R.id.editTextForClient);
		editTextAddress = (Spinner) findViewById(R.id.editTextAddress);
		editTextRefrence = (EditText) findViewById(R.id.editTextRefrence);
		editTextNOTES = (EditText) findViewById(R.id.editTextNOTES);
		spinnerEmployee = (Spinner) findViewById(R.id.spinnerEmployee);

		// GET Latitude and Longitude
		gps = new GPSTracker(mContext);
		// check if GPS enabled
		if (gps.canGetLocation()) {

			latitude = gps.getLatitude() + "";
			longitude = gps.getLongitude() + "";

		}

		// Intialize Array
		employeeArrayList = new ArrayList<ScAdminManualLookModel>();
		ScAdminManualLookModel manual = new ScAdminManualLookModel();
		employeeArrayList.add(manual);

		departmentArray = new ArrayList<ScAdminManualLookModel>();
		ScAdminManualLookModel department = new ScAdminManualLookModel();
		departmentArray.add(department);

		toleranceArray = new ArrayList<ScToleranceInfo>();

		clientArray = new ArrayList<ScAdminManualLookModel>();
		ScAdminManualLookModel clientAr = new ScAdminManualLookModel();
		clientArray.add(clientAr);

		String checkOutData = getIntent().getExtras().getString(
				"manualResponce");
		// Log.v("data from pervious screen", "data manual screen \t"
		// + allData);
		String alldata = getIntent().getExtras().getString("alldata");
		Log.v("data from pervious screen", "data employee screen \t" + alldata);
		imageUrl = getIntent().getExtras().getString("imageUrl");
		asset_id_string = getIntent().getExtras().getString("asset_id");
		clientid = getIntent().getExtras().getString("client_id");

		try {
			Picasso.with(mContext).load(imageUrl)
					.placeholder(R.drawable.photo_not_available)
					.error(R.drawable.photo_not_available).into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		textViewDueIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateTimeDialog();
			}
		});
		// editTextDueIn.setText(timeStamp);

		setAlldata(alldata);
		setManualResponce(checkOutData);
		setToleranceSpinner();

	}

	@SuppressLint("InflateParams")
	private void showDateTimeDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(mContext);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		final String timeS = android.provider.Settings.System.getString(
				getContentResolver(),
				android.provider.Settings.System.TIME_12_24);
		final boolean is24h = !(timeS == null || timeS.equals("12"));

		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {

					@SuppressLint("SimpleDateFormat")
					public void onClick(View v) {
						mDateTimePicker.clearFocus();

						// m/d/y
						// String dateTimeStamp;
						try {
							dateTime = ((mDateTimePicker.get(Calendar.MONTH) + 1)
									+ "/"
									+ (mDateTimePicker
											.get(Calendar.DAY_OF_MONTH)) + "/" + mDateTimePicker
									.get(Calendar.YEAR));
							Log.e("selected date", "selected date" + dateTime);

							SimpleDateFormat sdf = new SimpleDateFormat(
									"MM/dd/yyyy");
							Date pdate = sdf.parse(dateTime);
							Log.e("time check", "time check" + dateTime);
							sdf = new SimpleDateFormat("dd/MM/yy");
							String date = sdf.format(pdate);
							
					        //Date with 15 mins from now
							String dateTimeStamp = new SimpleDateFormat(
									"dd/MM/yy", Locale.getDefault())
									.format( new Date(System.currentTimeMillis()+15*60*1000));
							Date chedate = sdf.parse(dateTimeStamp);
							if (!(chedate.after(pdate))) {
								
								String time = (mDateTimePicker
										.get(Calendar.HOUR_OF_DAY)
										+ ":"
										+ mDateTimePicker.get(Calendar.MINUTE));
										

//								String time = (mDateTimePicker
//										.get(Calendar.HOUR_OF_DAY)
//										+ ":"
//										+ mDateTimePicker.get(Calendar.MINUTE)
//										+ " " + (mDateTimePicker
//										.get(Calendar.AM_PM) == Calendar.AM ? "AM"
//										: "PM"));
								Log.v("time in else", "time in else" + time);

								String TimeStamp = new SimpleDateFormat(
										"hh:mm a", Locale.getDefault())
										.format(new Date());
								Log.v("present time", "present time"
										+ TimeStamp);
								if (TimeStamp.equals(time)
										&& (chedate.equals(pdate))) {
									Toast.makeText(
											getApplicationContext(),
											"please select the time at least 15 mintues before current time",
											Toast.LENGTH_LONG).show();
								} else {
									if (mDateTimePicker.is24HourView()) {
										dateTime = dateTime
												+ " "
												+ (mDateTimePicker
														.get(Calendar.HOUR_OF_DAY)
														+ ":" + mDateTimePicker
															.get(Calendar.MINUTE));
										Log.v("date and time in if",
												"date and time in if"
														+ dateTime);
									}

									dateTime = dateTime + "  " + time;
									Log.v("last time", "last time \t "
											+ dateTime + "current time " + dateTimeStamp);
									
									

								}
								
								SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
								
								
								sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
								
								 dateTimeStamp = new SimpleDateFormat(
										"MM/dd/yyyy hh:mm a", Locale.getDefault())
										.format( new Date(System.currentTimeMillis()+15*60*1000));
							
								
								Date dueDate = sdf1.parse(dateTime);
								Date currentTime = sdf.parse(dateTimeStamp);
								String dueInDate = sdf.format(dueDate);
								if(dueDate.before(currentTime)){
					        		System.out.println("Duein is before currentTime+15 mins");
					        		textViewDueIn.setText("");
									Toast.makeText(getApplicationContext(),
											"Please select date greater than current time + 15 mins",
											Toast.LENGTH_LONG).show();
									textViewDueIn.setText("");
					        	} else {
								textViewDueIn.setText(dueInDate);
					        	}
							} else {
								textViewDueIn.setText("");
								Toast.makeText(getApplicationContext(),
										"Please select date greater than current date",
										Toast.LENGTH_LONG).show();
								textViewDueIn.setText("");
							}

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(),
									"exception raised", Toast.LENGTH_LONG)
									.show();
						}

						Log.v("due date in showdialog",
								"due date in showdialog\t" + dateTime);
						mDateTimeDialog.dismiss();
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	private void setToleranceSpinner() {

		// Set values of tolerance
		ScToleranceInfo toleranceInfo = new ScToleranceInfo();
		toleranceArray.add(toleranceInfo);

		ScToleranceInfo toleranceInfo1 = new ScToleranceInfo();
		toleranceInfo1.setValue("300");
		toleranceInfo1.setName("5 min");
		toleranceArray.add(toleranceInfo1);

		ScToleranceInfo toleranceInfo2 = new ScToleranceInfo();
		toleranceInfo2.setValue("600");
		toleranceInfo2.setName("10 min");
		toleranceArray.add(toleranceInfo2);

		ScToleranceInfo toleranceInfo3 = new ScToleranceInfo();
		toleranceInfo3.setValue("900");
		toleranceInfo3.setName("15 min");
		toleranceArray.add(toleranceInfo3);

		ScToleranceInfo toleranceInfo4 = new ScToleranceInfo();
		toleranceInfo4.setValue("1800");
		toleranceInfo4.setName("30 min");
		toleranceArray.add(toleranceInfo4);

		ScToleranceInfo toleranceInfo5 = new ScToleranceInfo();
		toleranceInfo5.setValue("3600");
		toleranceInfo5.setName("1 Hr");
		toleranceArray.add(toleranceInfo5);

		ScToleranceInfo toleranceInfo6 = new ScToleranceInfo();
		toleranceInfo6.setValue("7200");
		toleranceInfo6.setName("2 Hr");
		toleranceArray.add(toleranceInfo6);

		ScToleranceInfo toleranceInfo7 = new ScToleranceInfo();
		toleranceInfo7.setValue("21600");
		toleranceInfo7.setName("6 Hr");
		toleranceArray.add(toleranceInfo7);

		ScToleranceInfo toleranceInfo8 = new ScToleranceInfo();
		toleranceInfo8.setValue("43200");
		toleranceInfo8.setName("12 Hr");
		toleranceArray.add(toleranceInfo8);

		ScToleranceInfo toleranceInfo9 = new ScToleranceInfo();
		toleranceInfo9.setValue("86400");
		toleranceInfo9.setName("1 Day");
		toleranceArray.add(toleranceInfo9);

		ScToleranceInfo toleranceInfo10 = new ScToleranceInfo();
		toleranceInfo10.setValue("604800");
		toleranceInfo10.setName("1 Week");
		toleranceArray.add(toleranceInfo10);

		SpinnerToleranceAdapter adapter = new SpinnerToleranceAdapter(mContext,
				R.layout.spinner_search_view, toleranceArray, "Tolerance");

		spinnerTolerance.setAdapter(adapter);
		spinnerTolerance.setSelection(1);

		// }
		spinnerTolerance
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 > 0) {

							tolerance = toleranceArray.get(arg2).getValue();

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	private void setManualResponce(String checkOutData) {
		try {
			JSONObject json = new JSONObject(checkOutData);
			Log.i("employee link ", "employee link \t " + json);
			checkOutId = json.getString("id");
			Log.i("checkout", "checkout in manual\t" + checkOutId);
			String asset_id_string = json.getString("asset_id");
			String description = json.getString("description");
			String serial_number = json.getString("serial_number");
			String address = json.getString("address");

			try {
				JSONObject addressObject = new JSONObject(address);
				assetAddress = addressObject.getString("city") + " , "
						+ addressObject.getString("state") + "\n"
						+ addressObject.getString("country") + " , "
						+ addressObject.getString("zip_postal_code");
			} catch (Exception e) {
				assetAddress = json.getString("address");
			}

			String department1 = json.getString("department");

			String dateTimeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a",
					Locale.getDefault()).format(new Date());

			des_id.setText(description);
			asset_serial.setText(serial_number);
			asset_id.setText(asset_id_string);

			add_id.setText(address);
			departmentName.setText(department1);
			editTextDateAndTime.setText(dateTimeStamp);
			Log.i("due date in setmaual response ",
					"due date in setmaual response\t" + dateTimeStamp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setAlldata(String alldata) {
		try {
			JSONObject json = new JSONObject(alldata);
			Log.i("employee link ", "employee link \t " + json);
			JSONArray EmployeeArray = new JSONArray();
			JSONArray ClientJsonArray = new JSONArray();
			JSONArray DepartmentArray = new JSONArray();
			EmployeeArray = json.getJSONArray("employees");
			Log.i("employee link ", "EmployeeArray \t " + EmployeeArray);
			ClientJsonArray = json.getJSONArray("clients");
			Log.i("employee link ", "ClientJsonArray \t " + ClientJsonArray);
			DepartmentArray = json.getJSONArray("departments");
			Log.i("employee link ", "ClientJsonArray \t " + DepartmentArray);
			// clientArray
			if (EmployeeArray.length() > 0) {
				for (int j = 0; j < EmployeeArray.length(); j++) {
					JSONObject employeeObject = new JSONObject();
					employeeObject = EmployeeArray.getJSONObject(j);
					ScAdminManualLookModel manual = new ScAdminManualLookModel();
					manual.setFull_name(employeeObject.getString("full_name"));
					manual.setUser_id(employeeObject.getString("user_id"));
					manual.setDepartment(employeeObject.getString("department"));
					manual.setEmployee_url(employeeObject.getString("photo"));

					employeeArrayList.add(manual);
				}
			}

			// Department
			// if (DepartmentArray.length() > 0) {
			// for (int s = 0; s < DepartmentArray.length(); s++) {
			// JSONObject departmentObject = new JSONObject();
			// departmentObject = DepartmentArray.getJSONObject(s);
			// ScAdminManualLookModel dept = new ScAdminManualLookModel();
			// dept.setFull_name(departmentObject.getString("name"));
			// departmentArray.add(dept);
			// }
			// }

			ScAdminManualLookModel info;
			// ArrayList<ScAdminManualLookModel> addressArray = new
			// ArrayList<ScAdminManualLookModel>();
			// ScAdminManualLookModel adress = new ScAdminManualLookModel();
			// addressArray.add(adress);

			if (ClientJsonArray.length() > 0) {
				for (int k = 0; k < ClientJsonArray.length(); k++) {

					ArrayList<ScAdminManualLookModel> addressArray = new ArrayList<ScAdminManualLookModel>();
					ScAdminManualLookModel adress = new ScAdminManualLookModel();
					addressArray.add(adress);

					JSONObject clientObject = new JSONObject();
					clientObject = ClientJsonArray.getJSONObject(k);
					info = new ScAdminManualLookModel();
					info.setId(clientObject.getString("id"));
					info.setFull_name(clientObject.getString("name"));
					JSONArray address = new JSONArray();
					address = clientObject.getJSONArray("addresses");
					if (address.length() > 0) {
						for (int l = 0; l < address.length(); l++) {
							JSONObject addressObject = new JSONObject();
							addressObject = address.getJSONObject(l);
							ScAdminManualLookModel addressInfo = new ScAdminManualLookModel();
							addressInfo.setAddress(addressObject
									.getString("address1"));
							addressInfo
									.setCity(addressObject.getString("city"));
							addressInfo.setState(addressObject
									.getString("state"));
							addressArray.add(addressInfo);
							info.setClientAddress(addressArray);
						}
					}
					clientArray.add(info);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		SpinnerCheckOutClientAdapter adapter = new SpinnerCheckOutClientAdapter(
				mContext, R.layout.spinner_search_view, employeeArrayList,
				"Employee");

		spinnerEmployee.setAdapter(adapter);
		spinnerEmployee.setSelection(0);

		// }
		spinnerEmployee.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 > 0) {
					employeeName = employeeArrayList.get(arg2).getUser_id();
					employeefullname = employeeArrayList.get(arg2)
							.getFull_name();
					 editTextDepartment.setText(employeeArrayList.get(arg2)
					 .getDepartment());
					 //department=employeeArrayList.get(arg2)
					//		 .getDepartment();
				} else {
					 editTextDepartment.setText("");
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		SpinnerCheckOutClientAdapter clientAdapter = new SpinnerCheckOutClientAdapter(
				mContext, R.layout.spinner_search_view, clientArray, "Client");

		editTextForClient.setAdapter(clientAdapter);
		editTextForClient.setSelection(0);

		editTextForClient
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 > 0) {

							forClient = clientArray.get(arg2).getId();
							clientAddressArray = new ArrayList<ScAdminManualLookModel>();
							clientAddressArray = clientArray.get(arg2)
									.getClientAddress();

							SpinnerClientAddressAdapter clientAdapter = new SpinnerClientAddressAdapter(
									mContext, R.layout.spinner_search_view,
									clientAddressArray, "Address");

							editTextAddress.setAdapter(clientAdapter);
							editTextAddress.setSelection(0);
							editTextAddress
									.setOnItemSelectedListener(new OnItemSelectedListener() {

										@Override
										public void onItemSelected(
												AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
											if (arg2 > 0) {

												address = clientAddressArray
														.get(arg2).getAddress()
														+ "\n"
														+ clientAddressArray
																.get(arg2)
																.getCity()
														+ " , "
														+ clientAddressArray
																.get(arg2)
																.getState()
														+ "\n"
														+ clientAddressArray
																.get(arg2)
																.getCountry()
														+ " , "
														+ clientAddressArray
																.get(arg2)
																.getZipCode();

											} else {
												address = "";
											}

										}

										@Override
										public void onNothingSelected(
												AdapterView<?> arg0) {

										}
									});

						} else {

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

	}

	public void onClickCheckOut(View v) {
		if (checkBox1.isChecked()  && isClick == true) {
		 department = editTextDepartment.getText().toString();
			dateTime = editTextDateAndTime.getText().toString();
			dueIn = textViewDueIn.getText().toString();
			Log.v("due date in onclick ", "due date in onclick\t" + dueIn);
			// forClient = editTextForClient.getText().toString();
			// address = editTextAddress.getText().toString();
			// Log.v("username", "username in onclick" + employeeName);
			refrence = editTextRefrence.getText().toString();
			notes = editTextNOTES.getText().toString();
			if (!department.equals("") && !dateTime.equals("")
					&& !dueIn.equals("") && !tolerance.equals("")
					&& !forClient.equals("") && !address.equals("")
					&& !asset_id_string.equals("") && !link.equals("")
					&& !employeeName.equals("")) {

				new CheckOutAsyncTask().execute();

			} else {
				Toast.makeText(mContext, "Please fill all fields",
						Toast.LENGTH_LONG).show();
			}
		} else {
			showToast("Please sign &  accept conditions first");
		}
	}

	public void onClickBack(View v) {
		finish();
	}

	ProgressDialog pdialog;

	public void showProgressDialog() {
		pdialog = new ProgressDialog(mContext);
		pdialog.setIcon(R.drawable.info_icon);
		pdialog.setTitle("Loading Manual Data");
		pdialog.setMessage("Working...");
		pdialog.show();
	}

	public void hideProgressDialog() {
		pdialog.dismiss();
	}

	public void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	// Drawing Buttons
	boolean isClick = false;

	public void onClickAccept(View v) {

		scrolViewLayout.setEnableScrolling(true);
		if (isClick == false && checkBox1.isChecked()) {
			isClick = true;
			drawView.setDrawingCacheEnabled(true);
			drawView.setBrushSize(-1);
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
				nameValuePairs.add(new BasicNameValuePair("check_out_id",
						checkOutId));
				Log.i("checkout", "checkout in checkout async \t" + checkOutId);
				nameValuePairs.add(new BasicNameValuePair("employee",
						employeeName));
				Log.i("employee", "emp name in checkout async \t"
						+ employeeName);
				nameValuePairs.add(new BasicNameValuePair("department",
						department));
				nameValuePairs.add(new BasicNameValuePair("date_time_out",
						dateTime));
				nameValuePairs.add(new BasicNameValuePair("date_time_due_in",
						dueIn));
				Log.i("due date in doinbackground ",
						"due date in doinbackground\t" + dueIn);
				nameValuePairs.add(new BasicNameValuePair("tolerance",
						tolerance));
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
				// nameValuePairs.add(new BasicNameValuePair("user_id",
				// SCPreferences.getPreferences().getUserName(mContext)));
				nameValuePairs.add(new BasicNameValuePair("user_id",
						employeeName));
				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/checkout", "POST",
						nameValuePairs);
				Log.i("json object feed", "Json object feed" + jsonObject);
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
			Log.i("json object feed", "Json object feed" + data);
			JSONArray checkOutObject = new JSONArray();
			Log.i("json object feed", "Json object feed" + checkOutObject);
			if (json != null) {
				try {
					data = json.getJSONObject("data");
					checkOutObject = data.getJSONArray("checkout");
					String ticket_id = data.getString("ticket_id");
					String ticket_number = data.getString("ticket_number");
					data = checkOutObject.getJSONObject(0);

					if (!(ticket_id.equals(""))) {

						Intent checkoutView = new Intent(mContext,
								ScAdminCheckOutConfirmationScreen.class);
						checkoutView
								.putExtra("manualResponce", data.toString());
						checkoutView.putExtra("imageUrl", imageUrl);
						checkoutView.putExtra("name", employeeName);
						checkoutView.putExtra("ticketId", ticket_id);
						checkoutView.putExtra("ticket_number", ticket_number);
						checkoutView.putExtra("employeename", employeefullname);
						checkoutView.putExtra("duein", dueIn);
						checkoutView.putExtra("department", department);
						checkoutView.putExtra("address", assetAddress);
						
						checkoutView
								.putExtra("title", "CHECK-OUT CONFIRMATION");
						startActivity(checkoutView);
						Log.v("manualResponce",
								"manualResponce \t" + data.toString()
										+ "tickect id\t" + ticket_id
										+ "ticket_number\t" + ticket_number);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					showToast("Please check all fields");
				}
			}

		}
	}

	private String getContentType(Uri uri) {
		ContentResolver cR = getContentResolver();
		return cR.getType(uri);
	}

}
