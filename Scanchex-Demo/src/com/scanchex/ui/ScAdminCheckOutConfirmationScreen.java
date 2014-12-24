package com.scanchex.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.JSONParser;
import com.squareup.picasso.Picasso;

public class ScAdminCheckOutConfirmationScreen extends BaseActivity {

	ImageView imageView;

	EditText editTextCheckOut, editTextTicketIdCreated, editTextReTurn;
	TextView des_id, asset_serial, asset_id, departmentName, add_id,
			textViewConfirmTitle, textViewReturnTitle;
	CheckBox checkBox1;
	Context mContext;
	String ticketReturn = "", employeeName = "", ticketId = "", id = "",
			imageUrl = "", asset_id_string = "", clientid = "",
			duein_date = "", department = "", title = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_admin_checkout_confirmation);
		mContext = this;
		imageView = (ImageView) findViewById(R.id.imageView1);

		des_id = (TextView) findViewById(R.id.des_id);
		asset_serial = (TextView) findViewById(R.id.asset_serial);
		asset_id = (TextView) findViewById(R.id.asset_id);
		departmentName = (TextView) findViewById(R.id.departmentName);
		add_id = (TextView) findViewById(R.id.add_id);
		textViewConfirmTitle = (TextView) findViewById(R.id.textViewConfirmTitle);
		textViewReturnTitle = (TextView) findViewById(R.id.textView6);


		// EditText Fields
		editTextCheckOut = (EditText) findViewById(R.id.editTextCheckOut);
		editTextTicketIdCreated = (EditText) findViewById(R.id.editTextTicketIdCreated);
		editTextReTurn = (EditText) findViewById(R.id.editTextReTurn);
		String checkOutData = getIntent().getExtras().getString(
				"manualResponce");
		Log.v("data from pervious screen", "data from pervious screen1 \t"
				+ checkOutData);
		// String employee = getIntent().getExtras().getString("client");
		// Log.v("data from pervious screen", "data from pervious screen \t"
		// + employee);
		title = getIntent().getExtras().getString("title");
		imageUrl = getIntent().getExtras().getString("imageUrl");
		asset_id_string = getIntent().getExtras().getString("asset_id");
		clientid = getIntent().getExtras().getString("clientid");
		employeeName = getIntent().getExtras().getString("employeename");
		ticketId = getIntent().getExtras().getString("ticketId");
		duein_date = getIntent().getExtras().getString("duein");
		department = getIntent().getExtras().getString("department");
		String address = getIntent().getExtras().getString("address");
		Log.v("duein_date", "duein_date" + duein_date);
		try {
			Picasso.with(mContext).load(imageUrl)
					.placeholder(R.drawable.photo_not_available)
					.error(R.drawable.photo_not_available).into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ticket_number = getIntent().getExtras().getString(
				"ticket_number");

		String timeStamp = new SimpleDateFormat("MM/dd/yy hh:mm aa",
				Locale.getDefault()).format(new Date());

		textViewConfirmTitle.setText(title);
		
		editTextCheckOut.setText(employeeName);
		editTextTicketIdCreated.setText(ticket_number);
		editTextReTurn.setText(duein_date);
		departmentName.setText(department);
		add_id.setText(address);
		
		if ( title.equals("CHECK-IN CONFIRMATION")) {
			textViewReturnTitle.setText("ACTUAL RETURNED:");
		}
		
		setManualResponce(checkOutData);
	}

	private void setManualResponce(String checkOutData) {
		try {
			JSONObject json = new JSONObject(checkOutData);
			id = json.getString("id");
			String asset_id_string = json.getString("asset_id");
			String description = json.getString("description");
			String serial_number = json.getString("serial_number");
			// String department = json.getString("department");
			String address = json.getString("address");
			des_id.setText(description);
			asset_serial.setText(serial_number);
			asset_id.setText(asset_id_string);

			//add_id.setText(address);
			// departmentName.setText(department);
			// editTextReTurn.setText(json.getString("date_time_out"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void onClickClose(View v) {

		ticketReturn = editTextReTurn.getText().toString();

		if (ticketReturn.equals("")) {
			Toast.makeText(mContext, "Please fill all fields",
					Toast.LENGTH_LONG).show();
		} else {
			new CheckOutAsyncTask().execute();
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

	public class CheckOutAsyncTask extends
			AsyncTask<JSONObject, JSONObject, JSONObject> {

		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(JSONObject... strings) {
			JSONObject jsonObject = new JSONObject();
			try {

				JSONParser jsonParser = new JSONParser();

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs
						.add(new BasicNameValuePair("ticket_id", ticketId));
				nameValuePairs.add(new BasicNameValuePair(
						"schedule_to_be_return", ticketReturn));
				nameValuePairs.add(new BasicNameValuePair("employee",
						employeeName));

				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/confirm", "POST",
						nameValuePairs);
				Log.v("employee link ", "employee link \t " + jsonObject);

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
			// JSONObject checkOutObject = new JSONObject();
			try {
				data = json.getJSONObject("data");

				// data = checkOutObject.getJSONObject(0);
				String message = data.getString("msg");
				if (!(message.equals(""))) {

					showAlertDialog("Ticket Closed", message);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		private void showAlertDialog(String title, String message) {
			new AlertDialog.Builder(mContext)
					.setIcon(R.drawable.info_icon)
					.setTitle(title)
					.setMessage(message)
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									ScAdminCheckOutConfirmationScreen.this
											.finish();

								}
							}).show();
		}
	}

}
