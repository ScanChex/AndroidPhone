package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scanchex.bo.SCServiceComponentInfo;
import com.scanchex.ui.R;


public class SCServiceComponentAdapter extends BaseAdapter{
	
	
	private LayoutInflater mInflater;
	private Context context;
	private Vector<SCServiceComponentInfo> vector;
	public SCServiceComponentAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount(){
		if(vector!=null && this.vector.size()>0){
			return this.vector.size()+1;
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
			convertView = mInflater.inflate(R.layout.sc_servicecomponent_row, null);
			holder = new ViewHolder();
			holder.textView1 = (TextView) convertView.findViewById(R.id.text1);
			holder.textView2 = (TextView) convertView.findViewById(R.id.text2);
			holder.textView3 = (TextView) convertView.findViewById(R.id.text3);
			holder.textView4 = (TextView) convertView.findViewById(R.id.text4);
			holder.textView5 = (TextView) convertView.findViewById(R.id.text5);
			holder.textView6 = (TextView) convertView.findViewById(R.id.text6);
			convertView.setTag(holder);
		}else{
		
			holder = (ViewHolder) convertView.getTag();
		}

		
		if(position==0){
			
			holder.textView1.setText("PartNo.");	
			holder.textView2.setText("Qty");	
			holder.textView3.setText("");
			
			holder.textView4.setText("Description");
			holder.textView5.setText("Price");
			holder.textView6.setText("Total");
			
			holder.textView1.setTextSize(16);
			holder.textView2.setTextSize(16);
			holder.textView3.setTextSize(16);
			holder.textView4.setTextSize(16);
			holder.textView5.setTextSize(16);
			holder.textView6.setTextSize(16);
			holder.textView1.setTextColor(this.context.getResources().getColor(R.color.blue));
			holder.textView2.setTextColor(this.context.getResources().getColor(R.color.blue));
			holder.textView3.setTextColor(this.context.getResources().getColor(R.color.blue));
			holder.textView4.setTextColor(this.context.getResources().getColor(R.color.blue));
			holder.textView5.setTextColor(this.context.getResources().getColor(R.color.blue));
			holder.textView6.setTextColor(this.context.getResources().getColor(R.color.blue));
		}else{
			SCServiceComponentInfo compInfo = vector.get(position-1);
			
			holder.textView1.setText(compInfo.compId);	
			holder.textView2.setText(compInfo.compQuantity);	
			holder.textView3.setText(compInfo.compCost);	
			
			holder.textView4.setText(compInfo.compDescription);
			holder.textView5.setText("");	
			holder.textView6.setText("");	
			
			holder.textView1.setTextSize(14);
			holder.textView2.setTextSize(14);
			holder.textView3.setTextSize(14);
			holder.textView4.setTextSize(14);
			holder.textView5.setTextSize(14);
			holder.textView6.setTextSize(14);
			
			holder.textView1.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.textView2.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.textView3.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.textView4.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.textView5.setTextColor(this.context.getResources().getColor(R.color.black));
			holder.textView6.setTextColor(this.context.getResources().getColor(R.color.black));
			
			
		}
		return convertView;
	}
	

	class ViewHolder {

		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		TextView textView5;
		TextView textView6;
		
		
	}
	
	public void setComponentInfo(Vector<SCServiceComponentInfo> vector){
		this.vector = vector;
	}


}
