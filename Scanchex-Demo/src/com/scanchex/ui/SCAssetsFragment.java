package com.scanchex.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
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
	private Button ScanTicketButton, SuspendTickectButton, CloseButton;
	ListView checkList;
	TextView emptyText;
	public static int countCheckPoint = 0;
	ListView services;
	ArrayList<Item> items = new ArrayList<Item>();
	int scanArraySize = 0;
	String reasonvalue, curTime, ticketStatus, objvalue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isAfterUpdateServiceStatus = false;
		tInfo = Resources.getResources().getAssetTicketInfo();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setLenient(false);

		Date curDate = new Date();
		long curMillis = curDate.getTime();
		curTime = formatter.format(curDate);
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
		CloseButton = (Button) view.findViewById(R.id.close_button);
		assetImage = (ImageView) view.findViewById(R.id.asset_image_id);
		ScanTicketButton = (Button) view.findViewById(R.id.scan_button);
		SuspendTickectButton = (Button) view.findViewById(R.id.suspend_button);
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
		SuspendTickectButton.setOnClickListener(this);
		SuspendTickectButton.setVisibility(View.GONE);
		CloseButton.setVisibility(View.GONE);
		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));

		ScanTicketButton.setOnClickListener(this);

		checkList.setEmptyView(view.findViewById(R.id.textViewEmpty));
		CloseButton.setOnClickListener(this);
		if (Resources.getResources().getTicketExtraData() == null) {
			new TicketExtraTask().execute(CONSTANTS.BASE_URL);
		} else {
			EntryAdapter adapter = new EntryAdapter(getActivity(), Resources
					.getResources().getItemList());
			checkList.setAdapter(adapter);
			//setListViewHeightBasedOnChildren(checkList);
			EnableCloseButton();
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
		} else if (v == CloseButton) {
			if (Resources.getResources().isFirstScanDone()) {
				if (Resources.getResources().isCloseTicket()) {
					new AlertDialog.Builder(getActivity())
							.setIcon(R.drawable.message_info_icon)
							.setTitle("Info")
							.setMessage(
									"Are you sure, you want to close a ticket?")
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// getActivity().finish();
										}
									})
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											scanArraySize = Resources
													.getResources()
													.getCheckPointModelArray()
													.size();
											int count = Resources
													.getResources()
													.getTotalCheckPointScans();
											AssetsTicketsInfo tInfo = Resources
													.getResources()
													.getAssetTicketInfo();
											if (Resources.getResources()
													.isQuestionsSubmitted()
													|| Resources.getResources()
															.getQuestionsData() == null) {
												if (count == scanArraySize
														&& Resources
																.getResources()
																.isCorrectTicket()) {
													if (tInfo.ticketNumberOfScans == Resources
															.getResources()
															.getTotalScans()) {

														new CloseTicketTask()
																.execute(CONSTANTS.BASE_URL);
													} else {

														new AlertDialog.Builder(
																getActivity())
																.setIcon(
																		R.drawable.message_info_icon)
																.setTitle(
																		"Info")
																.setMessage(
																		"Double scan required before close ticket. Do you want another scan?")
																.setNegativeButton(
																		"No",
																		new DialogInterface.OnClickListener() {

																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {

																			}
																		})
																.setPositiveButton(
																		"Yes",
																		new DialogInterface.OnClickListener() {

																			@Override
																			public void onClick(
																					DialogInterface dialog,
																					int which) {
																				Resources
																						.getResources()
																						.setForDoubleScan(
																								true);
																				Intent i = new Intent(
																						getActivity(),
																						SCCameraPeviewScreen.class);
																				startActivity(i);
																			}
																		})
																.show();

													}
												} else {
													showInfoAlert("Info",
															"Please scan all tickets first");
												}
											} else {
												showInfoAlert("Info",
														"Please submit answers of all question first");
											}
										}
									}).show();
				} else {
					getActivity().finish();
				}
			} else {
				getActivity().finish();
			}

		} else if (v == SuspendTickectButton) {
			showSuspendAlert("Info", "Do want to suspend the ticket");
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (Resources.getResources().isFirstScanDone()) {
			// ((SCDetailsFragmentScreen) getActivity()).updateName();
			SuspendTickectButton.setVisibility(View.VISIBLE);
			CloseButton.setVisibility(View.VISIBLE);
			ticketStatus = tInfo.ticketStatus;
			EnableCloseButton();
		//	Toast.makeText(getActivity(), "Ticket status" + ticketStatus,
		//			Toast.LENGTH_LONG).show();
			if (ticketStatus.equals("suspended")) {
				new RestartTickectTask().execute(CONSTANTS.BASE_URL);

			}

		}

	}

	public void showSuspendAlert(String title, String message) {
		new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.message_info_icon)
				.setTitle("Info")
				.setMessage("Do you want to suspend the ticket ?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								LayoutInflater layoutInflater = LayoutInflater
										.from(getActivity());
								View promptView = layoutInflater.inflate(
										R.layout.sc_question_popup, null);
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										getActivity());
								alertDialogBuilder.setView(promptView);
								alertDialogBuilder.setCancelable(false);
								final AlertDialog alert = alertDialogBuilder
										.create();
								final TextView Titletext = (TextView) promptView
										.findViewById(R.id.questionId);
								final EditText changedAnswerView = (EditText) promptView
										.findViewById(R.id.questionAnswerId);

								final Button okbutton = (Button) promptView
										.findViewById(R.id.okButton);
								final Button cancelbutton = (Button) promptView
										.findViewById(R.id.cancelButton);
								Titletext.setText("Enter reason for suspending the ticket");
								okbutton.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										alert.dismiss();
										reasonvalue = changedAnswerView
												.getText().toString();
										new SuspendTask()
												.execute(CONSTANTS.BASE_URL);

									}
								});

								cancelbutton
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												alert.dismiss();

											}
										});

								// create an alert dialog

								alert.show();

							}

						}).show();

	}

	public void showInfoAlert(String title, String message) {
		new AlertDialog.Builder(getActivity()).setTitle(title)
				.setMessage(message).setIcon(R.drawable.info_icon)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

	}

	private class SuspendTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {

			try {

				Log.i("Close Ticket URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));

				listParams.add(new BasicNameValuePair("user_id", SCPreferences
						.getPreferences().getUserName(getActivity())));

				listParams.add(new BasicNameValuePair("master_id",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));

				listParams.add(new BasicNameValuePair("stop_reason",
						reasonvalue));
				listParams.add(new BasicNameValuePair("action",
						"suspend_ticket"));
				listParams.add(new BasicNameValuePair("stop_time", curTime));
				Log.v("Suspend values",
						"suspend values"
								+ tInfo.ticketTableId
								+ "\t"
								+ SCPreferences.getPreferences().getUserName(
										getActivity())
								+ "\t"
								+ SCPreferences.getPreferences()
										.getUserMasterKey(getActivity()) + "\t"
								+ reasonvalue + "\t" + curTime);
				response = new HttpWorker().getData(params[0], listParams);
				// response = response.substring(3);
				Log.i("RESPONSE", "Suspend Ticket Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				objvalue = obj.getString("msg");
				Log.i("objvalue RESPONSE", "objvalue Ticket Resp>> " + objvalue);
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
			// if (result) {
			// Intent in = new Intent(SCDetailsFragmentScreen.this,
			// ScPaynowScreen.class);
			// in.putExtra("ticketId", tInfo.ticketId);
			// startActivity(in);
			// }

			if (objvalue.equals("Ticket Stopped Successfully!")) {

				Resources.getResources().getAssetTicketInfo()
						.setTicketStatus("suspended");
			}
			getActivity().finish();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Suspend Ticket");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	private class RestartTickectTask extends
			AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {

			try {

				Log.i("Close Ticket URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));

				listParams.add(new BasicNameValuePair("user_id", SCPreferences
						.getPreferences().getUserName(getActivity())));

				listParams.add(new BasicNameValuePair("master_id",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));

				// listParams.add(new BasicNameValuePair("stop_reason",
				// reasonvalue));
				listParams.add(new BasicNameValuePair("action",
						"restart_ticket"));
				listParams.add(new BasicNameValuePair("restart_time", curTime));
				Log.v("Suspend values",
						"suspend values"
								+ tInfo.ticketTableId
								+ "\t"
								+ SCPreferences.getPreferences().getUserName(
										getActivity())
								+ "\t"
								+ SCPreferences.getPreferences()
										.getUserMasterKey(getActivity()) + "\t"
								+ curTime);
				response = new HttpWorker().getData(params[0], listParams);
				// response = response.substring(3);
				Log.i("RESPONSE", "restart Ticket Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				objvalue = obj.getString("msg");
				Log.i("RESPONSE", "restart Ticket Resp>> " + response);
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
			// if (result) {
			// Intent in = new Intent(SCDetailsFragmentScreen.this,
			// ScPaynowScreen.class);
			// in.putExtra("ticketId", tInfo.ticketId);
			// startActivity(in);
			// }
			// getActivity().finish();

			if (objvalue.equals("Ticket Started Successfully!")) {
				Resources.getResources().getAssetTicketInfo()
						.setTicketStatus("pending");
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Restart Ticket");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	// ////////////CloseTicketTask///////////////////////////////////

	private class CloseTicketTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		private String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {

			try {

				Log.i("Close Ticket URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams.add(new BasicNameValuePair("history_id", Resources
						.getResources().getTicketHistoryId()));

				listParams.add(new BasicNameValuePair("employee", SCPreferences
						.getPreferences().getUserName(getActivity())));

				listParams.add(new BasicNameValuePair("master_id",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));

				listParams.add(new BasicNameValuePair("action",
						"close_scan_ticket"));
				response = new HttpWorker().getData(params[0], listParams);
				// response = response.substring(3);
				Log.i("RESPONSE", "Close Ticket Resp>> " + response);
				JSONObject obj = new JSONObject(response);
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
			if (result) {
				Intent in = new Intent(getActivity(), ScPaynowScreen.class);
				in.putExtra("ticketId", tInfo.ticketId);
				startActivity(in);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Closing Ticket");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
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
			EnableCloseButton();
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
	
	@SuppressLint("NewApi") public void EnableCloseButton() {
		CloseButton.setClickable(false);
		CloseButton.setAlpha((float) 0.5);
		
		
		scanArraySize = Resources
				.getResources()
				.getCheckPointModelArray()
				.size();
		int count = Resources
				.getResources()
				.getTotalCheckPointScans();
		AssetsTicketsInfo tInfo = Resources
				.getResources()
				.getAssetTicketInfo();
		if (Resources.getResources()
				.isQuestionsSubmitted()
				|| Resources.getResources()
				.getQuestionsData() == null) {
			if (count == scanArraySize
					&& Resources
					.getResources()
					.isCorrectTicket()) {
				if (tInfo.ticketNumberOfScans == Resources
						.getResources()
						.getTotalScans()) {
					CloseButton.setClickable(true);
					CloseButton.setAlpha((float) 1.0);

				} 
			}

		}

	}
	
}
