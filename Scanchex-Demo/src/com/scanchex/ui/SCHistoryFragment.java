package com.scanchex.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCHistoryInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCHistoryFragment extends Fragment implements OnClickListener {

	// private TextView assetId;
	// private TextView assetDescription;
	// private TextView assetAddress;
	private TextView textViewTitle, textViewModel, textViewSerial,
			textViewInstalled, textViewLastService, textViewTechnician;
	AssetsTicketsInfo tInfo;
	private SCHistoryListAdapter adapter;
	ImageView imageView1;
	private Button ScanTicketButton, SuspendTickectButton, CloseButton;

	String reasonvalue, curTime, ticketStatus, objvalue;
	ListView history;
	String tickectid;
	int scanArraySize = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tInfo = Resources.getResources().getAssetTicketInfo();
		// if (Resources.getResources().getHistoryData() == null) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setLenient(false);

		Date curDate = new Date();
		long curMillis = curDate.getTime();
		curTime = formatter.format(curDate);
		new HistoryTask().execute(CONSTANTS.BASE_URL);
		// }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sc_historyfragment_screen,
				container, false);
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.historyScreen);
		layout.setBackgroundColor(SCPreferences.getColor(getActivity()));
		ScanTicketButton = (Button) view.findViewById(R.id.scan_button);
		SuspendTickectButton = (Button) view.findViewById(R.id.suspend_button);
		textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
		textViewModel = (TextView) view.findViewById(R.id.textViewModel);
		textViewSerial = (TextView) view.findViewById(R.id.textViewSerial);
		CloseButton = (Button) view.findViewById(R.id.close_button);
		textViewInstalled = (TextView) view
				.findViewById(R.id.textViewInstalled);
		textViewLastService = (TextView) view
				.findViewById(R.id.textViewLastService);
		textViewTechnician = (TextView) view
				.findViewById(R.id.textViewTechnician);
		imageView1 = (ImageView) view.findViewById(R.id.imageView1);
		SuspendTickectButton.setVisibility(View.GONE);
		history = (ListView) view.findViewById(R.id.history);
		history.setEmptyView(view.findViewById(R.id.textViewEmpty));
		adapter = new SCHistoryListAdapter(getActivity(), Resources
				.getResources().getHistoryData());
		history.setAdapter(adapter);

		ScanTicketButton.setOnClickListener(this);
		CloseButton.setOnClickListener(this);
		SuspendTickectButton.setOnClickListener(this);
		SuspendTickectButton.setVisibility(View.GONE);

		CloseButton.setVisibility(View.GONE);
		return view;
	}

	// //////////////////ADAPTER////////////////////

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("On Resume Called", "ASSETS!!");
		if (Resources.getResources().isFirstScanDone()) {
			// ((SCDetailsFragmentScreen) getActivity()).updateName();
			SuspendTickectButton.setVisibility(View.VISIBLE);
			CloseButton.setVisibility(View.VISIBLE);
ticketStatus = tInfo.ticketStatus;
			if (ticketStatus.equals("suspended")) {
				new RestartTickectTask().execute(CONSTANTS.BASE_URL);
}
			EnableCloseButton();
			
		}
	}

	@Override
	public void onClick(View v) {
		if (v == ScanTicketButton) {

			if (v == ScanTicketButton) {
				long current = System.currentTimeMillis();
				long start = Resources.getResources().getTimeToStartTicket();
				if (current == start || current >= start) {
					Resources.getResources().setFirstScanDone(false);
					Intent i = new Intent(getActivity(),
							SCCameraPeviewScreen.class);
					startActivity(i);
				} else {
					Toast.makeText(getActivity(), "Ticket time is not started",
							Toast.LENGTH_LONG).show();
				}
			}

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

	public void showInfoAlert(String title, String message) {
		new AlertDialog.Builder(getActivity()).setTitle(title)
				.setMessage(message).setIcon(R.drawable.info_icon)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

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
								Titletext.setText("Enter reason for suspend");
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
				listParams.add(new BasicNameValuePair("action",
						"restart_ticket"));
				listParams.add(new BasicNameValuePair("restart_time", curTime));
				response = new HttpWorker().getData(params[0], listParams);

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

	class SCHistoryListAdapter extends BaseAdapter implements OnClickListener {

		private LayoutInflater mInflater;
		private Context context;
		private Vector<SCHistoryInfo> vector;

		public SCHistoryListAdapter(Context context,
				Vector<SCHistoryInfo> vector) {
			mInflater = LayoutInflater.from(context);
			this.context = context;
			this.vector = vector;
		}

		@Override
		public int getCount() {
			if (vector != null && this.vector.size() > 0) {
				return this.vector.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {

			return vector.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.sc_history_row_new,
						parent, false);
				holder = new ViewHolder();
				holder.textView1 = (TextView) convertView
						.findViewById(R.id.text1);
				holder.textView2 = (TextView) convertView
						.findViewById(R.id.text2);

				holder.statusImage = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				holder.notesImage = (ImageView) convertView
						.findViewById(R.id.notes_image);
				holder.photoImage = (ImageView) convertView
						.findViewById(R.id.photo_image);
				holder.audioImage = (ImageView) convertView
						.findViewById(R.id.audio_image);
				holder.videoImage = (ImageView) convertView
						.findViewById(R.id.video_image);

				holder.textViewNotes = (TextView) convertView
						.findViewById(R.id.textViewNotes);
				holder.textViewImage = (TextView) convertView
						.findViewById(R.id.textViewImage);
				holder.textViewAudio = (TextView) convertView
						.findViewById(R.id.textViewAudio);
				holder.textViewVideo = (TextView) convertView
						.findViewById(R.id.textViewVideo);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if (position % 2 == 0) {
				convertView.setBackgroundColor(this.context.getResources()
						.getColor(R.color.white));
			} else {
				convertView.setBackgroundColor(this.context.getResources()
						.getColor(R.color.grey));
			}

			SCHistoryInfo hisInfo = vector.get(position);

			holder.textView1.setText(hisInfo.historyTicket + "\n"
					+ hisInfo.historyDate);
			holder.textView2.setText(  hisInfo.historyFullName);
			holder.textView1.setTextSize(10);
			holder.textView2.setTextSize(10); 

			holder.statusImage.setVisibility(View.VISIBLE);
			holder.textView1.setTextColor(this.context.getResources().getColor(
					R.color.black));
			holder.textView2.setTextColor(this.context.getResources().getColor(
					R.color.black));

			if (hisInfo.historyWarrenty != null
					&& hisInfo.historyWarrenty.equalsIgnoreCase("Y")) {
				holder.statusImage
						.setButtonDrawable(R.drawable.ticket_status_select);
			} else {
				//holder.statusImage
				//		.setButtonDrawable(R.drawable.ticket_status_unselect);
			}

			if (hisInfo.historyNotesCount != null
					&& hisInfo.historyNotesCount.length > 0) {
				holder.notesImage.setVisibility(View.VISIBLE);
				holder.textViewNotes.setVisibility(View.VISIBLE);
				holder.textViewNotes.setText(hisInfo.historyNotesCount.length
						+ "");
			} else {
				holder.notesImage.setVisibility(View.INVISIBLE);
				holder.textViewNotes.setVisibility(View.GONE);
			}

			if (hisInfo.historyImagesCount != null
					&& hisInfo.historyImagesCount.length > 0) {
				holder.photoImage.setVisibility(View.VISIBLE);
				holder.textViewImage.setVisibility(View.VISIBLE);
				holder.textViewImage.setText(hisInfo.historyImagesCount.length
						+ "");
			} else {
				holder.photoImage.setVisibility(View.INVISIBLE);
				holder.textViewImage.setVisibility(View.INVISIBLE);
			}

			if (hisInfo.historyVoiceCount != null
					&& hisInfo.historyVoiceCount.length > 0) {
				holder.audioImage.setVisibility(View.VISIBLE);
				holder.textViewAudio.setVisibility(View.VISIBLE);
				holder.textViewAudio.setText(hisInfo.historyVoiceCount.length
						+ "");
			} else {
				holder.audioImage.setVisibility(View.INVISIBLE);
				holder.textViewAudio.setVisibility(View.INVISIBLE);
			}

			if (hisInfo.historyVideoCount != null
					&& hisInfo.historyVideoCount.length > 0) {
				holder.videoImage.setVisibility(View.VISIBLE);
				holder.textViewVideo.setVisibility(View.VISIBLE);
				holder.textViewVideo.setText(hisInfo.historyVideoCount.length
						+ "");
			} else {
				holder.videoImage.setVisibility(View.INVISIBLE);
				holder.textViewVideo.setVisibility(View.INVISIBLE);
			}

			holder.notesImage.setOnClickListener(this);
			holder.photoImage.setOnClickListener(this);
			holder.audioImage.setOnClickListener(this);
			holder.videoImage.setOnClickListener(this);

			holder.notesImage.setTag(position);
			holder.photoImage.setTag(position);
			holder.audioImage.setTag(position);
			holder.videoImage.setTag(position);

			return convertView;
		}

		class ViewHolder {

			RelativeLayout layout;
			TextView textView1;
			TextView textView2;
			CheckBox statusImage;

			ImageView notesImage;
			ImageView photoImage;
			ImageView audioImage;
			ImageView videoImage;

			// counter
			TextView textViewNotes;
			TextView textViewImage;
			TextView textViewAudio;
			TextView textViewVideo;

			/*
			 * TextView notesImage; TextView photoImage; TextView audioImage;
			 * TextView videoImage;
			 */

		}

		public void setExtraInfo(Vector<SCHistoryInfo> vector) {
			this.vector = vector;
		}

		@Override
		public void onClick(View view) {

			int id = view.getId();

			if (id == R.id.notes_image) {

				Intent i = new Intent(getActivity(),
						SCHistoryNotesViewScreen.class);
				i.putExtra(
						"PATH",
						vector.get(Integer.parseInt("" + view.getTag())).historyNotesCount);

				i.putExtra(
						"tickectid",
						vector.get(Integer.parseInt("" + view.getTag())).historyTicket);
				Log.v("tickect val in notes ",
						"tickect val in notes"
								+ vector.get(Integer.parseInt(""
										+ view.getTag())).historyTicket);

				startActivity(i);

			} else if (id == R.id.photo_image) {

				Intent i = new Intent(getActivity(),
						SCHistoryImageViewScreen.class);
				i.putExtra(
						"PATH",
						vector.get(Integer.parseInt("" + view.getTag())).historyImagesCount);
				i.putExtra(
						"tickectid",
						vector.get(Integer.parseInt("" + view.getTag())).historyTicket);
				startActivity(i);
			} else if (id == R.id.audio_image) {

				tickectid = vector.get(Integer.parseInt("" + view.getTag())).historyTicket;
				String[] arr = vector.get(Integer.parseInt("" + view.getTag())).historyVoiceCount;
				String[] audiNames = new String[arr.length];
				for (int i = 0; i < arr.length; i++) {
					audiNames[i] = "Audio " + (i + 1);
				}

				showListAlert("History Audios", true, audiNames, arr, tickectid);
			} else if (id == R.id.video_image) {

				// Intent i = new Intent(getActivity(),
				// SCVideoPlayScreen.class);
				// i.putExtra("PATH",
				// vector.get(Integer.parseInt(""+view.getTag())).historyVideo);
				// startActivity(i);
				tickectid = vector.get(Integer.parseInt("" + view.getTag())).historyTicket;
				String[] arr = vector.get(Integer.parseInt("" + view.getTag())).historyVideoCount;
				String[] vidNames = new String[arr.length];
				for (int i = 0; i < vidNames.length; i++) {
					vidNames[i] = "Video " + (i + 1);
				}

				showListAlertvideo("History Videos", true, vidNames, arr, tickectid);

			}

		}

	}

	// //////////////////ASYNC TASK//////////////////
	private class HistoryTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCHistoryInfo> vector;

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
				// listParams.add(new BasicNameValuePair("asset_id", "4"));
				listParams
						.add(new BasicNameValuePair("action", "show_history"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);

				Log.i("RESPONSE", "Login Resp>> " + response);

				JSONArray jArr = new JSONArray(response);
				vector = new Vector<SCHistoryInfo>();

				if (jArr != null && jArr.length() > 0) {
					JSONObject getData = new JSONObject();
					for (int i = 0; i < jArr.length(); i++) {
						SCHistoryInfo hisInfo = new SCHistoryInfo();
						JSONObject jObj = jArr.getJSONObject(i);
						hisInfo.historyDescription = jObj
								.getString("asset_description");
						hisInfo.historyDate = jObj.getString("date");
						hisInfo.historyTechnician = jObj
								.getString("technician");
						hisInfo.historyTicket = jObj
								.getString("ticket_id");
						hisInfo.historyService = jObj
								.getString("last_service_date");
						hisInfo.historyWarrenty = jObj.getString("warranty");
						hisInfo.historySerialNumber = jObj
								.getString("serial_number");
						hisInfo.historyModel = jObj.getString("model_number");
						hisInfo.historyFullName = jObj.getString("full_name");

						JSONArray imgJarr = jObj.getJSONArray("images");
						if (imgJarr != null && imgJarr.length() > 0) {
							String[] images = new String[imgJarr.length()];
							for (int img = 0; img < imgJarr.length(); img++) {
								// imgJarr = imgJarr.getJSONArray(img);
								getData = imgJarr.getJSONObject(img);
								images[img] = getData.getString("image");
							}
							hisInfo.historyImagesCount = images;
						}

						JSONArray notJarr = jObj.getJSONArray("notes");
						if (notJarr != null && notJarr.length() > 0) {
							String[] notes = new String[notJarr.length()];
							for (int not = 0; not < notJarr.length(); not++) {
								getData = notJarr.getJSONObject(not);
								notes[not] = getData.getString("note");
							}
							hisInfo.historyNotesCount = notes;
						}

						JSONArray vocJarr = jObj.getJSONArray("voices");
						if (vocJarr != null && vocJarr.length() > 0) {
							String[] voices = new String[vocJarr.length()];
							for (int voc = 0; voc < vocJarr.length(); voc++) {
								getData = vocJarr.getJSONObject(voc);
								voices[voc] = getData.getString("audio");
							}
							hisInfo.historyVoiceCount = voices;
						}

						JSONArray vidsJarr = jObj.getJSONArray("videos");
						if (vidsJarr != null && vidsJarr.length() > 0) {
							String[] videos = new String[vidsJarr.length()];
							for (int vid = 0; vid < vidsJarr.length(); vid++) {
								getData = vidsJarr.getJSONObject(vid);
								videos[vid] = getData.getString("video");
							}
							hisInfo.historyVideoCount = videos;
						}

						vector.add(hisInfo);
						Resources.getResources().setHistoryData(vector);
						adapter.setExtraInfo(vector);
					}
				}
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
			Resources.getResources().setHistoryData(vector);
			adapter.notifyDataSetChanged();
			if (vector.size() > 0) {
				textViewTitle.setText(vector.get(0).historyDescription);
				textViewModel.setText(vector.get(0).historyModel);
				textViewSerial.setText(vector.get(0).historySerialNumber);
				textViewInstalled.setText(vector.get(0).historyDate);
				textViewLastService.setText(vector.get(0).historyService);
				textViewTechnician.setText(vector.get(0).historyFullName);

				try {
					Picasso.with(getActivity()) //
							.load(tInfo.assetPhotoUrl) //
							.placeholder(R.drawable.scan_chexs_logo) //
							.error(R.drawable.app_icon) //
							.into(imageView1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("History Info");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	public void showListAlert(String title, final boolean isAudio,
			final String[] nameArr, final String[] pathArr,
			final String ticketid) {
		Log.v("tickect val in function", "tickect val in function" + ticketid);
		final String tickect = ticketid;
		Log.v("tickect val in variable", "tickect val in variable" + tickect);
		new AlertDialog.Builder(getActivity()).setIcon(R.drawable.info_icon)
				.setTitle(title)
				.setItems(nameArr, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (isAudio) {

							Intent i = new Intent(getActivity(),
									SCAudioPlayer.class);
							i.putExtra("PATH", pathArr[which]);
							i.putExtra("ticketid", tickect);
							startActivity(i);
						} 
//						else {
//							Intent i = new Intent(getActivity(),
//									SCVideoPlayScreen.class);
//							i.putExtra("PATH", pathArr[which]);
//
//							i.putExtra("tickectid", tickect);
//							startActivity(i);
//						}

					}
				}).show();
		
	}

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

	public void showListAlertvideo(String title, final boolean isAudio,
			final String[] nameArr, final String[] pathArr,
			final String ticketid) {
		Log.v("tickect val in function", "tickect val in function" + ticketid);
		final String tickect = ticketid;
		Log.v("tickect val in variable", "tickect val in variable" + tickect);
		new AlertDialog.Builder(getActivity()).setIcon(R.drawable.info_icon)
				.setTitle(title)
				.setItems(nameArr, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

//						if (isAudio) {
//
//							Intent i = new Intent(getActivity(),
//									SCAudioPlayer.class);
//							i.putExtra("PATH", pathArr[which]);
//							i.putExtra("ticketid", tickect);
//							startActivity(i);
//						} else {
							Intent i = new Intent(getActivity(),
									SCVideoPlayScreen.class);
							i.putExtra("PATH", pathArr[which]);

							i.putExtra("tickectid", tickect);
							startActivity(i);
//						}

					}
				}).show();
	}
	
	@SuppressLint("NewApi") 
	public void EnableCloseButton() {
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
