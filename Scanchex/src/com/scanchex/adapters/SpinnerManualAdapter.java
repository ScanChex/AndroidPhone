package com.scanchex.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.bo.ScAdminManualLookModel;
import com.scanchex.ui.R;
import com.squareup.picasso.Picasso;

public class SpinnerManualAdapter extends ArrayAdapter<ScAdminManualLookModel> {

	// Your sent context
	private Context context;
	ArrayList<ScAdminManualLookModel> values;
	private LayoutInflater mInflater;
	String name;

	public SpinnerManualAdapter(Context context, int textViewResourceId,
			ArrayList<ScAdminManualLookModel> values,String name) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
		this.name = name;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return values.size();
	}

	public ScAdminManualLookModel getItem(int position) {
		return values.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// And the "magic" goes here
	// This is for the "passive" state of the spinner
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = mInflater.inflate(R.layout.spinner_manual_look, null);
		ImageView image = (ImageView) convertView
				.findViewById(R.id.imageSpinner);
		TextView label = (TextView) convertView
				.findViewById(R.id.textViewSpinner);
		if(name.equalsIgnoreCase("Address")){
			label.setHeight(90);
		}
		if(position == 0){
			
			label.setText(name);
			image.setVisibility(View.GONE);

		} else {

			label.setText(values.get(position).getDisplaySpinnerName());

			try {
				Picasso.with(context)

				.load(values.get(position).getAsset_photo())
						.placeholder(R.drawable.photo_not_available)
						.error(R.drawable.app_icon).into(image);
			} catch (Exception e) {
				e.printStackTrace();
			}

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
