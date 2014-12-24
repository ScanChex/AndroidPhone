package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

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
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.JSONParser;
import com.scanchex.utils.SCPreferences;

public class SCAdminScanScreen extends BaseActivity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	private Button scanButton;
	private ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	private String assetCode;
	String checkOut = "";
	Context mContext;
	JSONObject allDataApi;
	String allData;
	static {
		System.loadLibrary("iconv");
	}

	private LocationManager locManager;
	private double longitude;
	private double latitude;

	private String strLongitude;
	private String strLatitude;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sc_camerapreview_screen);
		// LinearLayout layout = (LinearLayout)
		// findViewById(R.id.cameraContainer);
		// layout.setBackgroundColor((SCPreferences
		// .getColor(SCAdminScanScreen.this)));
		mContext = this;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		checkOut = getIntent().getStringExtra("scanCheck");
		Log.v("scanvlaue", "scanvlaue \t" + checkOut);

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

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locManager.getBestProvider(criteria, false);
		Location location = locManager.getLastKnownLocation(provider);
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			strLatitude = "" + latitude;
			strLongitude = "" + longitude;
			Log.i("LOCATION LAT>>" + latitude, "Longitute" + longitude);
		} else {
			strLatitude = "Not Found";
			strLongitude = "Not Found";
		}
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

					Log.i("<<<<<<Asset Code>>>>> ",
							"<<<<Bar Code>>> " + sym.getData());
					assetCode = sym.getData().trim();
					// assetCode = "su4tmtc1mtawmi0wmdaxltawmdetmdawnw==";
					// Log.v("asset code in previewframe",
					// "asset code in previewframe" + assetCode);
					new AllDataApiAsyncTask().execute();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SCAdminTapToScanScreen.isFromAssetDetail = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public class AllDataApiAsyncTask extends
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

				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("master_key", SCPreferences
						.getPreferences().getUserMasterKey(mContext)));

				jsonObject = jsonParser.makeHttpRequest(
						CONSTANTS.BASE_URL_ADMIN + "asset/all", "POST", params);
				// Log.v("allDataasync", "allDataasync" + jsonObject);
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

			Log.d("json onpost execute",
					"json onpost execute \t" + json.toString());
			Log.d("json onpost", "jsononpost \t" + jsonResponce.toString());
			String msg = "";
			try {
				allDataApi = json.getJSONObject("data");
				allData = allDataApi.toString();
				JSONObject getObject = new JSONObject();
				JSONObject clientObject = new JSONObject();
				JSONArray assetsArray = new JSONArray();
				JSONArray clientsArray = new JSONArray();
				Log.d("allDataApi", "allDataApi \t" + allDataApi);
				// Log.d("getObject", "getObject" + getObject);
				Log.d("clientObject", "clientObject" + clientObject);

				// Log.d("json", "json" + json.toString());

				assetsArray = allDataApi.getJSONArray("assets");
				clientsArray = allDataApi.getJSONArray("clients");
				// Log.d(" assetsArray", " assetsArray" + assetsArray);
				// Log.d(" clientsArray", "clientsArray" + clientsArray);
				// Set Asset Array
				String id = "", name = "";
				if (assetsArray.length() > 0) {
					// Log.d(" assetsArray", " assetsArray" +
					// assetsArray.length());

					for (int asset = 0; asset < assetsArray.length(); asset++) {

						getObject = assetsArray.getJSONObject(asset);
						String status = getObject.getString("asset_status");
						// Log.v("status ", "status " + status);
						{
							String client_id = getObject.getString("client_id");
							// Log.v("client_id ", "client_id " + client_id);
							if (assetCode.equalsIgnoreCase(getObject
									.getString("asset_code"))) {
								if (!(status.equals("checked_out"))) {

								for (int k = 0; k < clientsArray.length(); k++) {
									clientObject = clientsArray
											.getJSONObject(k);
									String getId = clientObject.getString("id");
									if (client_id.equalsIgnoreCase(getId)) {
										id = clientObject.getString("id");
										name = clientObject.getString("name");
									}

								}
								Intent manualLook = new Intent(mContext,
										ScAdminScanLookUp.class);
								manualLook.putExtra("assetObject",
										getObject.toString());
								manualLook.putExtra("id", id);
								manualLook.putExtra("name", name);

									manualLook.putExtra("alldatapi", allData);
									startActivity(manualLook);
									// Log.v("\n assetObject",
									// getObject.toString()
									// + "\n" + "\n id " + id + "\n name"
									// + name);
									// Log.v("manualLook", "manualLook" +
									// manualLook);
									finish();
								} else {
									showOptionAlert("Info",
											"This asset is already checkout");

								}
							}
						}

						// else {
						// showOptionAlert("Info",
						// "This Ticket is already checkout");
						// }
					}

				}

			} catch (JSONException e) {
				e.printStackTrace();

			}

		}
	}

	private void showOptionAlert(String title, String message) {

		new AlertDialog.Builder(this).setIcon(R.drawable.info_icon)
				.setTitle(title).setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SCAdminScanScreen.this.finish();
					}
				}).show();
	}

	ProgressDialog pdialog;

	public void showProgressDialog() {
		pdialog = new ProgressDialog(SCAdminScanScreen.this);
		pdialog.setIcon(R.drawable.info_icon);
		pdialog.setTitle("Loading Manual Data");
		pdialog.setMessage("Working...");
		pdialog.show();
	}

	public void hideProgressDialog() {
		pdialog.dismiss();
	}
}
