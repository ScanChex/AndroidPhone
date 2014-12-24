package com.scanchex.adapters;

import java.util.ArrayList;

import com.scanchex.ui.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.adapters.EntryAdapter.ViewHolder;
import com.scanchex.bo.EntryItem;
import com.scanchex.bo.Item;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.bo.SectionItem;

public class DocumentEntryAdapter extends ArrayAdapter<Item> {
	 
	 private Context context;
	 private ArrayList<Item> items;
	 private LayoutInflater vi;
	 
	 public  DocumentEntryAdapter(Activity context, ArrayList<Item> items) {
			super(context, 0, items);
		  this.context = context;
	  this.items = items;
	  vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	 
	 
	 public View getView(int position, View convertView, ViewGroup parent) {
	  View v = convertView;
	 
	  final Item i = items.get(position);
	  if (i != null) {
	   if(i.isSection()){
	    SectionItem si = (SectionItem)i;
	    v = vi.inflate(R.layout.document_item_section, null);
	 
	    v.setOnClickListener(null);
	    v.setOnLongClickListener(null);
	    v.setLongClickable(false);
	     
	    final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
	    sectionView.setText(si.getTitle());
	     
	   }else{
	   // EntryItem ei = (EntryItem)i;
		final ViewHolder holder = new ViewHolder();
		
	    v = vi.inflate(R.layout.sc_documents_row, null);
		holder.textView1 = (TextView) v.findViewById(R.id.text1);
		holder.documentStatus = (TextView) v.findViewById(R.id.status1);
		
		holder.documentIconView = (ImageView) v
				.findViewById(R.id.documentIconId);

		v.setTag(holder);
		
		if (position % 2 == 0) {
			v.setBackgroundColor(this.context.getResources()
					.getColor(R.color.white));
		} else {
			v.setBackgroundColor(this.context.getResources()
					.getColor(R.color.grey));
		}

		SCDocumentInfo dInfo = (SCDocumentInfo)i;
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
		Log.i("RESPONSE", "document status  " + dInfo.getStatus());
		
		if ( dInfo.getStatus().equals("hold") ) {
		holder.documentStatus.setText("(Hold)");
		holder.documentIconView.setImageResource(R.drawable.pdfonhold);
		} else
		
		if ( dInfo.getStatus().equals("completed") ) {
			holder.documentStatus.setText("(Completed)");
			holder.documentIconView.setImageResource(R.drawable.pdfclosed);
			} else {
			holder.documentStatus.setText("");
		}

	   }
	  }
	  return v;
	 }
	 
	 
	 class ViewHolder {
			
			TextView textView1;
			TextView documentStatus;
			ImageView documentIconView;

		}
	 
	}

