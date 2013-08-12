package com.scanchex.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.bo.SCTicketExtraInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCAssetsFragment extends ListFragment{
	
	
	private TextView assetId;
	private TextView assetDescription;
	private TextView assetAddress;
	private ImageView assetImage;
	private AssetsTicketsInfo tInfo;
	private SCTicketExtraInfoAdapter adapter;
	
	public boolean isAfterUpdateServiceStatus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isAfterUpdateServiceStatus = false;
		tInfo = Resources.getResources().getAssetTicketInfo();
		adapter = new SCTicketExtraInfoAdapter(getActivity(), Resources.getResources().getTicketExtraData());
		setListAdapter(adapter);
		if(Resources.getResources().getTicketExtraData()==null){
			new TicketExtraTask().execute(CONSTANTS.BASE_URL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.sc_assets_fragment, container, false);
		 tInfo = Resources.getResources().getAssetTicketInfo();
		 assetId = (TextView)view.findViewById(R.id.asset_id);
		 assetDescription = (TextView)view.findViewById(R.id.des_id);
		 assetAddress = (TextView)view.findViewById(R.id.add_id);
		 assetImage = (ImageView)view.findViewById(R.id.asset_image_id);
		 assetId.setText(tInfo.assetUNAssetId);
		 assetDescription.setText(tInfo.assetDescription);
		 assetAddress.setText(tInfo.addressStreet+"\n"+tInfo.addressCity+","+tInfo.addressState+" "+tInfo.addressPostalCode);
		
		return view;
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		SCTicketExtraInfo extraInfo = (SCTicketExtraInfo)lv.getItemAtPosition(position);
		Log.i("TICKET EXTRA INFO", extraInfo.description);
		Intent intent = new Intent(getActivity(), SCServiceComponentScreen.class);
		intent.putExtra("SERVICE_ID", extraInfo.serviceId);
		startActivity(intent);
		
	}	
	
	/////////////ADAPTER/////////////////
	class SCTicketExtraInfoAdapter extends BaseAdapter implements OnClickListener{
		
		private LayoutInflater mInflater;
		private Context context;
		private Vector<SCTicketExtraInfo> vector;
		public SCTicketExtraInfoAdapter(Context context, Vector<SCTicketExtraInfo> vector) {
			mInflater = LayoutInflater.from(context);
			this.context = context;
			this.vector = vector;
		}

		@Override
		public int getCount(){
			if(vector!=null && this.vector.size()>0){
				return this.vector.size();
			}else{
				return 0;
			}
		}

		public Object getItem(int position) {
		
			return vector.get(position);
		}


		public long getItemId(int position) {
		
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if (convertView == null){
			convertView = mInflater.inflate(R.layout.sc_ticketextrainfo_row, null);
			holder = new ViewHolder();
			holder.textView1 = (TextView) convertView.findViewById(R.id.text1);
			holder.textView2 = (TextView) convertView.findViewById(R.id.text2);
			holder.ticketStatusImage = (ImageView) convertView.findViewById(R.id.image);
			
			
			convertView.setTag(holder);
		}else{
		
			holder = (ViewHolder) convertView.getTag();
		}
		
			SCTicketExtraInfo extraInfo = vector.get(position);
			holder.textView1.setText(extraInfo.model);
			holder.textView2.setText(extraInfo.description);
			if(extraInfo.status.equalsIgnoreCase("0")){
				holder.ticketStatusImage.setImageResource(R.drawable.ticket_status_unselect);
			}else{
				holder.ticketStatusImage.setImageResource(R.drawable.ticket_status_select);
			}
			holder.ticketStatusImage.setOnClickListener(this);
			holder.ticketStatusImage.setTag(position);
			return convertView;
		}
		

		class ViewHolder {
			
			RelativeLayout layout;
			TextView textView1;
			TextView textView2;
			ImageView ticketStatusImage;
			
		}
		
		public void setExtraInfo(Vector<SCTicketExtraInfo> vector){
			this.vector = vector;
		}

		@Override
		public void onClick(View view) {
		
			Log.i("<><><>","CLICKED "+view.getTag());
			
			SCTicketExtraInfo extraInfo = (SCTicketExtraInfo)this.vector.get(Integer.parseInt(view.getTag().toString()));
			if(extraInfo.status.equals("0")){
				new TicketUpdateStatusTask().execute(CONSTANTS.BASE_URL, extraInfo.ticketServiceId, "1");
			}else{
				new TicketUpdateStatusTask().execute(CONSTANTS.BASE_URL, extraInfo.ticketServiceId, "0");
			}
		}
	
	
	}

	////////////////////ASYNC TASK//////////////////
	 private class TicketExtraTask extends AsyncTask<String, Integer, Boolean> {

 		private ProgressDialog pdialog;
 		String response;
 		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
 		Vector<SCTicketExtraInfo> vector;
 		Bitmap assetPhoto;
 		@Override
 		protected Boolean doInBackground(String... params) {
 			try {
 				
 				Log.i("RESET PASS URL", "<><>" + params[0]);
 				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
 				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getActivity())));
 				listParams.add(new BasicNameValuePair("ticket_id", tInfo.ticketTableId));
 				listParams.add(new BasicNameValuePair("action", "get_ticket_services"));
 				response = new HttpWorker().getData(params[0], listParams);
 				response = response.substring(3);
 				assetPhoto = downloadFile(tInfo.assetPhotoUrl);
 				Log.i("RESPONSE", "Login Resp>> " + response);
 				JSONObject obj = new JSONObject(response);
 				JSONArray jArr = obj.getJSONArray("services");
 				vector = new Vector<SCTicketExtraInfo>();
 				if(jArr!=null && jArr.length()>0){
 					for(int i=0; i<jArr.length(); i++){
 						SCTicketExtraInfo extraInfo = new SCTicketExtraInfo();
 						JSONObject jObj = jArr.getJSONObject(i);
 						extraInfo.serviceId = jObj.getString("service_id");
 						extraInfo.model = jObj.getString("model");
 						extraInfo.description = jObj.getString("description");
 						extraInfo.status = jObj.getString("status");
 						extraInfo.ticketServiceId = jObj.getString("ticket_service_id");
 						vector.add(extraInfo);
 						Resources.getResources().setTicketExtraData(vector);
 						adapter.setExtraInfo(vector);
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
 			assetImage.setImageBitmap(assetPhoto);
 			if(!isAfterUpdateServiceStatus){
 				new QuestionsTask().execute(CONSTANTS.BASE_URL);
 			}
 		}

 		@Override
 		protected void onPreExecute() {
 			super.onPreExecute();
 			pdialog = new ProgressDialog(getActivity());
 			pdialog.setCancelable(false);
 			pdialog.setIcon(R.drawable.info_icon);
 			pdialog.setTitle("Ticket Extra Info");
 			pdialog.setMessage("Working...");
 			pdialog.show();
 		}
 	}
	 
	 private Bitmap downloadFile(String fileUrl) {
			
		 if(fileUrl.equals("null")||fileUrl.equalsIgnoreCase("N/A"))return null;
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
	 

	 
	 ////////////////////ASYNC TASK//////////////////
	 private class QuestionsTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCQuestionsInfo> vector;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getActivity())));
				listParams.add(new BasicNameValuePair("asset_id", tInfo.assetId));
				listParams.add(new BasicNameValuePair("action", "show_questions"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONArray mainArr = new JSONArray(response);
				if(mainArr!=null && mainArr.length()>0){
					vector = new Vector<SCQuestionsInfo>();
					for(int j=0; j<mainArr.length(); j++){
						SCQuestionsInfo qInfo = new SCQuestionsInfo();
						qInfo.questionId = mainArr.getJSONObject(j).getString("quest_id");
						qInfo.question = mainArr.getJSONObject(j).getString("question");
						qInfo.questionTypeId = mainArr.getJSONObject(j).getString("quest_type_id");
						qInfo.questionAnswer = mainArr.getJSONObject(j).getString("q_answer");
						Log.i("QUESTION> "+qInfo.question, "TYPE> "+qInfo.questionTypeId);
						if(mainArr.getJSONObject(j).has("answers")){
							JSONArray jArr = mainArr.getJSONObject(j).getJSONArray("answers");
							if(jArr!=null && jArr.length()>0){
								String [] answers = new String[jArr.length()];
								for(int i=0; i<jArr.length(); i++){
									String value = jArr.getString(i);
									answers[i] = value;
									qInfo.answers = answers;
								}
							}
						}
						vector.add(qInfo);
					}
					Resources.getResources().setQuestionsData(vector);
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
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Questions");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
	 
	 
	 
	 ////////////////////Temp TASK(Need To Change)//////////////////
	 private class TicketUpdateStatusTask extends AsyncTask<String, Integer, Boolean> {

 		private ProgressDialog pdialog;
 		private String response;
 		@Override
 		protected Boolean doInBackground(String... params) {
 			try {
 				
 				Log.i("RESET PASS URL", "<><>" + params[0]);
 				Log.i("Ticket Service ID", "<><>" + params[1]);
 				Log.i("Status", "<><>" + params[2]);
 				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
 				listParams.add(new BasicNameValuePair("ticket_service_id", params[1]));
 				listParams.add(new BasicNameValuePair("status", params[2]));				
 				listParams.add(new BasicNameValuePair("action", "update_service_status"));
 				response = new HttpWorker().getData(params[0], listParams);
 				response = response.substring(3);
 				Log.i("RESPONSE", "Login Resp>> " + response);
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
 			isAfterUpdateServiceStatus = true;
 			new TicketExtraTask().execute(CONSTANTS.BASE_URL);
 		}

 		@Override
 		protected void onPreExecute() {
 			super.onPreExecute();
 			pdialog = new ProgressDialog(getActivity());
 			pdialog.setCancelable(false);
 			pdialog.setIcon(R.drawable.info_icon);
 			pdialog.setTitle("Update Service Status");
 			pdialog.setMessage("Working...");
 			pdialog.show();
 		}
 	}
	 
}
