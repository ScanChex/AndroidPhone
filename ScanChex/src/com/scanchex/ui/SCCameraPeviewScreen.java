/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
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
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;


public class SCCameraPeviewScreen extends Activity{
    
	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    


    static {
        System.loadLibrary("iconv");
    } 
    

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
                    	AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
                    	String assetCode = tInfo.assetCode.trim();
                    	
                    	Log.i("<<<<<<Asset Code>>>>> "+assetCode, "<<<<Bar Code>>> "+sym.getData());
                    	if(assetCode.equals(sym.getData().trim())){
                    		new ScanTicketTask().execute(CONSTANTS.BASE_URL);
                    		break;
                    	}else{
                    		showAlert();
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
        
        private void showAlert(){
        	new AlertDialog.Builder(SCCameraPeviewScreen.this)
        	.setIcon(R.drawable.info_icon)
        	.setTitle("Error")
        	.setMessage("You have scaned a wrong ticket.")
        	.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).show();
        }
        
        
        private class ScanTicketTask extends AsyncTask<String, Integer, Boolean> {

    		private ProgressDialog pdialog;
    		String response;
    		String historyId;
    		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
    		@Override
    		protected Boolean doInBackground(String... params) {
    			try {
    				
    				Log.i("RESET PASS URL", "<><>" + params[0]);
    				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
    				listParams.add(new BasicNameValuePair("ticket_id", tInfo.ticketTableId));
    				listParams.add(new BasicNameValuePair("asset_id", tInfo.assetId));
    				listParams.add(new BasicNameValuePair("latitude", tInfo.assetlatitude));
    				listParams.add(new BasicNameValuePair("longitude", tInfo.assetLongitude));
    				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(SCCameraPeviewScreen.this)));
    				listParams.add(new BasicNameValuePair("username", SCPreferences.getPreferences().getUserName(SCCameraPeviewScreen.this)));
    				listParams.add(new BasicNameValuePair("action", "scan_ticket"));
    				response = new HttpWorker().getData(params[0], listParams);
    				response = response.substring(3);
    				Log.i("RESPONSE", "Login Resp>> " + response);
    				JSONObject obj = new JSONObject(response);
    				historyId = obj.getString("history_id");
    				Resources.getResources().setTicketHistoryId(historyId);
    				
    				int scanCount = Resources.getResources().getTotalScans();
    				scanCount = scanCount+1;
    				Resources.getResources().setTotalScans(scanCount);
    				
    				
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
    			if(!Resources.getResources().isForDoubleScan()){
	    			Intent details = new Intent(SCCameraPeviewScreen.this, SCDetailsFragmentScreen.class);
	    			startActivity(details);
    			}
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

}
