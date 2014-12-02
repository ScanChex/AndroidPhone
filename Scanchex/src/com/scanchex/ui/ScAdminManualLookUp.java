package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SpinnerManualAdapter;
import com.scanchex.bo.ScAdminManualLookModel;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.JSONParser;
import com.scanchex.utils.Network;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class ScAdminManualLookUp extends BaseActivity {

	ImageView imageView;
	Spinner spinnerAssetId;

	EditText editTextAddress, editTextDepartment;
	EditText editViewSerialNumber;
	TextView textViewAssetDescription, textViewClient;
	Context mContext;
	JSONObject jsonObject;

	ArrayList<ScAdminManualLookModel> clientArrayList;
	ArrayList<ScAdminManualLookModel> assetArrayList;
	ArrayList<ScAdminManualLookModel> departmentsArrayList;
	ArrayList<ScAdminManualLookModel> addressesArrayList;
	String master_key = "", description = "", serial_number = "", client = "",
			address = "", department = "", user_id = "", type = "",
			asset_id = "", asset_status = "";
	JSONObject allDataApi;
	String imageUrl = "";
	String status = "";
	String clientid = "";
	String allData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_manuallookup);
		mContext = this;
		imageView = (ImageView) findViewById(R.id.imageView1);
		textViewClient = (TextView) findViewById(R.id.textViewClient);
		spinnerAssetId = (Spinner) findViewById(R.id.spinnerAssetId);
		textViewAssetDescription = (TextView) findViewById(R.id.textViewAssetDescription);
		editViewSerialNumber = (EditText) findViewById(R.id.editViewSerialNumber);
		editTextAddress = (EditText) findViewById(R.id.spinnerViewAddress);
		editTextDepartment = (EditText) findViewById(R.id.spinnerViewDepartment);

		// Intialize Array
		clientArrayList = new ArrayList<ScAdminManualLookModel>();
		assetArrayList = new ArrayList<ScAdminManualLookModel>();
		departmentsArrayList = new ArrayList<ScAdminManualLookModel>();
		addressesArrayList = new ArrayList<ScAdminManualLookModel>();
		ScAdminManualLookModel manual = new ScAdminManualLookModel();
		clientArrayList.add(manual);
		assetArrayList.add(manual);
		departmentsArrayList.add(manual);
		addressesArrayList.add(manual);

		if (Network.haveNetworkConnection(mContext)) {
			new AllDataApiAsyncTask().execute();
		} else {
			showToast("Please check your internet connection");
		}
	}

	public void onClickCheckOut(View v) {
		if (!(status.equalsIgnoreCase("checked_out"))) {
			master_key = SCPreferences.getPreferences().getUserMasterKey(
					mContext);
			user_id = SCPreferences.getPreferences().getUserName(mContext);
			description = textViewAssetDescription.getText().toString();
			serial_number = editViewSerialNumber.getText().toString();
			address = editTextAddress.getText().toString();
			department = editTextDepartment.getText().toString();
			if (!(asset_id.equalsIgnoreCase(""))) {
				type = textViewAssetDescription.getText().toString();
			}

			if ((master_key.equalsIgnoreCase("")
					&& description.equalsIgnoreCase("")
					&& serial_number.equalsIgnoreCase("")
					&& client.equalsIgnoreCase("")
					&& address.equalsIgnoreCase("")
					&& department.equalsIgnoreCase("")
					&& user_id.equalsIgnoreCase("")
					&& type.equalsIgnoreCase("") && asset_id
						.equalsIgnoreCase(""))) {
				Toast.makeText(mContext, "Please fill fields",
						Toast.LENGTH_LONG).show();
			} else {
				new ManualLookUpAsyncTask().execute();
			}
		}
	}

	public void onClickBack(View v) {
		finish();
	}

	public class AllDataApiAsyncTask extends
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

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("master_key", SCPreferences
						.getPreferences().getUserMasterKey(mContext)));

				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/all", "POST", params);
				Log.v("jobj in doinbackground", "jobj in doinbackground"
						+ jsonObject);
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
			JSONObject jsonResponce = new JSONObject();
			//Log.d("json in postmethod", "json in postmethod" + json.toString());
			String msg = "";

			try {
				allDataApi = json.getJSONObject("data");
				allData = allDataApi.toString();
				JSONObject getObject = new JSONObject();
				JSONArray assetsArray = new JSONArray();
				JSONArray departmentsArray = new JSONArray();
				JSONArray addressesArray = new JSONArray();
				JSONArray clientsArray = new JSONArray();

				assetsArray = allDataApi.getJSONArray("assets");
				departmentsArray = allDataApi.getJSONArray("departments");
				addressesArray = allDataApi.getJSONArray("addresses");
				clientsArray = allDataApi.getJSONArray("clients");

				// Set Asset Array

				if (assetsArray.length() > 0) {
					for (int asset = 0; asset < assetsArray.length(); asset++) {
						getObject = assetsArray.getJSONObject(asset);
						ScAdminManualLookModel manual = new ScAdminManualLookModel();
						manual.setId(getObject.getString("id"));
						manual.setAsset_id(getObject.getString("asset_id"));
						manual.setDescription(getObject
								.getString("description"));
						manual.setClient_id(getObject.getString("client_id"));
						// Log.v("client Id",
						// "client Id" + getObject.getString("client_id"));
						department = getObject.getString("department");
						manual.setDepartment(getObject.getString("department"));
						JSONObject addressObject = new JSONObject();
						addressObject = getObject.getJSONObject("address");
						address = addressObject.getString("city") + " , "
								+ addressObject.getString("state") + "\n"
								+ addressObject.getString("country") + " , "
								+ addressObject.getString("zip_postal_code");
						manual.setAddress(address);
						manual.setAsset_photo(getObject
								.getString("asset_photo"));
						manual.setAsset_code(getObject.getString("asset_code"));
						manual.setAsset_url(getObject
								.getString("asset_code_url"));
						manual.setSerial_number(getObject
								.getString("serial_number"));
						manual.setAsset_status(getObject
								.getString("asset_status"));
						manual.setDisplaySpinnerName(getObject
								.getString("asset_id"));
						assetArrayList.add(manual);
					}
				}

				// Set Departments

				if (departmentsArray.length() > 0) {
					for (int j = 0; j < departmentsArray.length(); j++) {
						getObject = departmentsArray.getJSONObject(j);
						ScAdminManualLookModel manual = new ScAdminManualLookModel();
						manual.setName(getObject.getString("name"));
						manual.setDisplaySpinnerName(getObject
								.getString("name"));
						departmentsArrayList.add(manual);
					}
				}

				if (addressesArray.length() > 0) {
					for (int k = 0; k < addressesArray.length(); k++) {
						getObject = addressesArray.getJSONObject(k);
						ScAdminManualLookModel manual = new ScAdminManualLookModel();

						manual.setCity(getObject.getString("city"));

						manual.setState(getObject.getString("state"));

						manual.setCountry(getObject.getString("country"));

						manual.setZipCode(getObject
								.getString("zip_postal_code"));

						manual.setDisplaySpinnerName(getObject
								.getString("address1"));

						addressesArrayList.add(manual);
					}
				}

				if (clientsArray.length() > 0) {
					for (int k = 0; k < clientsArray.length(); k++) {
						getObject = clientsArray.getJSONObject(k);
						ScAdminManualLookModel manual = new ScAdminManualLookModel();
						manual.setId(getObject.getString("id"));
						manual.setName(getObject.getString("name"));
						clientArrayList.add(manual);
					}
				}

				// spinnerClient();
				spinnerAsset();

			} catch (JSONException e) {
				e.printStackTrace();
				showToast(msg + e);
			}

		}
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

	private void spinnerAsset() {

		SpinnerManualAdapter adapter = new SpinnerManualAdapter(mContext,
				R.layout.spinner_search_view, assetArrayList, "Assets");

		spinnerAssetId.setAdapter(adapter);
		spinnerAssetId.setSelection(0);

		spinnerAssetId.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				asset_status = assetArrayList.get(arg2).getAsset_status();
				Log.v("asset_status in spinner", "asset_status in spinner"
						+ asset_status);
				if (arg2 > 0) {
					if (!(asset_status.equalsIgnoreCase("checked_out"))) {
						String description = assetArrayList.get(arg2)
								.getDescription();
						asset_id = assetArrayList.get(arg2).getId();
						textViewAssetDescription.setText(description);
						clientid = assetArrayList.get(arg2).getClient_id();
						imageUrl = "";
						imageUrl = assetArrayList.get(arg2).getAsset_photo();
						// Log.v("image link", "image link \t" + imageUrl);
						try {
							Picasso.with(mContext)
									.load(imageUrl)
									.placeholder(R.drawable.photo_not_available)
									.error(R.drawable.photo_not_available)
									.into(imageView);
							// Log.v("image link", "image link \t" + imageUrl);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"unable to process image" + e,
									Toast.LENGTH_LONG).show();
						}
						editTextAddress.setText(assetArrayList.get(arg2)
								.getAddress());
						editTextDepartment.setText(assetArrayList.get(arg2)
								.getDepartment());
						editViewSerialNumber.setText(assetArrayList.get(arg2)
								.getSerial_number());

						for (int k = 0; k < clientArrayList.size(); k++) {
							if (assetArrayList.get(arg2).getClient_id()
									.equals(clientArrayList.get(k).getId())) {
								textViewClient.setText(clientArrayList.get(k)
										.getName());
								client = clientArrayList.get(k).getId();
							}
						}

					} else {
						showOptionAlert("Info",
								"This asset is already checkout");
					}
				} else {
					textViewAssetDescription.setText("");
					imageView.setImageResource(R.drawable.photo_not_available);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	public class ManualLookUpAsyncTask extends
			AsyncTask<JSONObject, JSONObject, JSONObject> {

		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(mContext);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Manual Data");
			pdialog.setMessage("Working...");
			pdialog.show();
		}

		@Override
		protected JSONObject doInBackground(JSONObject... strings) {
			jsonObject = new JSONObject();
			try {

				JSONParser jsonParser = new JSONParser();

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("master_key", master_key));
				params.add(new BasicNameValuePair("description", description));
				params.add(new BasicNameValuePair("serial_number",
						serial_number));
				params.add(new BasicNameValuePair("address", address));
				params.add(new BasicNameValuePair("client", client));
				params.add(new BasicNameValuePair("department", department));
				params.add(new BasicNameValuePair("user_id", user_id));
				params.add(new BasicNameValuePair("type", "check_out"));
				params.add(new BasicNameValuePair("asset_id", asset_id));

				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/manual_lookup",
						"POST", params);
				Log.v("scan lookout", "scan lookout" + "masterkey \t"
						+ master_key + "description\t" + description
						+ "serial_number \t" + serial_number + "\n client \t"
						+ client + "address\t" + address + "department \t"
						+ department + "user_id\t" + user_id + "type \t"
						+ "check_out" + "asset_id" + asset_id);
				
				
				
				
				
				
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
			// Log.v("data", "data \t" + data);
			JSONArray checkOutObject = new JSONArray();
			// Log.v("checkOutObject", "checkOutObject \t" + checkOutObject);
			try {
				data = json.getJSONObject("data");
				// Log.d("Checkout data", data.toString());
				checkOutObject = data.getJSONArray("checkout");
				data = checkOutObject.getJSONObject(0);
				// Log.d("Checkout", data.toString());

				Intent checkoutView = new Intent(mContext,
						ScAdminCheckOutScreen.class);
				checkoutView.putExtra("manualResponce", data.toString());
				checkoutView.putExtra("imageUrl", imageUrl);
				checkoutView.putExtra("asset_id", asset_id);
				checkoutView.putExtra("client_id", client);
				checkoutView.putExtra("alldata", allData);

				startActivity(checkoutView);
				finish();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Please select the asset", Toast.LENGTH_LONG).show();

			}

		}
	}

	private void showOptionAlert(String title, String message) {

		new AlertDialog.Builder(this).setIcon(R.drawable.info_icon)
				.setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						spinnerAssetId.setSelection(0);

						textViewAssetDescription.setText("");
						editViewSerialNumber.setText("");
						editTextAddress.setText("");
						textViewClient.setText("");
						editTextDepartment.setText("");
						editViewSerialNumber.setText("");
						dialog.dismiss();
					}
				}).show();
	}
}
