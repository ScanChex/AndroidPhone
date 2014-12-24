package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCQuestionsFragment extends Fragment implements OnClickListener,
OnCheckedChangeListener {

	private static final int SELECT_VIDEO = 3;
	private static final int SELECT_NOTES = 2;
	private static final int SELECT_PICTURE = 4;
	private static final int SELECT_AUDIO = 5;
	private AssetsTicketsInfo tInfo;
	private Integer countnotes = 0;
	private Integer countimage = 0;
	private Integer countaudio = 0;
	private Integer countvideo = 0;
	private ImageView notes;
	private ImageView image;
	private ImageView audio;
	private ImageView video;
	private Button submitBtn;
	int scanArraySize = 0;
	private static TextView textviewnotes;
	private TextView textviewimage;
	private TextView textviewaudio;
	private TextView textviewvideo;

	private String selectedVideoPath;
	private String contentType;

	private TextView fillAnswer[];
	private RadioGroup rbGroup[];
	private JSONArray idsArray = null;
	private JSONArray ansArray = null;

	private Button ScanTicketButton, SuspendTickectButton, CloseButton;
	String reasonvalue, curTime, ticketStatus, objvalue;
	private LinearLayout layout;
	private RelativeLayout notescounterlayout, imagescounterlayout, audiocounterlayout, videocounterlayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		View view = inflater.inflate(R.layout.sc_questionsfragment_screen,
				container, false);
		LinearLayout layout1 = (LinearLayout) view
				.findViewById(R.id.questionScreen);
		layout1.setBackgroundColor(SCPreferences.getColor(getActivity()));

		ScanTicketButton = (Button) view.findViewById(R.id.scan_button);
		SuspendTickectButton = (Button) view.findViewById(R.id.suspend_button);
		CloseButton = (Button) view.findViewById(R.id.close_button);
		SuspendTickectButton.setVisibility(View.GONE);
		CloseButton.setVisibility(View.GONE);
		

		layout = (LinearLayout) view.findViewById(R.id.questions_layout_id);

		if (Resources.getResources().getQuestionsData() != null) {
			Vector<SCQuestionsInfo> vector = Resources.getResources()
					.getQuestionsData();
			fillAnswer = new TextView[vector.size()];
			rbGroup = new RadioGroup[vector.size()];
			for (int i = 0; i < vector.size(); i++) {
				SCQuestionsInfo qInfo = vector.get(i);
				if (qInfo.questionTypeId.equals("2")) {
					View view1 = inflater.inflate(
							R.layout.sc_questions_multichoice_layout,
							container, false);
					TextView question = (TextView) view1
							.findViewById(R.id.question_id);
					RadioGroup radioGroup = (RadioGroup) view1
							.findViewById(R.id.radio_gorup);
					question.setText(qInfo.question);
					String[] answers = qInfo.answers;
					Log.i("ANS LENGTH", "<><> " + answers.length);
					for (int j = 0; j < answers.length; j++) {
						RadioButton rb = new RadioButton(getActivity());
						rb.setOnClickListener(
								new OnClickListener (){
									public void onClick(View v) {
										//Your Implementaions...
										if (Resources.getResources().isFirstScanDone()) {
											v.setClickable(true);
										} else {
											Toast.makeText(getActivity(),
													"Please Scan Ticket First!",
													Toast.LENGTH_SHORT).show();
											v.setClickable(false);
										}
									}
								});
						rb.setText(answers[j]);
						rb.setTextColor(getResources().getColor(R.color.white));

						radioGroup.addView(rb);

						if (answers[j].trim().equals(
								qInfo.questionAnswer.trim())) {
							rb.toggle();
						}
					}

					//disable radio group
					for (int k = 0; k < radioGroup.getChildCount(); k++) {
						radioGroup.getChildAt(k).setClickable(false);
					}

					radioGroup.setOnCheckedChangeListener(this);


					rbGroup[i] = radioGroup;

					layout.addView(view1);
					fillAnswer[i] = null;
				} else if (qInfo.questionTypeId.equals("1")) {
					View view1 = inflater.inflate(
							R.layout.sc_questions_multichoice_layout,
							container, false);
					TextView question = (TextView) view1
							.findViewById(R.id.question_id);
					RadioGroup radioGroup = (RadioGroup) view1
							.findViewById(R.id.radio_gorup);
					question.setText(qInfo.question);

					RadioButton rbYes = new RadioButton(getActivity());
					rbYes.setText("Yes");
					rbYes.setOnClickListener(
							new OnClickListener (){
								public void onClick(View v) {
									//Your Implementaions...
									if (Resources.getResources().isFirstScanDone()) {
										v.setClickable(true);
									} else {
										Toast.makeText(getActivity(),
												"Please Scan Ticket First!",
												Toast.LENGTH_SHORT).show();
										v.setClickable(false);
									}
								}
							});
					rbYes.setTextColor(getResources().getColor(R.color.white));
					radioGroup.addView(rbYes);

					RadioButton rbNo = new RadioButton(getActivity());
					rbNo.setText("No");
					rbNo.setOnClickListener(
							new OnClickListener (){
								public void onClick(View v) {
									//Your Implementaions...
									if (Resources.getResources().isFirstScanDone()) {

									} else {
										Toast.makeText(getActivity(),
												"Please Scan Ticket First!",
												Toast.LENGTH_SHORT).show();
									}
								}
							});
					rbNo.setTextColor(getResources().getColor(R.color.white));
					radioGroup.addView(rbNo);

					//disable radio group
					for (int k = 0; k < radioGroup.getChildCount(); k++) {
						radioGroup.getChildAt(k).setClickable(false);
					}

					radioGroup.setOnCheckedChangeListener(this);

					if ( qInfo.questionAnswer.equals("true")) {
						rbYes.setChecked(true);
					} else if ( qInfo.questionAnswer.equals("false")) {
						rbNo.setChecked(true);
					}

					layout.addView(view1);
					fillAnswer[i] = null;
					rbGroup[i] = radioGroup;
				} else {
					View view1 = inflater.inflate(
							R.layout.sc_questions_infield_layout, container,
							false);

					LinearLayout llayout = (LinearLayout) view1
							.findViewById(R.id.llayout);

					TextView questionView = new TextView(getActivity());
					questionView.setLayoutParams(new LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));

					questionView.setText(qInfo.question);
					questionView.setTextColor(Color.parseColor("#FFFFFF"));

					llayout.addView(questionView);

					final TextView answerView = new TextView(getActivity());
					answerView.setLayoutParams(new LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1f));

					answerView.setText(qInfo.questionAnswer);
					answerView.setTextColor(Color.parseColor("#000000"));
					answerView
					.setBackgroundResource(R.drawable.edittext_border);
					answerView.setPadding(5, 5, 5, 5);
					llayout.addView(answerView);
					fillAnswer[i] = answerView;

					layout.addView(view1);
					rbGroup[i] = null;

					answerView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							if (Resources.getResources().isFirstScanDone()) {

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

								final EditText changedAnswerView = (EditText) promptView
										.findViewById(R.id.questionAnswerId);

								final Button okbutton = (Button) promptView
										.findViewById(R.id.okButton);
								final Button cancelbutton = (Button) promptView
										.findViewById(R.id.cancelButton);

								changedAnswerView.setText(answerView.getText());

								okbutton.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub

										String answer = changedAnswerView
												.getText().toString();
										answerView.setText(answer);
										alert.dismiss();

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

							} else {
								Toast.makeText(getActivity(),
										"Please Scan Ticket First!",
										Toast.LENGTH_SHORT).show();
							}

						}

					});

					/*
					 * TextView question = (TextView) view1
					 * .findViewById(R.id.question_id); ansField = (TextView)
					 * view1.findViewById(R.id.answer_field);
					 */
					// ansField.setText(qInfo.questionAnswer);

					// question.setText(qInfo.question);

				}
			}
		} else {
			
		}

		submitBtn = (Button) view.findViewById(R.id.question_submit_btn);
		notes = (ImageView) view.findViewById(R.id.question_notes);
		image = (ImageView) view.findViewById(R.id.question_image);
		audio = (ImageView) view.findViewById(R.id.question_audio);
		video = (ImageView) view.findViewById(R.id.question_video);

		textviewnotes = (TextView) view.findViewById(R.id.textnotes);
		textviewimage = (TextView) view.findViewById(R.id.textViewImage);
		textviewaudio = (TextView) view.findViewById(R.id.audiotextViewNotes);
		textviewvideo = (TextView) view.findViewById(R.id.textViewvideoNotes);

		textviewnotes.setVisibility(View.GONE);
		textviewimage.setVisibility(View.GONE);
		textviewaudio.setVisibility(View.GONE);
		textviewvideo.setVisibility(View.GONE);


		// Log.v("count in oncreateview", "count in oncreateview" + count);
		notes.setOnClickListener(this);
		image.setOnClickListener(this);
		audio.setOnClickListener(this);
		video.setOnClickListener(this);
		ScanTicketButton.setOnClickListener(this);
		SuspendTickectButton.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		CloseButton.setOnClickListener(this);
		return view;
	}




	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!Resources.getResources().isFirstScanDone()) {

			return true;
		} 

		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("On Resume Called", "ASSETS!!");
		if (Resources.getResources().isFirstScanDone()) {

			disableEnableControls(true, layout);
			SuspendTickectButton.setVisibility(View.VISIBLE);
			CloseButton.setVisibility(View.VISIBLE);
			EnableCloseButton();
			// for (int j = 0; j < layout.getChildCount(); j++) {
			// View v1 = layout.getChildAt(j);
			// if (v1 instanceof RadioGroup) {
			//
			// for (int k = 0; k < ((RadioGroup)v1).getChildCount(); k++) {
			// ((RadioGroup)v1).getChildAt(k).setClickable(true);
			// }
			// //v1.setClickable(true);
			// } //etc. If it fails anywhere, just return false.
			// }
			ticketStatus = tInfo.ticketStatus;
			if (ticketStatus.equals("suspended")) {
				new RestartTickectTask().execute(CONSTANTS.BASE_URL);

			}

		}

	}

	private void disableEnableControls(boolean enable, ViewGroup vg){
		for (int i = 0; i < vg.getChildCount(); i++){
			View child = vg.getChildAt(i);
			child.setClickable(enable);
			if (child instanceof ViewGroup){ 
				disableEnableControls(enable, (ViewGroup)child);
			}
		}
	}

	@Override
	public void onClick(View v) {

		if (v == notes) {

			if (Resources.getResources().isFirstScanDone()) {

				Intent intent = new Intent(getActivity(),
						SCAddCommentScreen.class);

				startActivityForResult(intent, SELECT_NOTES);

			} else {
				Toast.makeText(getActivity(), "Please Scan Ticket First!",
						Toast.LENGTH_SHORT).show();
			}

		} else if (v == image) {

			if (Resources.getResources().isFirstScanDone()) {

				Intent intent = new Intent(getActivity(),
						SCImageTakenScreen.class);
				// startActivity(intent);
				startActivityForResult(intent, SELECT_PICTURE);
			} else {
				Toast.makeText(getActivity(), "Please Scan Ticket First!",
						Toast.LENGTH_SHORT).show();
			}

		} else if (v == audio) {

			if (Resources.getResources().isFirstScanDone()) {

				Intent intent = new Intent(getActivity(),
						SCRecordingScreen.class);
				//startActivity(intent);
				startActivityForResult(intent,SELECT_AUDIO);

			} else {
				Toast.makeText(getActivity(), "Please Scan Ticket First!",
						Toast.LENGTH_SHORT).show();
			}

		} else if (v == video) {

			if (Resources.getResources().isFirstScanDone()) {

				Intent intent = new Intent(getActivity(),
						VideoCaptureActivity.class);
				startActivityForResult(intent, SELECT_VIDEO);

			} else {
				Toast.makeText(getActivity(), "Please Scan Ticket First!",
						Toast.LENGTH_SHORT).show();
			}

		} else if (v == submitBtn) {

			if (Resources.getResources().isFirstScanDone()) {

				Vector<SCQuestionsInfo> vector = Resources.getResources()
						.getQuestionsData();
				idsArray = new JSONArray();
				ansArray = new JSONArray();
				if (vector != null) {
					for (int i = 0; i < vector.size(); i++) {
						SCQuestionsInfo qInfo = vector.get(i);
						if (qInfo.questionTypeId.equals("3")
								&& fillAnswer[i] != null) {
							String myAns = fillAnswer[i].getText().toString();
							Log.i("Question ID " + qInfo.questionId, "ANSWER "
									+ myAns);
							idsArray.put(qInfo.questionId);
							if (myAns != null && myAns.length() > 0) {
								ansArray.put(myAns);
							}
						} else if ((qInfo.questionTypeId.equals("1") || qInfo.questionTypeId
								.equals("2")) && rbGroup[i] != null) {

							RadioButton btn = (RadioButton) getActivity()
									.findViewById(
											rbGroup[i]
													.getCheckedRadioButtonId());
							if (btn == null) {
								continue;
							}
							String text = btn.getText().toString();
							Log.i("Question ID " + qInfo.questionId,
									" CHECKED ANSWER " + text);
							if (text.equalsIgnoreCase("Yes")) {
								text = "true";
							} else if (text.equalsIgnoreCase("No")) {
								text = "false";
							}
							idsArray.put(qInfo.questionId);
							ansArray.put(text);
						}
					}


					Log.i("IDS", "IDS> " + idsArray.toString());
					Log.i("ANS", "ANS> " + ansArray.toString());
					if (vector.size() == ansArray.length()) {
						new UpdateAnswersTask().execute(CONSTANTS.BASE_URL);
					} else {
						showAlertDialog("Info", "Please answer all questions");
					}

				} else {
					showAlertDialog("Info", "There are no answers to submit");
				}

			} else {
				Toast.makeText(getActivity(), "Please Scan Ticket First!",
						Toast.LENGTH_SHORT).show();
			}
		} else if (v == ScanTicketButton) {

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SELECT_NOTES && data != null) {
			if (resultCode == Activity.RESULT_OK) {
				String notescount = data.getStringExtra("result");
				if (notescount.equals("newvalue")) {

					countnotes += 1;
					String countno = "";
					countno = countnotes.toString();
					textviewnotes.setText(countno);
					Log.v("count value", "count value" + countnotes);
					textviewnotes.setVisibility(View.VISIBLE);

					//
				}

			}

		} else if (requestCode == SELECT_PICTURE && data != null) {
			if (resultCode == Activity.RESULT_OK) {
				String notescount = data.getStringExtra("result");
				if (notescount.equals("newvalue")) {

					countimage += 1;
					String countno = "";
					countno = countimage.toString();
					textviewimage.setText(countno);
					Log.v("count value", "count value" + countimage);
					textviewimage.setVisibility(View.VISIBLE);



					//
				}

			}
		} else if (requestCode == SELECT_AUDIO && data != null) {
			if (resultCode == Activity.RESULT_OK) {
				String notescount = data.getStringExtra("result");
				if (notescount.equals("newvalue")) {
					// Integer count = 0;
					countaudio += 1;
					String countno = "";
					countno = countaudio.toString();
					textviewaudio.setText(countno);
					Log.v("count value", "count value" + countaudio);
					textviewaudio.setVisibility(View.VISIBLE);


					//
				}
			}
		} else if (requestCode == SELECT_VIDEO && data != null) {
			if (resultCode == Activity.RESULT_OK) {

				// Uri selectedImageUri = data.getData();
				selectedVideoPath = data.getStringExtra("result");
				// Uri selectedImageUri = Uri.parse(uriResult);
				contentType = "video/mp4";// getContentType(selectedImageUri);
				// selectedVideoPath = getRealPathFromURI(selectedImageUri);
				Log.i("VID Path", "SELECT_VIDEO Path : " + selectedVideoPath);
				new UploadVideoTask().execute(CONSTANTS.BASE_URL);
				if (selectedVideoPath.equals(selectedVideoPath)) {
					// Integer count = 0;
					countvideo += 1;
					String countno = "";
					countno = countvideo.toString();
					textviewvideo.setText(countno);
					Log.v("count value", "count value" + countvideo);
					textviewvideo.setVisibility(View.VISIBLE);

				}
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

	private String getContentType(Uri uri) {
		ContentResolver cR = getActivity().getContentResolver();
		return cR.getType(uri);
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(contentUri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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

	private class UploadVideoTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog pdialog;
		private String serverResp;

		private String status;
		private String message;

		@Override
		protected Boolean doInBackground(String... path) {

			String url = path[0];
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			Log.i("URL", "<><><>" + url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("master_key", ""
					+ SCPreferences.getPreferences().getUserMasterKey(
							getActivity())));
			nameValuePairs.add(new BasicNameValuePair("history_id", Resources
					.getResources().getTicketHistoryId()));
			Log.v("tickect val using resources in notes",
					"tickect val using resources in notes"
							+ Resources.getResources().getTicketHistoryId());
			// nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
			nameValuePairs.add(new BasicNameValuePair("action", "upload"));
			nameValuePairs.add(new BasicNameValuePair("type", "video"));
			nameValuePairs
			.add(new BasicNameValuePair("file", selectedVideoPath));
			nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"
					+ new Date().getTime()));
			Log.i("<<CTYPE>>>" + contentType, "<<<<FILE PATH >>>>>"
					+ selectedVideoPath);

			try {
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				for (int index = 0; index < nameValuePairs.size(); index++) {
					if (nameValuePairs.get(index).getName()
							.equalsIgnoreCase("file")) {
						// If the key equals to "image", we use FileBody to
						// transfer the data

						entity.addPart(nameValuePairs.get(index).getName(),
								new FileBody(new File(nameValuePairs.get(index)
										.getValue()), contentType));
					} else {
						// Normal string data
						entity.addPart(nameValuePairs.get(index).getName(),
								new StringBody(nameValuePairs.get(index)
										.getValue()));
					}
				}
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost,
						localContext);
				StringBuilder sb = null;
				String line = null;
				if (response != null) {
					InputStream in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				}
				serverResp = sb.toString();
				Log.i("SERVER RESP", "<><><>" + serverResp);
				JSONObject obj = new JSONObject(serverResp);

				if (serverResp.contains("error")) {
					status = obj.getString("error");
					return false;
				} else {
					status = obj.getString("status");
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Boolean.FALSE;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pdialog.dismiss();
			pdialog = null;
			if (result) {
				File file = new File(SCImageTakenScreen.selectedImagePath);
				boolean deleted = file.delete();
				showAlertDialog("Info", status);
			} else {
				showAlertDialog("Info", status);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setTitle("Uploading Video");
			pdialog.setMessage("Please Wait...");
			pdialog.show();

		}
	}

	private void showAlertDialog(String title, String message) {
		new AlertDialog.Builder(getActivity()).setIcon(R.drawable.info_icon)
		.setTitle(title).setMessage(message)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		}).show();
	}

	// //////////////////ASYNC TASK//////////////////
	private class UpdateAnswersTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				Log.i("ANS  PASS URL", "<><>" + params[0]);
				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
				listParams.add(new BasicNameValuePair("master_key",
						SCPreferences.getPreferences().getUserMasterKey(
								getActivity())));
				listParams.add(new BasicNameValuePair("quest_id", idsArray
						.toString()));
				listParams.add(new BasicNameValuePair("answer", ansArray
						.toString()));
				listParams.add(new BasicNameValuePair("ticket_id",
						tInfo.ticketTableId));
				listParams
				.add(new BasicNameValuePair("action", "update_answer"));
				response = new HttpWorker().getData(params[0], listParams);
				// response = response.substring(3);
				Log.i("RESPONSE", "Question Resp>> " + response);
				JSONObject obj = new JSONObject(response);
				String status = obj.getString("response");
				if (status.equalsIgnoreCase("1")) {
					return true;
				} else {
					return false;
				}
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
				showAlertDialog("Info", "Answers updated successfully");
			} else {
				showAlertDialog("Info", "Answers not updated");
			}
			Resources.getResources().setQuestionsSubmitted(true);
			EnableCloseButton();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Updating Answers");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		if (Resources.getResources().isFirstScanDone()) {
			for (int k = 0; k < group.getChildCount(); k++) {
				group.getChildAt(k).setClickable(true);
			}

		} else {
			Toast.makeText(getActivity(), "Please Scan Ticket First!",
					Toast.LENGTH_SHORT).show();
			for (int k = 0; k < group.getChildCount(); k++) {
				group.getChildAt(k).setClickable(false);
			}

		}

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
