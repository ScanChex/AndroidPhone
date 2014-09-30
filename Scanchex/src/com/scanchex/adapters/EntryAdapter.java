package com.scanchex.adapters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.EntryItem;
import com.scanchex.bo.Item;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.bo.SectionItem;
import com.scanchex.network.HttpWorker;
import com.scanchex.ui.R;
import com.scanchex.ui.SCAssetsFragment;
import com.scanchex.ui.SCCameraPeviewScreen;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;

public class EntryAdapter extends ArrayAdapter<Item> {

	private Activity context;
	private ArrayList<Item> items;
	private LayoutInflater vi;
	private ArrayList<Item> searchListFriends;
	ArrayList<Item> aa;

	public EntryAdapter(Activity context, ArrayList<Item> items) {
		super(context, 0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		searchListFriends = new ArrayList<Item>();
		this.searchListFriends.addAll(items);

		aa = new ArrayList<Item>();
		aa = items;
	}

	@Override
	public View getView(final int position, View convertView1, ViewGroup parent) {
		View v = convertView1;

		final Item i = items.get(position);
		if (i != null) {
			if (i.isSection()) {
				SectionItem si = (SectionItem) i;
				v = vi.inflate(R.layout.list_item_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView sectionView = (TextView) v
						.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());

			} else {

				final EntryItem ei = (EntryItem) i;

				if (ei.isService == false) {
					final ViewHolder holder = new ViewHolder();
					v = vi.inflate(R.layout.sc_ticketcheckinfo_row, null);

					holder.textView1 = (TextView) v.findViewById(R.id.text1);
					holder.textView2 = (TextView) v.findViewById(R.id.text2);
					holder.textViewTime = (TextView) v
							.findViewById(R.id.textViewTime);
					holder.scan = (Button) v.findViewById(R.id.buttonScan);
					holder.checkBoxStatus = (ImageView)v.findViewById(R.id.checkBoxStatus);
					holder.textViewTime = (TextView) v
							.findViewById(R.id.textViewTime);
					//holder.textView1.setText(ei.checkpoint_id);
					holder.textView1.setVisibility(View.GONE);
					holder.textView2.setText(ei.description);
					holder.textViewTime.setText(ei.time);

					if (ei.isTrue) {
						holder.scan.setVisibility(View.GONE);
						holder.checkBoxStatus.setVisibility(View.VISIBLE);
					} else {
						holder.scan.setVisibility(View.VISIBLE);
						holder.checkBoxStatus.setVisibility(View.GONE);
					}

					holder.scan.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (Resources.getResources().isFirstScanDone()) {

								final ScCheckPoints check = new ScCheckPoints();
								check.isTrue = true;
								check.checkpoint_id = ei.checkpoint_id;
								check.description = ei.description;
								check.qr_code = ei.qr_code;
								check.time = ei.time;

								items.set(position, new EntryItem("", "",
										ei.description, "", "", ei.time, false,
										ei.checkpoint_id, ei.qr_code, true));

								Resources.getResources().setItemList(items);
								Resources.getResources()
										.setCheckPointScan(true);
								
								
								
								// holder.scan.setVisibility(View.GONE);
								Intent i = new Intent(context,
										SCCameraPeviewScreen.class);
								i.putExtra("qr_code", ei.qr_code);
								i.putExtra("position", position);
								i.putExtra("checkpoint_id", check.checkpoint_id);
								i.putExtra("description", check.description);
								i.putExtra("time", check.time);

								context.startActivity(i);
								notifyDataSetChanged();
							} else {
								Toast.makeText(context,
										"Please Scan Ticket First!",
										Toast.LENGTH_SHORT).show();
							}

						}
					});

					holder.scan.setTag(position);
				} else {
					final ViewHolder holder = new ViewHolder();
					v = vi.inflate(R.layout.sc_ticketextrainfo_row, null);

					holder.textView1 = (TextView) v.findViewById(R.id.text1);
					holder.textView2 = (TextView) v.findViewById(R.id.text2);
					holder.textViewTime = (TextView) v
							.findViewById(R.id.textViewTime);
					holder.ticketCheckBox = (ImageView) v
							.findViewById(R.id.checkBoxStatus);

					holder.ticketCheckBox.setTag(position);
					// convertView.setTag(holder);

					holder.textView1.setText(ei.model);
					holder.textView2.setText(ei.description);
					holder.textViewTime.setText(ei.time);
					if (ei.status.equalsIgnoreCase("0")) {
						holder.ticketCheckBox.setImageResource(R.drawable.ticket_status_unselect);
					}else{
						holder.ticketCheckBox.setImageResource(R.drawable.ticket_status_select);
					}
					
					holder.ticketCheckBox.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(Resources.getResources().isFirstScanDone()){
							if(ei.status.equals("0")){
								holder.ticketCheckBox.setImageResource(R.drawable.ticket_status_select);
								new TicketUpdateStatusTask().execute(CONSTANTS.BASE_URL, ei.ticketServiceId, "1");
							}else{
								holder.ticketCheckBox.setImageResource(R.drawable.ticket_status_unselect);
								new TicketUpdateStatusTask().execute(CONSTANTS.BASE_URL, ei.ticketServiceId, "0");
							}
							}else{
								Toast.makeText(context, "Please Scan Ticket First!", Toast.LENGTH_SHORT).show();
							}
						}
					});
 
				}
			}
		}

		return v;
	}

	class ViewHolder {

		TextView textView2;
		Button scan;
		ImageView checkBoxStatus;
		RelativeLayout layout;
		TextView textView1, textViewTime;
		ImageView ticketCheckBox;

	}

	private class TicketUpdateStatusTask extends
			AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("RESET PASS URL", "<><>" + params[0]);
				Log.i("Ticket Service ID", "<><>" + params[1]);
				Log.i("Status", "<><>" + params[2]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				// listParams.add(new BasicNameValuePair("ticket_service_id",
				// params[1]));
				listParams.add(new BasicNameValuePair("service_id", params[1]));
				listParams.add(new BasicNameValuePair("status", params[2]));
				listParams.add(new BasicNameValuePair("action",
						"update_service_status"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				Log.d("Responce", "res"+obj);
				return true;
			} catch (Exception e) {
				Log.e("Exception", e.getMessage(), e);
			}
			return Boolean.FALSE;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pdialog.dismiss();
			SCAssetsFragment.isAfterUpdateServiceStatus = true;
			
			// new TicketExtraTask().execute(CONSTANTS.BASE_URL);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(context);
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Update Service Status");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

}
