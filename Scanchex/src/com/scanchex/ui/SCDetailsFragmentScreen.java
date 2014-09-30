package com.scanchex.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.GPSTracker;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCDetailsFragmentScreen extends SherlockFragmentActivity {

	AssetsTicketsInfo tInfo;
	LinearLayout layout;
	ImageView ticketStatusIcon;
	ImageView image;
	ImageView notesstatusicon;
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
	Button closeTocketButton;
	int scanCount = 0;
	int scanArraySize = 0;
	Button assetTab, questionTab, documentTab, historyTab, noteTab;
	GPSTracker gps;
	public static String historyid = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.sc_detailsfragment_screen);
		RelativeLayout layoutBg = (RelativeLayout) findViewById(R.id.detailFragmentScreen);
		layoutBg.setBackgroundColor(SCPreferences
				.getColor(SCDetailsFragmentScreen.this));
		gps = new GPSTracker(this);
		closeTocketButton = (Button) findViewById(R.id.closeTocketButton);
		layout = (LinearLayout) findViewById(R.id.tickets_layout);
		image = (ImageView) findViewById(R.id.image_view);
		notesstatusicon = (ImageView) findViewById(R.id.notes_status_icon);
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
		assetTab = (Button) findViewById(R.id.assetsId);
		questionTab = (Button) findViewById(R.id.questionsId);
		documentTab = (Button) findViewById(R.id.documentsId);
		historyTab = (Button) findViewById(R.id.historyId);
		noteTab = (Button) findViewById(R.id.notesId);

		initColor();
		assetTab.setBackgroundResource(R.drawable.round_corner_left_selected_tab);
		tInfo = Resources.getResources().getAssetTicketInfo();
		
		

		// **************************DateTime**************************//

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
		formatter.setLenient(false);

		Date curDate = new Date();
		long curMillis = curDate.getTime();
		String curTime = formatter.format(curDate);
		try {

			String oldTime = tInfo.ticketTimeStamp;
			String tolerance = tInfo.assetTolerance;

			long toleranceSec = 0;

			if (tolerance.contains("\\d+"))
				toleranceSec = Long.parseLong(tolerance);

			long toleranceMili = TimeUnit.SECONDS.toMillis(toleranceSec);
			long newTimeInMili;

			Date oldDate = formatter.parse(oldTime);
			long oldMillis = oldDate.getTime();
			newTimeInMili = oldMillis - toleranceMili;
			Resources.getResources().setTimeToStartTicket(newTimeInMili);

		} catch (ParseException e) {
			e.printStackTrace();
		}

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

		try {
			Picasso.with(SCDetailsFragmentScreen.this) //
					.load(tInfo.thumbPhotoUrl) //
					.placeholder(R.drawable.photo_not_available) //
					.error(R.drawable.photo_not_available) //
					.into(image);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if( !(tInfo.notes.equalsIgnoreCase("")
				|| tInfo.notes.equalsIgnoreCase(null))) {
			noteTab.setTextColor(this.getResources().getColor(R.color.red));
			
		}
		
		//for debugging - remove for production
		//Resources.getResources().setFirstScanDone(true);
		
		//Update close ticket button name based on ticket status preview/update
		updateName();
		
		clientName.setText(tInfo.assetClientName);
		phoneNumber.setText(tInfo.assetPhone);
		address1.setText(tInfo.addressStreet);
		address2.setText(tInfo.addressCity + ", " + tInfo.addressState);
		historyid = tInfo.ticketId;
		ticketId.setText(tInfo.ticketId);
		assetId.setText(tInfo.assetUNAssetId);
		assetName.setText(tInfo.assetDescription);
		ticketStartDate.setText(tInfo.ticketStartDate);
		ticketStartTime.setText(tInfo.ticketStartTime);

		Resources.getResources().setCurrentContext(this);
		SCAssetsFragment subregionFragment = new SCAssetsFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
		fragmentTransaction.commit();
	}

	public void onTicketMapClick(View v) {
		Intent i = new Intent(this, SCViewMapDirectionsScreen.class);
		startActivity(i);
		finish();
	}

	public void onTicketDetailsClick(View view) {

		Intent thisIntent = getIntent();
		startActivity(thisIntent);
		finish();

	}

	public void onTicketDirectionClick(View v) {
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?saddr="
						+ gps.getLatitude() + "," + gps.getLongitude()
						+ "&daddr=" + Double.parseDouble(tInfo.assetlatitude)
						+ "," + Double.parseDouble(tInfo.assetLongitude)));
		startActivity(intent);
	}

	public void onCloseTicketClick(View view) {
		if (Resources.getResources().isFirstScanDone()) {
			showAlert();
			
		} else {
			finish();
		}
	}

	public void onAssetsClick(View view) {
		initColor();
		assetTab.setBackgroundColor(getResources().getColor(
				R.color.selectedtTab));

		SCAssetsFragment subregionFragment = new SCAssetsFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
		fragmentTransaction.commit();

	}

	public void onQuestionsClick(View view) {
		initColor();
		questionTab.setBackgroundColor(getResources().getColor(
				R.color.selectedtTab));
		SCQuestionsFragment subregionFragment = new SCQuestionsFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
		fragmentTransaction.commit();
	}

	public void onDocumentsClick(View view) {
		initColor();
		documentTab.setBackgroundColor(getResources().getColor(
				R.color.selectedtTab));
		SCDocumentsFragment subregionFragment = new SCDocumentsFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
		fragmentTransaction.commit();
	}

	public void onHistoryClick(View view) {
		initColor();
		historyTab.setBackgroundColor(getResources().getColor(
				R.color.selectedtTab));
		SCHistoryFragment subregionFragment = new SCHistoryFragment();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, subregionFragment);
		fragmentTransaction.commit();
	}

	public void onNotesClick(View v) {
		initColor();
		noteTab.setBackgroundResource(R.drawable.round_corner_right_selected_tab);
		noteTab.setText("Notes");

		showMessgaeDialog();
	}

	public void initColor() {

		assetTab.setBackgroundResource(R.drawable.round_corner_left_tab);
		questionTab.setBackgroundColor(getResources().getColor(R.color.black));
		documentTab.setBackgroundColor(getResources().getColor(R.color.black));
		historyTab.setBackgroundColor(getResources().getColor(R.color.black));
		noteTab.setBackgroundResource(R.drawable.round_corner_right_tab);
		
	}

	public void showPushNotificationAlert(String message) {

		new AlertDialog.Builder(this)
				.setIcon(R.drawable.message_info_icon)
				.setTitle("New Notification Arrived")
				.setMessage(message + "\n\n Do you want to see other messages?")
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Intent ticketView = new Intent(
										SCDetailsFragmentScreen.this,
										SCDetailsFragmentScreen.class);
								startActivity(ticketView);
							}
						}).show();

	}

	private void showAlert() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.message_info_icon)
				.setTitle("Info")
				.setMessage("Are you sure, you want to close a ticket?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								scanArraySize = Resources.getResources()
										.getCheckPointModelArray().size();
								int count = Resources.getResources()
										.getTotalScans();
								AssetsTicketsInfo tInfo = Resources
										.getResources().getAssetTicketInfo();
								if (Resources.getResources()
										.isQuestionsSubmitted()
										|| Resources.getResources()
												.getQuestionsData() == null) {
									if (count == scanArraySize
											&& Resources.getResources()
													.isCorrectTicket()) {
										if (tInfo.ticketNumberOfScans == Resources
												.getResources().getTotalScans()) {

											new CloseTicketTask()
													.execute(CONSTANTS.BASE_URL);
										} else {
											showOptionAlert();
										}
									} else {
										showInfoAlert("Info",
												"Please scan all tickets first");
									}
								} else {
									showInfoAlert("Info",
											"Please submit answers of all question first");
								}
							}
						}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (Resources.getResources().isFirstScanDone()) {
				if (Resources.getResources().isCloseTicket()) {
					showAlert();
				} else {
					finish();
				}
			} else {
				finish();
			}

			// Intent in = new
			// Intent(SCDetailsFragmentScreen.this,ScPaynowScreen.class);
			// in.putExtra("ticketId", tInfo.ticketId);
			// startActivity(in);
			//

			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showOptionAlert() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.message_info_icon)
				.setTitle("Info")
				.setMessage(
						"Double scan required before close ticket. Do you want another scan?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Resources.getResources().setForDoubleScan(true);
								Intent i = new Intent(
										SCDetailsFragmentScreen.this,
										SCCameraPeviewScreen.class);
								startActivity(i);
							}
						}).show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (Resources.getResources().isForDoubleScan()) {
			Resources.getResources().setForDoubleScan(false);

			new CloseTicketTask().execute(CONSTANTS.BASE_URL);

		}
	}

	public void showInfoAlert(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setIcon(R.drawable.info_icon)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

	}

	// //////////////////Close Ticket//////////////////
	private class CloseTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {

			try {

				Log.i("Close Ticket URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams.add(new BasicNameValuePair("history_id", Resources
						.getResources().getTicketHistoryId()));

				listParams.add(new BasicNameValuePair("employee", SCPreferences
						.getPreferences().getUserName(
								SCDetailsFragmentScreen.this)));

				listParams.add(new BasicNameValuePair("master_id",
						SCPreferences.getPreferences().getUserMasterKey(
								SCDetailsFragmentScreen.this)));

				listParams.add(new BasicNameValuePair("action",
						"close_scan_ticket"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Close Ticket Resp>> " + response);
				JSONObject obj = new JSONObject(response);
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
				Intent in = new Intent(SCDetailsFragmentScreen.this,
						ScPaynowScreen.class);
				in.putExtra("ticketId", tInfo.ticketId);
				startActivity(in);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCDetailsFragmentScreen.this);
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Closing Ticket");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	private void showMessgaeDialog() {
		final Dialog dialog = new Dialog(SCDetailsFragmentScreen.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.sc_popup_notes_view_dialog);

		TextView messageText = (TextView) dialog
				.findViewById(R.id.editTextMessage);
		if (tInfo.notes.equalsIgnoreCase("")
				|| tInfo.notes.equalsIgnoreCase(null)) {
			messageText.setText("---");
			messageText.setBackgroundResource(R.drawable.messgae_dialog_corner);
		} else {
			messageText.setText(tInfo.notes);
			noteTab.setTextColor(this.getResources().getColor(R.color.white));
			notesstatusicon.setVisibility(View.GONE);
		
		}
		Button cancel = (Button) dialog.findViewById(R.id.buttonCancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();
	}

	public void updateName() {
		if (Resources.getResources().isFirstScanDone()) {
			closeTocketButton.setText("Close Ticket");
		} else {
			closeTocketButton.setText("Back");
		}
	}

}
