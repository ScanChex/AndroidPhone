package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SpinnerSearchAdapter;
import com.scanchex.bo.SCMessageInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.AdminUserNameModel;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCAdminMainMenuScreen extends Activity {

	private TextView employeeName;
	String getUsername = "";
	String IMEI;
	String resgisteredEmp;
	ArrayList<AdminUserNameModel> userNameAray;
	Spinner userName;
	ImageView logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_mainmenu_screen);
		LinearLayout layout = (LinearLayout) findViewById(R.id.adminMenuCOntainer);
		logo = (ImageView) findViewById(R.id.logo);
		userNameAray = new ArrayList<AdminUserNameModel>();
		layout.setBackgroundColor((SCPreferences
				.getColor(SCAdminMainMenuScreen.this)));
		TelephonyManager telephonyManager = (TelephonyManager) SCAdminMainMenuScreen.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();
		employeeName = (TextView) findViewById(R.id.mainmenu_employeename_text);
		employeeName.setText(getIntent().getExtras().getString("NAME"));
		userName = (Spinner) findViewById(R.id.spinner1);

		String url = SCPreferences.getPreferences().getClientLogo(
				SCAdminMainMenuScreen.this);

		try {
			Picasso.with(this).load(url)
					.placeholder(R.drawable.photo_not_available)
					.error(R.drawable.app_icon).into(logo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new UserNameTask().execute(CONSTANTS.BASE_URL);
		

	}

	private void spinnerSearchPlantGroup() {

		SpinnerSearchAdapter adapter = new SpinnerSearchAdapter(
				SCAdminMainMenuScreen.this, R.layout.spinner_search_view,
				userNameAray);
		userName.setAdapter(adapter);
		userName.setSelection(0);

		userName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if (arg2 > 0) {

					getUsername = userNameAray.get(arg2).getUser_id()
							.toString();
					if (getUsername.equalsIgnoreCase("")) {
						Toast.makeText(SCAdminMainMenuScreen.this,
								"Please enter username", Toast.LENGTH_LONG)
								.show();
					} else {
						new registerDeviceIdTask().execute(CONSTANTS.BASE_URL);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void onUploadImageClick(View view) {

		Resources.getResources().setFromAdminTakePicture(true);
		Intent intent = new Intent(this, SCAdminTapToScanScreen.class);
		startActivity(intent);
	}

	public void onLockLocationClick(View view) {

		Resources.getResources().setFromAdminTakePicture(false);
		Intent intent = new Intent(this, SCAdminTapToScanScreen.class);
		startActivity(intent);
	}

	public void onRegisterEmployeeDevice(View view) {
		// showMessgaeDialog();

	}

	public void onClickCheckin(View v) {
		Intent cico = new Intent(SCAdminMainMenuScreen.this,
				ScAdminCheckinCheckoutMenu.class);
		startActivity(cico);
	}

	EditText messageText;

	private void showMessgaeDialog() {
		final Dialog dialog = new Dialog(SCAdminMainMenuScreen.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.sc_popup_registerdevice_dialog);

		messageText = (EditText) dialog.findViewById(R.id.editTextMessage);
		Button cancel = (Button) dialog.findViewById(R.id.buttonCancel);
		Button send = (Button) dialog.findViewById(R.id.buttonSend);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getUsername = messageText.getText().toString();
				if (getUsername.equalsIgnoreCase("")) {
					Toast.makeText(SCAdminMainMenuScreen.this,
							"Please enter username", Toast.LENGTH_LONG).show();
				} else {
					new registerDeviceIdTask().execute(CONSTANTS.BASE_URL);
				}
			}
		});

		dialog.show();
	}

	public void onLogoutClick(View view) {
		removeAdminPrefrencesData();
		this.finish();
	}

	private void removeAdminPrefrencesData() {

		SCPreferences.getPreferences().setUserName(SCAdminMainMenuScreen.this,
				"");
		SCPreferences.getPreferences().setCompanyId(SCAdminMainMenuScreen.this,
				"");
		SCPreferences.getPreferences().setUserMasterKey(
				SCAdminMainMenuScreen.this, "");
		SCPreferences.getPreferences().setUserFullName(
				SCAdminMainMenuScreen.this, "");
	}

	private class registerDeviceIdTask extends
			AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String messgae;

		private Vector<SCMessageInfo> vector;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("MESSAGE URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();

				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCAdminMainMenuScreen.this)
								+ ""));
				listParams.add(new BasicNameValuePair("created_by",
						SCPreferences.getPreferences().getUserName(
								SCAdminMainMenuScreen.this)));
				listParams.add(new BasicNameValuePair("username",
						resgisteredEmp));
				listParams.add(new BasicNameValuePair("uuid", IMEI));
				listParams
						.add(new BasicNameValuePair("device_type", "android"));
				listParams.add(new BasicNameValuePair("action", "device"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				messgae = obj.getString("status");
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

				getUsername = "";
				Toast.makeText(getApplicationContext(), messgae,
						Toast.LENGTH_LONG).show();
				userName.setSelection(0);

			} else {
				userName.setSelection(0);
			}
			new UserNameTask().execute(CONSTANTS.BASE_URL);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			resgisteredEmp = "";

			for (int i = 1; i < userNameAray.size(); i++) {

				//
				AdminUserNameModel model3 = new AdminUserNameModel();

				model3 = userNameAray.get(i);

				Log.e("array list", "array list \t" + model3);
				int resu = userNameAray.get(i).getisregistered();
				Log.e("array list", "array list \t" + resu);
				if (resu == 1) {

					resgisteredEmp += userNameAray.get(i).getUser_id() + ",";
					Log.e("resgisteredEmp", "resgisteredEmp \t"
							+ resgisteredEmp);
				} else {
					Log.e("resgisteredEmp", "resgisteredEmp \t"
							+ resgisteredEmp);
				}

			}

			resgisteredEmp += getUsername;

			pdialog = new ProgressDialog(SCAdminMainMenuScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Message");
			pdialog.setMessage("Sending...");
			pdialog.show();
		}
	}

	private class UserNameTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;

		protected Boolean doInBackground(String... params) {
			try {

				List<NameValuePair> listParams = new ArrayList<NameValuePair>();

				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCAdminMainMenuScreen.this)));
				listParams.add(new BasicNameValuePair("action",
						"employees_list"));
				listParams.add(new BasicNameValuePair("udid", IMEI));
				// Log.e("name", "name \t" + IMEI);
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				// Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject jObject = new JSONObject(response);
				JSONArray jsonArray = jObject.getJSONArray("employees");
				AdminUserNameModel model1 = new AdminUserNameModel();
				userNameAray.add(model1);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						jObject = jsonArray.getJSONObject(i);
						AdminUserNameModel model = new AdminUserNameModel();
						model.setUser_id(jObject.getString("user_id"));
						model.setFull_name(jObject.getString("full_name"));
						model.setPhoto(jObject.getString("photo"));
						model.setisregistered(jObject.getInt("is_registered"));
						Log.v("executed inusername", "executed inusername \t");

						userNameAray.add(model);
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
			if (result) {
				spinnerSearchPlantGroup();
			} else {
				Toast.makeText(getBaseContext(), "No Result", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCAdminMainMenuScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Assets & Tickets");
			pdialog.setMessage("Working...");
			pdialog.show();
			userNameAray = new ArrayList<AdminUserNameModel>();
		}
	}

}
