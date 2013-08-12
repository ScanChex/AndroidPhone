package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.ui.R;

public class SCDocumentsListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context context;
	private Vector<SCDocumentInfo> vector;
	public SCDocumentsListAdapter(Context context, Vector<SCDocumentInfo> vector) {
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
	
		return vector.get(position);
	}


	public long getItemId(int position) {
	
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
	
	ViewHolder holder;
	
	if (convertView == null){
		convertView = mInflater.inflate(R.layout.sc_documents_row, null);
		holder = new ViewHolder();
		holder.textView1 = (TextView) convertView.findViewById(R.id.text1);
		
		convertView.setTag(holder);
	}else{
	
		holder = (ViewHolder) convertView.getTag();
	}
	
		SCDocumentInfo dInfo = vector.get(position);
		holder.textView1.setText(dInfo.documentSubject);
		return convertView;
	}
	

	class ViewHolder {
		
		TextView textView1;
		
	}
	
	public void setExtraInfo(Vector<SCDocumentInfo> vector){
		this.vector = vector;
	}
	

}
