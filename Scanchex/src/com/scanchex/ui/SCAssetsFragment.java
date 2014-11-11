package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.EntryAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.EntryItem;
import com.scanchex.bo.Item;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.bo.SCTicketExtraInfo;
import com.scanchex.bo.ScCheckPoints;
import com.scanchex.bo.SectionItem;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCAssetsFragment extends Fragment implements OnClickListener {

	private TextView assetId;
	private TextView assetDescription;
	private TextView assetAddress;
	private ImageView assetImage;
	private AssetsTicketsInfo tInfo;
	// private SCTicketExtraInfoAdapter adapter;
	public static boolean isAfterUpdateServiceStatus;
	// CheckPointAdapter checkAdapter;
	private Button ScanTicketButton;
	ListView checkList;
	TextView emptyText;
	public static int countCheckPoint = 0;
	ListView services;
	ArrayList<Item> items = new ArrayList<Item>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isAfterUpdateServiceStatus = false;
		tInfo = Resources.getResources().getAssetTicketInfo();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sc_assets_fragment, container,
				false);
		tInfo = Resources.getResources().getAssetTicketInfo();
		assetId = (TextView) view.findViewById(R.id.asset_id);
		assetDescription = (TextView) view.findViewById(R.id.des_id);
		assetAddress = (TextView) view.findViewById(R.id.add_id);
		TextView start = (TextView) view.findViewById(R.id.timeStart);
		TextView end = (TextView) view.findViewById(R.id.timeCompleted);
		TextView total = (TextView) view.findViewById(R.id.totalTime);

		assetImage = (ImageView) view.findViewById(R.id.asset_image_id);
		ScanTicketButton = (Button) view.findViewById(R.id.scan_button);

		checkList = (ListView) view.findViewById(R.id.checkList);
		assetId.setText(tInfo.assetUNAssetId);
		assetDescription.setText(tInfo.assetDescription);
		assetAddress.setText(tInfo.addressStreet + "\n" + tInfo.addressCity
				+ ", " + tInfo.addressState + " " + tInfo.addressPostalCode);
		start.setText(tInfo.ticket_start_time);

		end.setText(tInfo.ticket_end_time);
		total.setText(tInfo.ticket_total_time);

		assetImage.setOnClickListener(this);
		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		if (Resources.getResources().getTicketExtraData() == null) {
			new TicketExtraTask().execute(CONSTANTS.BASE_URL);
		} else {
			EntryAdapter adapter = new EntryAdapter(getActivity(), Resources
					.getResources().getItemList());
			checkList.setAdapter(adapter);
			//setListViewHeightBasedOnChildren(checkList);
		}

		try {
			Picasso.with(getActivity()) //
					.load(tInfo.thumbPhotoUrl) //
					.placeholder(R.drawable.scan_chexs_logo) //
					.error(R.drawable.app_icon) //
					.into(assetImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v == ScanTicketButton) {
			long current = System.currentTimeMillis();
			long start = Resources.getResources().getTimeToStartTicket();
			if (current == start || current >= start) {
				Resources.getResources().setFirstScanDone(false);
				Intent i = new Intent(getActivity(), SCCameraPeviewScreen.class);
				startActivity(i);
			} else {
				Toast.makeText(getActivity(), "Ticket time is not started",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == assetImage) {

			String imageUrl = tInfo.thumbPhotoUrl;
			Intent intent = new Intent(getActivity(), SCImagePinch.class);
			intent.putExtra("ImageUrl", imageUrl);
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("On Resume Called", "ASSETS!!");

	}

	// //////////////////ASYNC TASK//////////////////
	private class TicketExtraTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCTicketExtraInfo> vector;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams.add(new BasicNameValuePair("action",
						"get_ticket_services"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				vector = new Vector<SCTicketExtraInfo>();
				if (!(obj.has("error"))) {

					JSONArray jArr = obj.getJSONArray("services");

					if (jArr != null && jArr.length() > 0) {
						for (int i = 0; i < jArr.length(); i++) {
							SCTicketExtraInfo extraInfo = new SCTicketExtraInfo();
							JSONObject jObj = jArr.getJSONObject(i);
							extraInfo.serviceId = jObj.getString("service_id");
							extraInfo.model = jObj.getString("model");
							extraInfo.description = jObj
									.getString("description");
							extraInfo.status = jObj.getString("status");
							extraInfo.ticketServiceId = jObj
									.getString("ticket_service_id");
							extraInfo.time = jObj.getString("estimated_time");
							vector.add(extraInfo);
							Resources.getResources().setTicketExtraData(vector);

						}
					}

				}
				return true;
			} catch (JSONException e) {
				Log.e("Exception", e.getMessage(), e);
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

			if (!isAfterUpdateServiceStatus) {
				new QuestionsTask().execute(CONSTANTS.BASE_URL);
			}
			ArrayList<ScCheckPoints> check = new ArrayList<ScCheckPoints>();

			if (Resources.getResources().getCheckPointModelArray().size() > 0) {

				check.addAll(Resources.getResources().getCheckPointModelArray());
			}

			try {

				items.add(new SectionItem("Services"));
				if (vector.size() > 0 || vector.equals(null)) {
					for (int s = 0; s < vector.size(); s++) {

						items.add(new EntryItem(vector.get(s).serviceId, vector
								.get(s).model, vector.get(s).description,
								vector.get(s).status,
								vector.get(s).ticketServiceId,
								vector.get(s).time, true, "", "", false));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("Check Fragment", "Check Fragment --" + check.toString());
			items.add(new SectionItem("Check Points"));
			if (check.size() > 0) {
				for (int c = 0; c < check.size(); c++) {
					items.add(new EntryItem("", "", check.get(c).description,
							"", "", check.get(c).time, false,
							check.get(c).checkpoint_id, check.get(c).qr_code,
							check.get(c).isTrue));
				}
			}

			Resources.getResources().setItemList(items);
			EntryAdapter adapter = new EntryAdapter(getActivity(), items);
			checkList.setAdapter(adapter);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Ticket Extra Info");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	// //////////////////ASYNC TASK//////////////////
	private class QuestionsTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCQuestionsInfo> vector;

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("RESET PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));
				listParams
						.add(new BasicNameValuePair("asset_id", tInfo.assetId));
				// listParams.add(new BasicNameValuePair("action",
				// "show_questions"));
				// new api
				listParams.add(new BasicNameValuePair("action",
						"show_questions1"));
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				Resources.getResources().setQuestionsSubmitted(true);
				Resources.getResources().setQuestionsData(null);

				Object json = new JSONTokener(response).nextValue();
				if (json instanceof JSONObject) {
					JSONObject jobj = new JSONObject(response);
					String val = jobj.getString("error");
					Log.v("error value", "error value" + val);

				} else if (json instanceof JSONArray) {
					JSONArray mainArr = new JSONArray(response);
					if (mainArr != null && mainArr.length() > 0) {
						vector = new Vector<SCQuestionsInfo>();
						for (int j = 0; j < mainArr.length(); j++) {
							SCQuestionsInfo qInfo = new SCQuestionsInfo();
							qInfo.questionId = mainArr.getJSONObject(j)
									.getString("quest_id");
							qInfo.question = mainArr.getJSONObject(j)
									.getString("question");
							qInfo.questionTypeId = mainArr.getJSONObject(j)
									.getString("quest_type_id");
							qInfo.questionAnswer = mainArr.getJSONObject(j)
									.getString("q_answer");
							Log.i("QUESTION> " + qInfo.question, "TYPE> "
									+ qInfo.questionTypeId);
							if (mainArr.getJSONObject(j).has("answers")) {

//							JSONObject answer = mainArr.getJSONObject(j)
//									.getJSONObject("answers");
//							JSONArray jArr;
//							if (answer == null) {
							JSONArray	jArr = mainArr.getJSONObject(j).optJSONArray(
										"answers");

								// if ( mainArr.getJSONObject(j)
								// .getJSONObject("answers").getClass().isArray()
								// ) {
								// // if ( mainArr.getJSONObject(j)
								// .getJSONObject("answers") != null) {

								// JSONArray jArr = mainArr.getJSONObject(j)
								// .getJSONArray("answers");
								if (jArr != null && jArr.length() > 0) {
									String[] answers = new String[jArr.length()];
									for (int i = 0; i < jArr.length(); i++) {
										String value = jArr.getString(i);
										answers[i] = value;
										qInfo.answers = answers;
									}
								}
								// }
							} else {
								Resources.getResources().setQuestionsSubmitted(
										false);
							}
							vector.add(qInfo);
						}
						Resources.getResources().setQuestionsData(vector);
					}
					return true;
				}
			} catch (JSONException e) {
				Log.e("Exception", e.getMessage(), e);
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
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Questions");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        return;
	    }
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}
	
}
