package com.scanchex.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.bo.ScToleranceInfo;
import com.scanchex.ui.R;

public class SpinnerToleranceAdapter extends ArrayAdapter<ScToleranceInfo> {

	// Your sent context
	private Context context;
	ArrayList<ScToleranceInfo> values;
	private LayoutInflater mInflater;
	String name;

	public SpinnerToleranceAdapter(Context context, int textViewResourceId,
			ArrayList<ScToleranceInfo> values) {
		super(context, textViewResourceId, values);

		this.context = context;
		this.values = values;
		// this.name = name;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return values.size();
	}

	public ScToleranceInfo getItem(int position) {
		return values.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// And the "magic" goes here
	// This is for the "passive" state of the spinner
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = mInflater.inflate(R.layout.spinner_manual_look, null);
		TextView label = (TextView) convertView
				.findViewById(R.id.textViewSpinner);
		ImageView im = (ImageView) convertView.findViewById(R.id.imageSpinner);
		label.setTextColor(0xff000000);
		label.setBackgroundColor(0xffFFD6E2);
//		if (position == 0) {
//			im.setVisibility(View.GONE);
//			//label.setText(name);
//			label.setGravity(Gravity.LEFT);
//		}
		
		//else 
		{
			label.setGravity(Gravity.LEFT);
			im.setVisibility(View.GONE);
			label.setText(values.get(position).getName());
		}
		return convertView;
	}

	// And here is when the "chooser" is popped up
	// Normally is the same view, but you can customize it if you want
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		return getView(position, convertView, parent);
	}
}
