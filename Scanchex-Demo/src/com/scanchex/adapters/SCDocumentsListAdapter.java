package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.bo.Item;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.bo.SectionItem;
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
		
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sc_documents_row, null);
			holder = new ViewHolder();
			holder.textView1 = (TextView) convertView.findViewById(R.id.text1);
			holder.documentStatus = (TextView) convertView.findViewById(R.id.status1);
			
			holder.documentIconView = (ImageView) convertView
					.findViewById(R.id.documentIconId);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		if (position % 2 == 0) {
			convertView.setBackgroundColor(this.context.getResources()
					.getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(this.context.getResources()
					.getColor(R.color.grey));
		}
		SCDocumentInfo dInfo = vector.get(position);

		String documentUrl = dInfo.getDocumentUrl();
		int index = documentUrl.lastIndexOf('.');
		String extension = (documentUrl.substring(index + 1));

		if (extension.equalsIgnoreCase("pdf")) {

			holder.documentIconView.setImageResource(R.drawable.pdf);

		} else if (extension.equalsIgnoreCase("xlsx")) {

			holder.documentIconView.setImageResource(R.drawable.xlsx);

		} else if (extension.equalsIgnoreCase("xls")) {

			holder.documentIconView.setImageResource(R.drawable.xls);

		} else if (extension.equalsIgnoreCase("docx")) {

			holder.documentIconView.setImageResource(R.drawable.docx);

		} else if (extension.equalsIgnoreCase("pptx")) {

			holder.documentIconView.setImageResource(R.drawable.pptx);

		} else if (extension.equalsIgnoreCase("txt")) {

			holder.documentIconView.setImageResource(R.drawable.text);

		} else if (extension.equalsIgnoreCase("jpg")
				|| extension.equalsIgnoreCase("jpeg")) {

			holder.documentIconView.setImageResource(R.drawable.jpeg);

		} else if (extension.equalsIgnoreCase("png")) {
			holder.documentIconView.setImageResource(R.drawable.png);

		} else if (extension.equalsIgnoreCase("gif")) {

			holder.documentIconView.setImageResource(R.drawable.gif);

		} else {
			holder.documentIconView.setImageResource(R.drawable.pdf);
		}

		holder.textView1.setText(dInfo.documentSubject);
		if ( dInfo.status == "hold" ) {
		holder.documentStatus.setText("(Hold)");
		holder.documentIconView.setImageResource(R.drawable.pdfonhold);
		} else {
			holder.documentStatus.setText("");
		}
			
		return convertView;
	}
	

	class ViewHolder {
		
		TextView textView1;
		TextView documentStatus;
		ImageView documentIconView;

	}
	
	public void setExtraInfo(Vector<SCDocumentInfo> vector){
		this.vector = vector;
	}
	

}
