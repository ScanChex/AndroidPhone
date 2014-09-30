package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ScPaymentScreen extends Activity {

	String ticketIdString, imageFIle, comment, AdditionalComment, contentType,
			payment_type;
	TextView textViewAsset, textViewTicket, textViewDate, textViewAmountDue;

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
		setContentView(R.layout.sc_paynow_second_screen);
		textViewAsset = (TextView) findViewById(R.id.textViewAsset);
		textViewTicket = (TextView) findViewById(R.id.textViewTicket);
		textViewDate = (TextView) findViewById(R.id.textViewDate);
		textViewAmountDue = (TextView) findViewById(R.id.textViewAmountDue);

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
		address2.setText(tInfo.addressCity + ", " + tInfo.addressState);

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

		if (getIntent().hasExtra("ticketId")
				&& getIntent().hasExtra("imageFIle")
				&& getIntent().hasExtra("comment")) {

			ticketIdString = tInfo.ticketId;
			imageFIle = getIntent().getStringExtra("imageFIle");
			comment = getIntent().getStringExtra("comment");

			contentType = "image/jpeg";
			textViewAsset.setText(tInfo.assetId);
			textViewTicket.setText(tInfo.ticketId);
			textViewDate.setText(tInfo.ticketStartDate);
			textViewAmountDue.setText("99$");

			if (getIntent().hasExtra("AdditionalComment")) {
				AdditionalComment = getIntent().getStringExtra(
						"AdditionalComment");
			}

		}
	}

	public void onCLickChecque(View v) {
		payment_type = "cheque";
		new UploadTask().execute(CONSTANTS.BASE_URL);
	}

	public void onClickCash(View v) {
		payment_type = "cash";
		new UploadTask().execute(CONSTANTS.BASE_URL);
	}

	public void onClickCredit(View v) {
		payment_type = "credit card";
		new UploadTask().execute(CONSTANTS.BASE_URL);
	}

	private class UploadTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog pdialog;
		private String serverResp;

		private String status;
		private String message;

		@Override
		protected Boolean doInBackground(String... path) {

			String url = path[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			Log.i("URL", "<><><>" + url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("master_key", ""
					+ SCPreferences.getPreferences().getUserMasterKey(
							ScPaymentScreen.this)));
			nameValuePairs.add(new BasicNameValuePair("ticket_id",
					ticketIdString));
			nameValuePairs.add(new BasicNameValuePair("upload_array", ""));
			nameValuePairs.add(new BasicNameValuePair("comments", comment));
			nameValuePairs.add(new BasicNameValuePair("addiotnal_comments",
					AdditionalComment));
			nameValuePairs.add(new BasicNameValuePair("payment_type",
					payment_type));
			nameValuePairs.add(new BasicNameValuePair("action",
					"save_signature"));
			nameValuePairs.add(new BasicNameValuePair("file", imageFIle));

			nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"
					+ new Date().getTime()));

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
										.getValue()), contentType));
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

				if (serverResp.contains("error")) {
					status = obj.getString("error");
					return false;
				} else {
					status = obj.getString("status");
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
			Log.i("DONE DONE", "DONE DONE");
			pdialog.dismiss();
			pdialog = null;

			if (result) {
				File file = new File(SCImageTakenScreen.selectedImagePath);
				boolean deleted = file.delete();
				Toast.makeText(ScPaymentScreen.this, status, Toast.LENGTH_SHORT)
						.show();

				Intent ticketView = new Intent(ScPaymentScreen.this,
						SCMainMenuScreen.class);
				startActivity(ticketView);
				finish();
			} else {
				Toast.makeText(ScPaymentScreen.this, status, Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(ScPaymentScreen.this);
			pdialog.setCancelable(false);
			pdialog.setTitle("Uploading");
			pdialog.setMessage("Please Wait...");
			pdialog.show();

		}

	}

}
