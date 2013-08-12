package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCHistoryInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCHistoryFragment extends ListFragment{
	
	private TextView assetId;
	private TextView assetDescription;
	private TextView assetAddress;
	AssetsTicketsInfo tInfo;
	private SCHistoryListAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SCHistoryListAdapter(getActivity(), Resources.getResources().getHistoryData());
		setListAdapter(adapter);
		tInfo = Resources.getResources().getAssetTicketInfo();
		if(Resources.getResources().getHistoryData()==null){
			new HistoryTask().execute(CONSTANTS.BASE_URL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.sc_historyfragment_screen, container, false);
		 assetId = (TextView)view.findViewById(R.id.asset_id);
		 assetDescription = (TextView)view.findViewById(R.id.des_id);
		 assetAddress = (TextView)view.findViewById(R.id.add_id);
		 assetId.setText(tInfo.assetUNAssetId);
		 assetDescription.setText(tInfo.assetDescription);
		 assetAddress.setText(tInfo.addressStreet+"\n"+tInfo.addressCity+","+tInfo.addressState+" "+tInfo.addressPostalCode);
		
		return view;
	}
	////////////////////ADAPTER////////////////////
	
	
	class SCHistoryListAdapter extends BaseAdapter implements OnClickListener{
		
		private LayoutInflater mInflater;
		private Context context;
		private Vector<SCHistoryInfo> vector;
		public SCHistoryListAdapter(Context context, Vector<SCHistoryInfo> vector) {
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
				convertView = mInflater.inflate(R.layout.sc_history_row, null);
				holder = new ViewHolder();
				holder.textView1 = (TextView) convertView.findViewById(R.id.text1);
				holder.textView2 = (TextView) convertView.findViewById(R.id.text2);
				holder.textView3 = (TextView) convertView.findViewById(R.id.text3);
				holder.textView4 = (TextView) convertView.findViewById(R.id.text4);
				holder.statusImage = (ImageView)convertView.findViewById(R.id.status_image);
				holder.notesImage = (ImageView)convertView.findViewById(R.id.notes_image);
				holder.photoImage = (ImageView)convertView.findViewById(R.id.photo_image);
				holder.audioImage = (ImageView)convertView.findViewById(R.id.audio_image);
				holder.videoImage = (ImageView)convertView.findViewById(R.id.video_image);
				convertView.setTag(holder);
			}else{
			
				holder = (ViewHolder) convertView.getTag();
			}
		
			SCHistoryInfo hisInfo = vector.get(position);
			
			if(position==0){
				
				holder.textView1.setText("Model");	
				holder.textView2.setText("Serial");	
				holder.textView3.setText("Installed N");
				holder.textView4.setText("Technician\njbrown");
				holder.statusImage.setVisibility(View.GONE);
				holder.notesImage.setVisibility(View.GONE);
				holder.photoImage.setVisibility(View.GONE);
				holder.audioImage.setVisibility(View.GONE);
				holder.videoImage.setVisibility(View.GONE);
				holder.textView3.setVisibility(View.VISIBLE);
				holder.textView4.setVisibility(View.VISIBLE);
				holder.textView1.setTextColor(this.context.getResources().getColor(R.color.blue));
				holder.textView2.setTextColor(this.context.getResources().getColor(R.color.blue));
				holder.textView3.setTextColor(this.context.getResources().getColor(R.color.blue));
				holder.textView4.setTextColor(this.context.getResources().getColor(R.color.blue));
			}else{
				holder.textView1.setText(hisInfo.historyDate);	
				holder.textView2.setText(hisInfo.historyFullName);	
				holder.textView3.setVisibility(View.GONE);
				holder.textView4.setVisibility(View.GONE);
				holder.statusImage.setVisibility(View.VISIBLE);
				holder.textView1.setTextColor(this.context.getResources().getColor(R.color.black));
				holder.textView2.setTextColor(this.context.getResources().getColor(R.color.black));
				
				if(hisInfo.historyWarrenty!=null && hisInfo.historyWarrenty.equalsIgnoreCase("Y")){
					holder.statusImage.setImageResource(R.drawable.ticket_status_select);
				}else{
					holder.statusImage.setImageResource(R.drawable.ticket_status_unselect);
				}
				
				if(hisInfo.historyNotes!=null && hisInfo.historyNotes.length()>0){
					holder.notesImage.setVisibility(View.VISIBLE);
				}else{
					holder.notesImage.setVisibility(View.GONE);
				}
				
				if(hisInfo.historyImages!=null && hisInfo.historyImages.length()>0){
					holder.photoImage.setVisibility(View.VISIBLE);
				}else{
					holder.photoImage.setVisibility(View.GONE);
				}
				
				if(hisInfo.historyVoice!=null && hisInfo.historyVoice.length()>0){
					holder.audioImage.setVisibility(View.VISIBLE);
				}else{
					holder.audioImage.setVisibility(View.GONE);
				}
				
				if(hisInfo.historyVideo!=null && hisInfo.historyVideo.length()>0){
					holder.videoImage.setVisibility(View.VISIBLE);
				}else{
					holder.videoImage.setVisibility(View.GONE);
				}
				
				holder.notesImage.setOnClickListener(this);
				holder.photoImage.setOnClickListener(this);
				holder.audioImage.setOnClickListener(this);
				holder.videoImage.setOnClickListener(this);
				
				holder.notesImage.setTag(position);
				holder.photoImage.setTag(position);
				holder.audioImage.setTag(position);
				holder.videoImage.setTag(position);
				
			}
			return convertView;
		}
		

		class ViewHolder {
			
			RelativeLayout layout;
			TextView textView1;
			TextView textView2;
			TextView textView3;
			TextView textView4;
			ImageView statusImage;
			
			ImageView notesImage;
			ImageView photoImage;
			ImageView audioImage;
			ImageView videoImage;
			
		}
		
		public void setExtraInfo(Vector<SCHistoryInfo> vector){
			this.vector = vector;
		}

		@Override
		public void onClick(View view) {
		
			int id = view.getId();
			if(id == R.id.notes_image){

				showAlertDialog("History Notes:", vector.get(Integer.parseInt(""+view.getTag())).historyNotes);
			}else if(id == R.id.photo_image){

				Intent i = new Intent(getActivity(), SCHistoryImageViewScreen.class);
				i.putExtra("PATH", vector.get(Integer.parseInt(""+view.getTag())).historyImages);
				startActivity(i);
			}else if(id == R.id.audio_image){
				
				Intent i = new Intent(getActivity(), SCAudioPlayer.class);
				i.putExtra("PATH", vector.get(Integer.parseInt(""+view.getTag())).historyVoice);
				startActivity(i);
			}else if(id == R.id.video_image){
				
				Intent i = new Intent(getActivity(), SCVideoPlayScreen.class);
				i.putExtra("PATH", vector.get(Integer.parseInt(""+view.getTag())).historyVideo);
				startActivity(i);
				
			}
			
		}
		
		private void showAlertDialog(String title, String message) {
			new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.info_icon)
			.setTitle(title)
			.setMessage(message)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						
				public void onClick(DialogInterface dialog, int which) {
							
				}
			}).show();
		}
	}

	
	////////////////////ASYNC TASK//////////////////
	 private class HistoryTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCHistoryInfo> vector;
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				
				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getActivity())));
				listParams.add(new BasicNameValuePair("asset_id", tInfo.assetId));
//				listParams.add(new BasicNameValuePair("asset_id", "4"));
				listParams.add(new BasicNameValuePair("action", "show_history"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONArray jArr = new JSONArray(response);
				vector = new Vector<SCHistoryInfo>();
				if(jArr!=null && jArr.length()>0){
					for(int i=0; i<jArr.length(); i++){
						SCHistoryInfo hisInfo = new SCHistoryInfo();
						JSONObject jObj = jArr.getJSONObject(i);
						hisInfo.historyDate = jObj.getString("date");
						hisInfo.historyTechnician = jObj.getString("technician");
						hisInfo.historyService = jObj.getString("service");
						hisInfo.historyWarrenty = jObj.getString("warranty");
						hisInfo.historyImages = jObj.getString("images");
						hisInfo.historyNotes = jObj.getString("notes");
						hisInfo.historyVideo = jObj.getString("video");
						hisInfo.historyVoice = jObj.getString("voice");
						hisInfo.historySerialNumber = jObj.getString("serial_number");
						hisInfo.historyModel = jObj.getString("model");
						hisInfo.historyFullName = jObj.getString("full_name");
						vector.add(hisInfo);
						Resources.getResources().setHistoryData(vector);
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
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("History Info");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}
		 
}
