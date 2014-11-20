/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.scanchex.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.EntryItem;
import com.scanchex.bo.Item;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCCameraPeviewScreen extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	private Button scanButton;
	private ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	private String deviceId;
	private String phoneModel;
	private String androidVersion;
	private String qr_code = "";
	private String description = "";
	private String checkpoint_id = "";
	private String time = "";

	public static int position = -1;
	public boolean scanTicket = false;
	public static int count = 0;

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_camerapreview_screen);
		LinearLayout layout = (LinearLayout) findViewById(R.id.cameraContainer);
		layout.setBackgroundColor((SCPreferences
				.getColor(SCCameraPeviewScreen.this)));

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		// Device model
		phoneModel = android.os.Build.MODEL;
		// Android version
		androidVersion = android.os.Build.VERSION.RELEASE;

		if (getIntent().hasExtra("qr_code")) {
			qr_code = getIntent().getStringExtra("qr_code");
			description = getIntent().getStringExtra("description");
			checkpoint_id = getIntent().getStringExtra("checkpoint_id");
			time= getIntent().getStringExtra("time");
			position = getIntent().getIntExtra("position", -1);
		}

		autoFocusHandler = new Handler();
		mCamera = getCameraInstance();

		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);

		scanButton = (Button) findViewById(R.id.ScanButton);

		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (barcodeScanned) {
					barcodeScanned = false;
					mCamera.setPreviewCallback(previewCb);
					mCamera.startPreview();
					previewing = true;
					mCamera.autoFocus(autoFocusCB);
				}
			}
		});

	}

	public void onPause() {
		super.onPause();
		releaseCamera();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					AssetsTicketsInfo tInfo = Resources.getResources()
							.getAssetTicketInfo();
					String assetCode = tInfo.assetCode.trim();
					String isIdcard = tInfo.allowIdCardScan;

					if (Resources.getResources().isCheckPointScan()) {
						if (qr_code.equals(sym.getData().trim())) {
							scanTicket = true;
							new ScanCheckPointTask().execute(CONSTANTS.BASE_URL);
							break;
						} else {

							final ScCheckPoints check = new ScCheckPoints();
							check.isTrue = false;
							check.checkpoint_id = checkpoint_id;
							check.description = description;
							check.qr_code = qr_code;

							ArrayList<Item> aa = new ArrayList<Item>();
							aa = Resources.getResources().getItemList();
							aa.set(position, new EntryItem("", "",
									description, "", "", time, false,
									checkpoint_id, qr_code, false));
							Resources.getResources().setItemList(aa);
							

							Resources.getResources().setCheckPointScan(false);

							showAlert();

						}
					} else

					{
						
						scanTicket = true;
						

						if (isIdcard.equalsIgnoreCase("Yes")) {
							if (assetCode.equals(sym.getData().trim())
									|| SCPreferences.getEmployeeCard(
											SCCameraPeviewScreen.this).equals(
											sym.getData().trim())) {
								new ScanTicketTask()
										.execute(CONSTANTS.BASE_URL);
								break;
							} else {
								showAlert();
							}
						} else {
							Log.d("<<<<<<Asset Code>>>>> " + assetCode,
									"<<<<Bar Code>>> " + sym.getData());
							if (assetCode.equals(sym.getData().trim())) {
								new ScanTicketTask()
										.execute(CONSTANTS.BASE_URL);
								break;
							} else {
								showAlert();

							}
						}
					}

					barcodeScanned = true;
					break;
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private void showAlert() {
		new AlertDialog.Builder(SCCameraPeviewScreen.this)
				.setIcon(R.drawable.info_icon).setTitle("Error")
				.setMessage("You have scaned a wrong ticket.")
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	private class ScanTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String historyId = "";
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams
						.add(new BasicNameValuePair("asset_id", tInfo.assetId));
				listParams.add(new BasicNameValuePair("latitude",
						tInfo.assetlatitude));
				listParams.add(new BasicNameValuePair("longitude",
						tInfo.assetLongitude));

				listParams
						.add(new BasicNameValuePair("handset_make", "Android"));
				listParams.add(new BasicNameValuePair("os", androidVersion));// current
																				// version
				listParams.add(new BasicNameValuePair("model_no", phoneModel));// device
																				// name
				listParams
						.add(new BasicNameValuePair("serial_number", deviceId));

				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCCameraPeviewScreen.this)));
				listParams.add(new BasicNameValuePair("username", SCPreferences
						.getPreferences()
						.getUserName(SCCameraPeviewScreen.this)));
				listParams.add(new BasicNameValuePair("action", "scan_ticket"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				historyId = obj.getString("history_id");
				Resources.getResources().setTicketHistoryId(historyId);

				

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
			SCCameraPeviewScreen.this.finish();
			Resources.getResources().setFirstScanDone(true);
			Resources.getResources().setCloseTicket(true);
			if(!(historyId.equals(""))){
				if(scanTicket == true){
				int scanCount = Resources.getResources().getTotalScans();				
				scanCount = scanCount + 1;
				Resources.getResources().setTotalScans(scanCount);
				
				}
				Resources.getResources().setCorrectTicket(true);
			}else{
				Resources.getResources().setCorrectTicket(false);
			}
			// if(countCheck == SCAssetsFragment.countCheckPoint){
			// Resources.getResources().setCheckPointDone(true);
			// }

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCCameraPeviewScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Ticket Scan");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	
	private class ScanCheckPointTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		String historyId = "";
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams
						.add(new BasicNameValuePair("asset_id", tInfo.assetId));
				listParams
				.add(new BasicNameValuePair("checkpoint_id", checkpoint_id));

				listParams.add(new BasicNameValuePair("latitude",
						tInfo.assetlatitude));
				listParams.add(new BasicNameValuePair("longitude",
						tInfo.assetLongitude));

		
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								SCCameraPeviewScreen.this)));
				listParams.add(new BasicNameValuePair("username", SCPreferences
						.getPreferences()
						.getUserName(SCCameraPeviewScreen.this)));
				listParams.add(new BasicNameValuePair("action", "scan_checkpoint"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				//historyId = obj.getString("history_id");
				//Resources.getResources().setTicketHistoryId(historyId);

				

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
			
		//	 if(!(historyId.equals(""))){
					if(scanTicket == true){
					int scanCount = Resources.getResources().getTotalCheckPointScans();				
					scanCount = scanCount + 1;
					Resources.getResources().setTotalCheckPointScans(scanCount);
					
					}
					Resources.getResources().setCorrectTicket(true);
//				}else{
//					Resources.getResources().setCorrectTicket(false);
//				}
			SCCameraPeviewScreen.this.finish();
			 Resources.getResources().setCheckPointDone(true);
			
		
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCCameraPeviewScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Check Point  Scan");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
}
