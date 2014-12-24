package com.scanchex.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanchex.bo.ScAdminManualLookModel;
import com.scanchex.ui.R;
import com.squareup.picasso.Picasso;

public class SpinnerCheckOutClientAdapter extends
		ArrayAdapter<ScAdminManualLookModel> {

	// Your sent context
	private Context context;
	ArrayList<ScAdminManualLookModel> values;
	private LayoutInflater mInflater;
	String name;

	public SpinnerCheckOutClientAdapter(Context context,
			int textViewResourceId, ArrayList<ScAdminManualLookModel> values,
			String name) {
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

		convertView = mInflater.inflate(R.layout.spinner_checkout_view, null);
		TextView label = (TextView) convertView
				.findViewById(R.id.textViewSpinner);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageSpinner);

		label.setTextColor(0XFF000000);
		label.setBackgroundColor(0xffFFFFFF);
		if (position == 0) {

			label.setText(name);

			imageView.setVisibility(View.GONE);
		} else {
			label.setText(values.get(position).getFull_name());
			if (name.equals("Client")) {
				imageView.setVisibility(View.GONE);
			}

			try {
				Picasso.with(context)
						.load(values.get(position).getEmployee_url())
						.placeholder(R.drawable.photo_not_available)
						.error(R.drawable.app_icon).into(imageView);
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
