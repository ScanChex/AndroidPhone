package com.scanchex.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.scanchex.bo.SCAdminAssetDetailsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;


public class SCAdminCameraPeviewScreen extends Activity{
    
	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    
    private String assetCode;

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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);


        scanButton = (Button)findViewById(R.id.ScanButton);

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
        
        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
        Criteria criteria = new Criteria();
        String provider = locManager.getBestProvider(criteria, false);
        Location location = locManager.getLastKnownLocation(provider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            strLatitude = ""+latitude;
            strLongitude = ""+longitude;
            Log.i("LOCATION LAT>>"+latitude,"Longitute" +longitude);
        }else{
        	strLatitude = "Not Found";
        	strLongitude = "Not Found";
        }  
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
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
                    	
                    	Log.i("<<<<<<Asset Code>>>>> ", "<<<<Bar Code>>> "+sym.getData());
                    	assetCode = sym.getData().trim();
//                    	assetCode = "uestmda3ltawmditmdawms0wmdax";

                    	new ScanTicketTask().execute(CONSTANTS.BASE_URL);
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
    		if(keyCode == KeyEvent.KEYCODE_BACK){
    			SCAdminTapToScanScreen.isFromAssetDetail = false;
    		}
    		return super.onKeyDown(keyCode, event);
    	}
        
        
        private class ScanTicketTask extends AsyncTask<String, Integer, Boolean> {

    		private ProgressDialog pdialog;
    		String response;
    		String errMsg;
    		@Override
    		protected Boolean doInBackground(String... params) {
    			try {
    				
    				Log.i("Admin QR code URL", "<><>" + params[0]);
    				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
    				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(SCAdminCameraPeviewScreen.this)));
    				listParams.add(new BasicNameValuePair("asset_id", assetCode));
    				listParams.add(new BasicNameValuePair("action", "get_asset_info"));
    				response = new HttpWorker().getData(params[0], listParams);
    				response = response.substring(3);
    				Log.i("RESPONSE", "Admin asset code Resp>> " + response);
    				if(!response.contains("error")){
	    				JSONObject obj = new JSONObject(response);
	    				SCAdminAssetDetailsInfo assetInfo = new SCAdminAssetDetailsInfo();
	    				assetInfo.assetId = obj.getString("asset_id");
	    				assetInfo.ltitude = obj.getString("ltitude");
	    				assetInfo.lngitude = obj.getString("lngitude");
	    				assetInfo.assetDescription = obj.getString("description");
	    				assetInfo.assetSerialNum = obj.getString("serial_number");
	    				assetInfo.assetLastScan = obj.getString("last_scanned_date");
	    				assetInfo.assetEmployee = obj.getString("emp_id");
	    				
	    				assetInfo.street = obj.getString("street");
	    				assetInfo.city = obj.getString("city");
	    				assetInfo.state = obj.getString("state");
	    				assetInfo.postalCode = obj.getString("postal_code");
	    				assetInfo.country = obj.getString("country");
	    				assetInfo.assetEmployeeFullName = obj.getString("full_name");
	    				assetInfo.assetImage = downloadFile(obj.getString("asset_photo"));
	    				
	    				assetInfo.assetScanningCode = assetCode;
	    				
	    				Resources.getResources().setAssetDetailInfo(assetInfo);
	    				return true;
    				}else{
    					JSONObject obj = new JSONObject(response);
    					errMsg = obj.getString("error");
    					return false;
    				}
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
    			if(result){
	    			String message = "Previous Latitude: "+Resources.getResources().getAssetDetailInfo().ltitude+"\n"
	    					+"Previous Longitude: "+Resources.getResources().getAssetDetailInfo().lngitude+"\n\n"
	    					+"Current Latitude: "+strLatitude+"\n"
	    					+"Curren Longitude: "+strLongitude+"\n";
	    			showOptionAlert("Info", message);
    			}else{
    				Toast.makeText(SCAdminCameraPeviewScreen.this, ""+errMsg, Toast.LENGTH_LONG).show();
    			}
    		}

    		@Override
    		protected void onPreExecute() {
    			super.onPreExecute();
    			pdialog = new ProgressDialog(SCAdminCameraPeviewScreen.this);
    			pdialog.setIcon(R.drawable.info_icon);
    			pdialog.setTitle("Asset Scan");
    			pdialog.setMessage("Working...");
    			pdialog.show();
    		}
    	}
        
        private Bitmap downloadFile(String fileUrl) {
			
   		 if(fileUrl.equals("null"))return null;
   			Bitmap bmImg;
   			URL myFileUrl = null;
   			try {
   				Log.i("File URL", "<>" + fileUrl);
   				myFileUrl = new URL(fileUrl);
   			} catch (MalformedURLException e) {
   				e.printStackTrace();
   			}
   			try {
   				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
   				conn.setDoInput(true);
   				conn.connect();
   				InputStream is = conn.getInputStream();
   				bmImg = BitmapFactory.decodeStream(is);
   				return bmImg;
   			} catch (IOException e) {
   				e.printStackTrace();
   				return null;
   			}
   		}
        
        
        
        private void showOptionAlert(String title, String message){
        	
        	new AlertDialog.Builder(this)
        	.setIcon(R.drawable.info_icon)
        	.setTitle(title)
        	.setMessage(message)
        	.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SCAdminCameraPeviewScreen.this.finish();
					Intent details = new Intent(SCAdminCameraPeviewScreen.this, SCAdminShowAssetDetailsScreen.class);
	    			startActivity(details);
					
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SCAdminCameraPeviewScreen.this.finish();
					
				}
			}).show();
        }

}
