package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.scanchex.adapters.SCServiceComponentAdapter;
import com.scanchex.bo.SCServiceComponentInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.SCPreferences;



public class SCServiceComponentScreen extends ListActivity{
	
	private SCServiceComponentAdapter adapter;
	private String serviceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_servicecomponent_screen);
		adapter = new SCServiceComponentAdapter(this);
		setListAdapter(adapter);
		serviceId = getIntent().getExtras().getString("SERVICE_ID");
		new ServiceComponentTask().execute(CONSTANTS.BASE_URL);
	}
	
	////////////////////ASYNC TASK//////////////////
	 private class ServiceComponentTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		Vector<SCServiceComponentInfo> vector;
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(SCServiceComponentScreen.this)));
				listParams.add(new BasicNameValuePair("service_id", serviceId));
				listParams.add(new BasicNameValuePair("action", "get_service_components"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				JSONArray jArr = obj.getJSONArray("components");
				vector = new Vector<SCServiceComponentInfo>();
				if(jArr!=null && jArr.length()>0){
					for(int i=0; i<jArr.length(); i++){
						SCServiceComponentInfo extraInfo = new SCServiceComponentInfo();
						JSONObject jObj = jArr.getJSONObject(i);
						extraInfo.itemId = jObj.getString("itemid");
						extraInfo.compId = jObj.getString("comp_id");
						extraInfo.compDescription = jObj.getString("comp_desc");
						extraInfo.compCost = jObj.getString("comp_cost");
						extraInfo.compNotes = jObj.getString("notes");
						extraInfo.compQuantity = jObj.getString("qty");
						vector.add(extraInfo);
						adapter.setComponentInfo(vector);
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
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(SCServiceComponentScreen.this);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Service Component Info");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}	
	
	

}
