package com.scanchex.adapters;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.SCTicketExtraInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.ui.R; 
import com.scanchex.ui.SCCameraPeviewScreen;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;

 
public class CheckPointAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private Activity activity;
	private  ArrayList<ScCheckPoints>  vector;
	private  ArrayList<ScCheckPoints>  nCheckPoints;
	
	public CheckPointAdapter(Activity context, ArrayList<ScCheckPoints> arrayList) {
		mInflater = LayoutInflater.from(context);
		this.activity = context;
		this.vector = arrayList;
		nCheckPoints = new ArrayList<ScCheckPoints>();
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

	
	public View getView(final int position, View convertView, ViewGroup parent) {
	
	final ViewHolder holder;
	
	if (convertView == null){
		convertView = mInflater.inflate(R.layout.sc_ticketcheckinfo_row, null);
		holder = new ViewHolder(); 
		holder.textView2 = (TextView) convertView.findViewById(R.id.text2);
		holder.scan = (Button) convertView.findViewById(R.id.buttonScan);
		
		
		convertView.setTag(holder);
	}else{
	
		holder = (ViewHolder) convertView.getTag();
	}
	
		final ScCheckPoints extraInfo = vector.get(position);
		holder.textView2.setText(extraInfo.description);
		
		if(extraInfo.isTrue){
			holder.scan.setVisibility(View.INVISIBLE);	
		}
		
		holder.scan.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(Resources.getResources().isFirstScanDone()){					 
					
					final ScCheckPoints check = new ScCheckPoints();			
					check.isTrue = true;
					check.checkpoint_id = extraInfo.checkpoint_id;
					check.description = extraInfo.description;
					check.qr_code = extraInfo.qr_code;
					check.time = extraInfo.time;
					
					ArrayList<ScCheckPoints> aa  =new ArrayList<ScCheckPoints>();
					aa = Resources.getResources().getCheckPoints();
					aa.set(position, check);
					Resources.getResources().setCheckPoints(aa);
					Resources.getResources().setCheckPointScan(true);
					
					Intent i = new Intent(activity, SCCameraPeviewScreen.class);
					i.putExtra("qr_code",extraInfo.qr_code);
					i.putExtra("position",position);
					i.putExtra("checkpoint_id",check.checkpoint_id);
					i.putExtra("description",check.description);
					
					activity.startActivity(i);
				}else{
					Toast.makeText(activity, "Please Scan Ticket First!", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		notifyDataSetChanged();
		holder.scan.setTag(position);
		return convertView;
	}
	

	class ViewHolder {
		
	//	RelativeLayout layout;
		TextView textView2;
		Button scan;
		
	}
	


}