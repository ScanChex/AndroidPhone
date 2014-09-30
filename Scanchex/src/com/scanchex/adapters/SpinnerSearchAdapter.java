package com.scanchex.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.android.gms.internal.ck;
import com.scanchex.ui.R;
import com.scanchex.utils.AdminUserNameModel;
import com.squareup.picasso.Picasso;

public class SpinnerSearchAdapter extends ArrayAdapter<AdminUserNameModel> {

	// Your sent context
	private Context context;
	// Your custom values for the spinner (User)
	ArrayList<AdminUserNameModel> values;
	private LayoutInflater mInflater;
	AQuery aQuery;

	public SpinnerSearchAdapter(Context context, int textViewResourceId,
			ArrayList<AdminUserNameModel> values) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		aQuery = new AQuery(context);
	}

	public int getCount() {
		return values.size();
	}

	public AdminUserNameModel getItem(int position) {
		return values.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// And the "magic" goes here
	// This is for the "passive" state of the spinner
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = mInflater.inflate(R.layout.spinner_search_view, parent,
				false);
		// I created a dynamic TextView here, but you can reference your own
		// custom layout for each spinner item
		TextView label = (TextView) convertView
				.findViewById(R.id.textViewSpinner);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView1);
		CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
		if (position == 0) {

			label.setGravity(Gravity.CENTER);
			label.setText("REGISTER EMPLOYEE DEVICE");
			label.setPadding(15, 15, 15, 15);
			imageView.setVisibility(View.GONE);
			check.setVisibility(View.GONE);
		} else {
			label.setHeight(60);
			label.setTextColor(0xff2B91AF);
			label.setGravity(Gravity.CENTER | Gravity.LEFT);
			imageView.setVisibility(View.VISIBLE);
			check.setVisibility(View.VISIBLE);
			label.setText(values.get(position).getFull_name());
			// check.setChecked(values.get(position).getisregistered());

			if (values.get(position).getisregistered() == 1) {
				check.setChecked(true);
				Log.v("registered", "registered");
			}
			// aQuery.id(imageView)
			// .image(values.get(position).getPhoto(), true, true, 0, 0, null,
			// 0,
			// 0.8f / 1.0f);

			try {
				Picasso.with(context).load(values.get(position).getPhoto())
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
