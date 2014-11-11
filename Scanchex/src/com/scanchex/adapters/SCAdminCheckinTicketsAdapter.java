package com.scanchex.adapters;

import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.ui.R;
import com.scanchex.ui.SCDetailsFragmentScreen;
import com.squareup.picasso.Picasso;

public class SCAdminCheckinTicketsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private Vector<AssetsTicketsInfo> vector;
	AQuery aq;

	public SCAdminCheckinTicketsAdapter(Context context,
			Vector<AssetsTicketsInfo> vector) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.vector = vector;
		aq = new AQuery(context);

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
			convertView = mInflater.inflate(
					R.layout.sc_admin_checkin_tickets_row, null);
			holder = new ViewHolder();
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.tickets_layout);
			holder.image = (ImageView) convertView
					.findViewById(R.id.image_view);
			holder.ticketStatusIcon = (ImageView) convertView
					.findViewById(R.id.ticket_status_icon);
			holder.clientName = (TextView) convertView.findViewById(R.id.text1);
			holder.phoneNumber = (TextView) convertView
					.findViewById(R.id.text2);
			holder.address1 = (TextView) convertView.findViewById(R.id.text3);
			holder.address2 = (TextView) convertView.findViewById(R.id.text4);
			holder.ticketId = (TextView) convertView.findViewById(R.id.text5);
			holder.assetId = (TextView) convertView.findViewById(R.id.text6);
			holder.assetName = (TextView) convertView.findViewById(R.id.text7);
			holder.ticketStartDate = (TextView) convertView
					.findViewById(R.id.text8);
			holder.ticketStartTime = (TextView) convertView
					.findViewById(R.id.text9);
			holder.buttonCheckIn = (Button) convertView
					.findViewById(R.id.buttonCheckIn);
			holder.checkimage = (ImageView) convertView
					.findViewById(R.id.checkimage);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		AssetsTicketsInfo tInfo = this.vector.get(position);
		if (tInfo.ticketOverDue.equals("1")) {

			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.overDue));
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.excalamation_icon);
			holder.buttonCheckIn.setVisibility(View.VISIBLE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("Assigned")
				&& tInfo.ticketOverDue.equals("0")) {

			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.assign));
			holder.ticketStatusIcon.setVisibility(View.GONE);
			holder.buttonCheckIn.setVisibility(View.VISIBLE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("Assigned")
				&& tInfo.ticketOverDue.equals("1")) {

			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.assign));
			holder.ticketStatusIcon.setVisibility(View.GONE);
			holder.buttonCheckIn.setVisibility(View.VISIBLE);
			holder.buttonCheckIn.setBackgroundColor(this.context.getResources()
					.getColor(R.color.red));
		}

		else if (tInfo.ticketStatus.equalsIgnoreCase("complete")) {

			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.completed));
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.accept_ticket);
			holder.buttonCheckIn.setVisibility(View.INVISIBLE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("pending")) {

			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.pending));
			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.lightning_image);
			holder.buttonCheckIn.setVisibility(View.VISIBLE);
		} else {
			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.overDue));
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.excalamation_icon);
			holder.buttonCheckIn.setVisibility(View.VISIBLE);
		}
		// String ticketStartDate;
		// String ticketStartTime;
		String dueDate = tInfo.getTicketStartDate();
		String dueTime = tInfo.getTicketStartTime();

		// if(tInfo.ticketStartDate.)
		// {
		//
		// }

		holder.clientName.setText(tInfo.employee);
		holder.phoneNumber.setText(tInfo.assetPhone);
		holder.phoneNumber.setTag(position);
		holder.address1.setText(tInfo.addressStreet);
		holder.address2.setText(tInfo.addressCity + ", " + tInfo.addressState);
		holder.ticketId.setText(tInfo.ticketId);
		holder.assetId.setText(tInfo.assetUNAssetId);
		holder.assetName.setText(tInfo.assetDescription);
		holder.ticketStartDate.setText(tInfo.ticketStartDate);
		holder.ticketStartTime.setText(tInfo.ticketStartTime);

		if (tInfo.ticket_type.equals("check_in")) {

			Log.v("ticket_type", "ticket_type" + tInfo.ticket_type);
			holder.checkimage.setImageResource(R.drawable.door_in);

		} else if (tInfo.ticket_type.equals("check_out")) {

			Log.v("ticket_type", "ticket_type" + tInfo.ticket_type);
			holder.checkimage.setImageResource(R.drawable.door_out);
		}
		holder.buttonCheckIn.setTag(position);

		Picasso.with(this.context) //
				.load(tInfo.thumbPhotoUrl) //
				.placeholder(R.drawable.photo_not_available) //
				.error(R.drawable.photo_not_available) //
				.into(holder.image);

		return convertView;
	}

	class ViewHolder {

		RelativeLayout layout;
		ImageView ticketStatusIcon;
		ImageView image;
		TextView clientName;
		TextView phoneNumber;
		TextView address1;
		TextView address2;
		TextView ticketId;
		TextView assetId;
		TextView assetName;
		TextView ticketStartDate;
		TextView ticketStartTime;
		Button buttonCheckIn;
		ImageView checkimage;

	}

	public void setAssetTicetData(Vector<AssetsTicketsInfo> vector) {
		this.vector = vector;
	}
}
