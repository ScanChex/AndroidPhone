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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SpinnerManualAdapter;
import com.scanchex.adapters.SpinnerManualClientAdapter;
import com.scanchex.bo.ScAdminManualLookModel;
import com.scanchex.ui.ScAdminManualLookUp.AllDataApiAsyncTask;
import com.scanchex.ui.ScAdminManualLookUp.ManualLookUpAsyncTask;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.JSONParser;
import com.scanchex.utils.Network;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class ScAdminScanLookUp extends BaseActivity {

	ImageView imageView;
	EditText editTextSerialNumber, editTextDepartment, editTextAddress,
			editTextClient, editTextAssetId;
	TextView textViewAssetDescription, textViewClient;;
	Button buttonTitle;
	Context mContext;
	String assetObject, client_name, client_id, allData;
	Spinner spinnerAssetId;
	ArrayList<ScAdminManualLookModel> clientArrayList;
	ArrayList<ScAdminManualLookModel> assetArrayList;
	ArrayList<ScAdminManualLookModel> departmentsArrayList;
	ArrayList<ScAdminManualLookModel> addressesArrayList;
	String master_key = "", description = "", serial_number = "", address = "",
			department = "", user_id = "", type = "", asset_id = "", link = "",
			client = "", assetID = "";
	JSONObject jsonObject, manualObject;
	JSONObject allDataApi;
	String imageUrl = "";
	String id = "";
	String status = "";
	String clientid = "";
	EditText editViewSerialNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_scanlookup);
		mContext = this;
		imageView = (ImageView) findViewById(R.id.imageView1);
		editTextClient = (EditText) findViewById(R.id.editTextClient);
		editTextAssetId = (EditText) findViewById(R.id.editTextAssetId);
		textViewAssetDescription = (TextView) findViewById(R.id.textViewAssetDescription);
		editTextSerialNumber = (EditText) findViewById(R.id.editTextSerialNumber);
		editTextAddress = (EditText) findViewById(R.id.editTextAddress);
		editTextDepartment = (EditText) findViewById(R.id.editTextDepartment);
		buttonTitle = (Button) findViewById(R.id.button2);
		buttonTitle.setText("SCAN LOOK UP");

		// get extra from ScAdminManualLookup
		assetObject = getIntent().getStringExtra("assetObject");
		client_id = getIntent().getStringExtra("id");
		client_name = getIntent().getStringExtra("name");
		allData = getIntent().getStringExtra("alldatapi");
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
			setEmployee(assetObject);
			// new AllDataApiAsyncTask().execute();
		} else {
			showToast("Please check your internet connection");
		}
	}

	private void setEmployee(String assetObject2) {
		// TODO Auto-generated method stub

		try {
			JSONObject json = new JSONObject(assetObject);
			imageUrl = json.getString("asset_photo");
			asset_id = json.getString("asset_id");
			assetID = json.getString("id");
			description = json.getString("description");
			serial_number = json.getString("serial_number");

			department = json.getString("department");
			// user_id = json.getString("asset_code_url");
			type = json.getString("asset_status");
			id = json.getString("id");
			JSONObject jobj = json.getJSONObject("address");
			address = jobj.getString("address1") + jobj.getString("city")
					+ "\n" + jobj.getString("state") + "\t"
					+ jobj.getString("country")
					+ jobj.getString("zip_postal_code");
			try {
				Picasso.with(mContext).load(imageUrl)
						.placeholder(R.drawable.photo_not_available)
						.error(R.drawable.photo_not_available).into(imageView);

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"unable to process image" + e, Toast.LENGTH_LONG)
						.show();
			}
			editTextClient.setText(client_name);
			editTextAssetId.setText(asset_id);
			textViewAssetDescription.setText(description);
			editTextSerialNumber.setText(serial_number);
			editTextAddress.setText(address);
			editTextDepartment.setText(department);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onClickCheckOut(View v) {
		master_key = SCPreferences.getPreferences().getUserMasterKey(mContext);
		user_id = SCPreferences.getPreferences().getUserName(mContext);
		// asset_id = editTextAssetId.getText().toString();

		description = textViewAssetDescription.getText().toString();
		serial_number = editTextSerialNumber.getText().toString();
		address = editTextAddress.getText().toString();
		department = editTextDepartment.getText().toString();
		type = "check_out";

		new ManualLookUpAsyncTask().execute();

	}

	public void onClickBack(View v) {
		finish();
	}

	ProgressDialog pdialog;

	public void showProgressDialog() {
		pdialog = new ProgressDialog(mContext);
		pdialog.setIcon(R.drawable.info_icon);
		pdialog.setTitle("Loading Scan Data");
		pdialog.setMessage("Working...");
		pdialog.show();
	}

	public void hideProgressDialog() {
		pdialog.dismiss();
	}

	public void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
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
				// Log.v("jobj in doinbackground", "jobj in doinbackground"
				// + jsonObject);
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
			// JSONObject jsonResponce = new JSONObject();
			Log.d("json in postmethod", "json in postmethod" + json.toString());
			String msg = "";

			try {
				allDataApi = json.getJSONObject("data");
				allData = allDataApi.toString();
				JSONObject getObject = new JSONObject();
				JSONArray assetsArray = new JSONArray();
				JSONArray departmentsArray = new JSONArray();
				JSONArray addressesArray = new JSONArray();
				JSONArray clientsArray = new JSONArray();
				Log.v("json all data ", "json all data \t" + allData);
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

		private void spinnerAsset() {

			SpinnerManualAdapter adapter = new SpinnerManualAdapter(mContext,
					R.layout.spinner_search_view, assetArrayList, "Assets");

			spinnerAssetId.setAdapter(adapter);
			spinnerAssetId.setSelection(0);

			spinnerAssetId
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

							if (arg2 > 0) {

								String description = assetArrayList.get(arg2)
										.getDescription();
								asset_id = assetArrayList.get(arg2).getId();
								textViewAssetDescription.setText(description);
								clientid = assetArrayList.get(arg2)
										.getClient_id();
								imageUrl = "";
								imageUrl = assetArrayList.get(arg2)
										.getAsset_photo();
								// Log.v("image link", "image link \t" +
								// imageUrl);
								try {
									Picasso.with(mContext)
											.load(imageUrl)
											.placeholder(
													R.drawable.photo_not_available)
											.error(R.drawable.photo_not_available)
											.into(imageView);
									// Log.v("image link", "image link \t" +
									// imageUrl);
								} catch (Exception e) {
									e.printStackTrace();
									Toast.makeText(getApplicationContext(),
											"unable to process image" + e,
											Toast.LENGTH_LONG).show();
								}
								editTextAddress.setText(assetArrayList
										.get(arg2).getAddress());
								editTextDepartment.setText(assetArrayList.get(
										arg2).getDepartment());
								editViewSerialNumber.setText(assetArrayList
										.get(arg2).getSerial_number());

									for (int k = 0; k < clientArrayList.size(); k++) {
										if (assetArrayList
												.get(arg2)
												.getClient_id()
												.equals(clientArrayList.get(k)
														.getId())) {
											textViewClient
													.setText(clientArrayList
															.get(k).getName());
											client = clientArrayList.get(k)
													.getId();
										}
									}

							} else {
								textViewAssetDescription.setText("");
								imageView
										.setImageResource(R.drawable.photo_not_available);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

		}
	}

	public class ManualLookUpAsyncTask extends
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

				// List<NameValuePair> params = new ArrayList<NameValuePair>();
				//
				// params.add(new BasicNameValuePair("master_key", master_key));
				// params.add(new BasicNameValuePair("asset_id", id));
				//
				// jsonObject = jsonParser
				// .makeHttpRequest(CONSTANTS.BASE_URL_ADMIN
				// + "asset/info", "POST", params);

				List<NameValuePair> param = new ArrayList<NameValuePair>();

				param.add(new BasicNameValuePair("master_key", master_key));
				param.add(new BasicNameValuePair("description", description));
				param.add(new BasicNameValuePair("serial_number", serial_number));
				param.add(new BasicNameValuePair("client", client_id));
				param.add(new BasicNameValuePair("address", address));
				param.add(new BasicNameValuePair("department", department));
				param.add(new BasicNameValuePair("user_id", user_id));
				param.add(new BasicNameValuePair("type", "check_out"));
				param.add(new BasicNameValuePair("asset_id", assetID));

				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/manual_lookup",
						"POST", param);
				Log.v("scan lookout", "scan lookout" + "masterkey \t"
						+ master_key + "description\t" + description
						+ "serial_number \t" + serial_number + "\n client \t"
						+ client_id + "address\t" + address + "department \t"
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
				checkoutView.putExtra("asset_id", assetID);
				checkoutView.putExtra("client_id", client_id);
				checkoutView.putExtra("alldata", allData);
				Log.v("onpost assetArray", "allData \t" + allData + "\n"
						+ "client \t" + client);

				startActivity(checkoutView);
				finish();

			} catch (JSONException e) {
				e.printStackTrace();
				try {
					JSONArray array = data.getJSONArray("msg");
					for (int i = 0; i < array.length(); i++) {
						String msg = array.getString(i);
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
						dialog.dismiss();
					}
				}).show();
	}
}
