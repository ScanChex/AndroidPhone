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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.ui.R;
import com.scanchex.ui.SCDetailsFragmentScreen;
import com.squareup.picasso.Picasso;

public class SCTicketsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private Vector<AssetsTicketsInfo> vector;
	AQuery aq;


	public SCTicketsAdapter(Context context, Vector<AssetsTicketsInfo> vector) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.vector = vector;
		aq = new AQuery(context);
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
			convertView = mInflater.inflate(R.layout.sc_tickets_row_new, null);
			holder = new ViewHolder();

			// holder.relativeImage1 =
			// (RelativeLayout)convertView.findViewById(R.id.relativeImage1);
			// holder.relativeImages =
			// (RelativeLayout)convertView.findViewById(R.id.relativeImages);
			// holder.linearCompany =
			// (LinearLayout)convertView.findViewById(R.id.linearCompany);
			holder.linearPreview = (RelativeLayout) convertView
					.findViewById(R.id.relativeImages);

			holder.layout = (LinearLayout) convertView
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
			holder.mapIcon = (ImageView) convertView
					.findViewById(R.id.map_icon);
			holder.detailIcon = (ImageView) convertView
					.findViewById(R.id.ticket_detail_icon);

			// holder.relativeImage1.getLayoutParams().width = a;
			// holder.linearCompany.getLayoutParams().width = b;
			// holder.linearTicket.getLayoutParams().width = c;
			// holder.relativeImages.getLayoutParams().width = d;

			// holder.image.getLayoutParams().width =e;
			// holder.image.getLayoutParams().height =e;
			// holder.ticketStatusIcon.getLayoutParams().width =f;
			// holder.ticketStatusIcon.getLayoutParams().height =f;

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
		} else if (tInfo.ticketStatus.equalsIgnoreCase("Assigned")
				&& tInfo.ticketOverDue.equals("0")) {
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.assign));
			holder.ticketStatusIcon.setVisibility(View.GONE);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("complete")) {

			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.completed));
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.accept_ticket);
		} else if (tInfo.ticketStatus.equalsIgnoreCase("pending")) {

			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.pending));
			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.lightning_image);
		} else {
			holder.ticketStatusIcon.setVisibility(View.VISIBLE);
			holder.layout.setBackgroundColor(this.context.getResources()
					.getColor(R.color.overDue));
			holder.ticketStatusIcon
					.setBackgroundResource(R.drawable.excalamation_icon);
		}

		holder.clientName.setText(tInfo.assetClientName);
		holder.phoneNumber.setText(tInfo.assetPhone);
		holder.phoneNumber.setTag(position);
		holder.address1.setText(tInfo.addressStreet);
		holder.address2.setText(tInfo.addressCity + ", " + tInfo.addressState);
		holder.ticketId.setText(tInfo.ticketId);
		holder.assetId.setText(tInfo.assetUNAssetId);
		holder.assetName.setText(tInfo.assetDescription);
		holder.ticketStartDate.setText(tInfo.ticketStartDate);
		holder.ticketStartTime.setText(tInfo.ticketStartTime);
		holder.mapIcon.setTag(position);
		holder.detailIcon.setTag(position);

		if (tInfo.assetType.equals("CHECK-OUT/CHECK-IN")) {
			holder.linearPreview.setVisibility(View.GONE);
		}

		Picasso.with(this.context) //
				.load(tInfo.thumbPhotoUrl) //
				.placeholder(R.drawable.photo_not_available) //
				.error(R.drawable.photo_not_available) //
				.into(holder.image);

		// aq.id(holder.image)
		// .image(tInfo.thumbPhotoUrl, true, true, 0, 0, null, 0,
		// .8f / 1.0f);

		return convertView;
	}

	class ViewHolder {

		LinearLayout layout;
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
		ImageView mapIcon;
		ImageView detailIcon;
		// ImageView callIcon;

		// Layouts
		// RelativeLayout relativeImage1,relativeImages,linearTicket;
		RelativeLayout linearPreview;
	}

	public void setAssetTicetData(Vector<AssetsTicketsInfo> vector) {
		this.vector = vector;
	}
}
