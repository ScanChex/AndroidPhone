package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCMessageInfo;
import com.scanchex.ui.R;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCMessagesAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private Vector<SCMessageInfo> vector;

	public SCMessagesAdapter(Context context, Vector<SCMessageInfo> vector) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.vector = vector;
	}

	@Override
	public int getCount() {
		if (this.vector != null && this.vector.size() > 0) {
			return vector.size();
		} else {
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

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sc_messageview_row, null);
			holder = new ViewHolder();
			holder.relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.tickets_layout);
			holder.image = (ImageView) convertView
					.findViewById(R.id.image_view);
			holder.messageText = (TextView) convertView
					.findViewById(R.id.text1);
			holder.textViewTime= (TextView) convertView
					.findViewById(R.id.textViewTime);
			holder.replyButton = (Button) convertView.findViewById(R.id.text2);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		SCMessageInfo tInfo = this.vector.get(position);

		if (tInfo.senderId.equalsIgnoreCase(SCPreferences.getPreferences()
				.getUserName(context))) {

			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(40, 0, 0, 0);
			holder.relativeLayout.setLayoutParams(params);
			holder.replyButton.setVisibility(View.GONE);
		}

		holder.messageText.setText(tInfo.message);
		holder.textViewTime.setText(tInfo.dateTime);
		holder.replyButton.setTag(position);
		Log.i("PHOTO URL", "<> " + tInfo.senderPhoto);
		Picasso.with(this.context) //
				.load(tInfo.senderPhoto) //
				.placeholder(R.drawable.app_icon) //
				.error(R.drawable.app_icon) 
				.into(holder.image);
		return convertView;
	}

	class ViewHolder {

		ImageView image;
		TextView messageText,textViewTime;
		Button replyButton;
		RelativeLayout relativeLayout;

	}

	public void setAssetTicetData(Vector<SCMessageInfo> vector) {
		this.vector = vector;
	}
}
