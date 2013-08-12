package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.ui.R;

public class SCTicketsAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context context;
	private Vector<AssetsTicketsInfo> vector;
	public SCTicketsAdapter(Context context, Vector<AssetsTicketsInfo> vector) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.vector = vector;
	}

	@Override
	public int getCount(){
		if(this.vector!=null && this.vector.size()>0){
			return vector.size();
		}else{
			return 0;
		}
		
	}

	public Object getItem(int position) {
	
		return this.vector.get(position);
	}


	public long getItemId(int position) {
	
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
	
	ViewHolder holder;
	
	if (convertView == null){
		convertView = mInflater.inflate(R.layout.sc_tickets_row, null);
		holder = new ViewHolder();
		holder.layout = (RelativeLayout)convertView.findViewById(R.id.tickets_layout);
		holder.clientName = (TextView) convertView.findViewById(R.id.text1);
		holder.phoneNumber = (TextView) convertView.findViewById(R.id.text2);
		holder.address1 = (TextView) convertView.findViewById(R.id.text3);
		holder.address2 = (TextView) convertView.findViewById(R.id.text4);
		
		holder.ticketId = (TextView) convertView.findViewById(R.id.text5);
		holder.assetId = (TextView) convertView.findViewById(R.id.text6);
		holder.assetName = (TextView) convertView.findViewById(R.id.text7);
		holder.ticketStartDate = (TextView) convertView.findViewById(R.id.text8);
		holder.ticketStartTime = (TextView) convertView.findViewById(R.id.text9);
		convertView.setTag(holder);
	}else{
	
		holder = (ViewHolder) convertView.getTag();
	}
	
		AssetsTicketsInfo tInfo = this.vector.get(position);
		if(tInfo.ticketOverDue.equals("1")){
			
			holder.layout.setBackgroundColor(this.context.getResources().getColor(R.color.red));
		}else if(tInfo.ticketStatus.equalsIgnoreCase("Assigned") && tInfo.ticketOverDue.equals("0")){
			
			holder.layout.setBackgroundColor(this.context.getResources().getColor(R.color.green));
		}else if(tInfo.ticketStatus.equalsIgnoreCase("complete")){
			
			holder.layout.setBackgroundColor(this.context.getResources().getColor(R.color.grey));
		}else if(tInfo.ticketStatus.equalsIgnoreCase("pending")){
			
			holder.layout.setBackgroundColor(this.context.getResources().getColor(R.color.blue));
		}else{
			holder.layout.setBackgroundColor(this.context.getResources().getColor(R.color.red));
		}
		
		holder.clientName.setText(tInfo.assetClientName);
		holder.phoneNumber.setText(tInfo.assetPhone);
		holder.address1.setText(tInfo.addressStreet);
		holder.address2.setText(tInfo.addressCity+","+tInfo.addressState+" "+tInfo.addressPostalCode);
	
		holder.ticketId.setText(tInfo.ticketId);
		holder.assetId.setText(tInfo.assetUNAssetId);
		holder.assetName.setText(tInfo.assetDescription);
		holder.ticketStartDate.setText(tInfo.ticketStartDate);
		holder.ticketStartTime.setText(tInfo.ticketStartTime);
	
		return convertView;
	}
	

	class ViewHolder {
		
		RelativeLayout layout;
		TextView clientName;
		TextView phoneNumber;
		TextView address1;
		TextView address2;
		
		TextView ticketId;
		TextView assetId;
		TextView assetName;
		TextView ticketStartDate;
		TextView ticketStartTime;
	}
	
	public void setAssetTicetData(Vector<AssetsTicketsInfo> vector){
		this.vector = vector;
	}
}
