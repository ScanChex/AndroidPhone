package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCQuestionsInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCQuestionsFragment extends Fragment implements OnClickListener{

	private static final int SELECT_VIDEO = 3;
	
	private ImageView notes;
	private ImageView image;
	private ImageView audio;
	private ImageView video;
	private Button submitBtn;
	
	private String selectedVideoPath;
	private String contentType;
	
	private EditText fillAnswer [];
	private RadioGroup rbGroup [];
	private JSONArray idsArray = null;
	private JSONArray ansArray = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 View view = inflater.inflate(R.layout.sc_questionsfragment_screen, container, false);
		 LinearLayout layout = (LinearLayout)view.findViewById(R.id.questions_layout_id);
		 if(Resources.getResources().getQuestionsData()!=null){
			 Vector<SCQuestionsInfo> vector = Resources.getResources().getQuestionsData();
			 fillAnswer = new EditText[vector.size()];
			 rbGroup = new RadioGroup[vector.size()];
			 for(int i = 0; i<vector.size(); i++){
				 SCQuestionsInfo qInfo = vector.get(i);
				 if(qInfo.questionTypeId.equals("2")){
					 View view1 = inflater.inflate(R.layout.sc_questions_multichoice_layout, container, false);
					 TextView question = (TextView)view1.findViewById(R.id.question_id);
					 RadioGroup radioGroup = (RadioGroup)view1.findViewById(R.id.radio_gorup);
					 question.setText(qInfo.question);
					 String[] answers = qInfo.answers;
					 Log.i("ANS LENGTH", "<><> "+answers.length);
					 for(int j = 0; j<answers.length; j++){
						RadioButton rb = new RadioButton(getActivity());
						rb.setText(answers[j]);
						rb.setTextColor(getResources().getColor(R.color.black));
						radioGroup.addView(rb);
					 }
					 rbGroup[i] = radioGroup;
					 layout.addView(view1);
					 fillAnswer[i] = null;
				 }else if(qInfo.questionTypeId.equals("1")){
					 View view1 = inflater.inflate(R.layout.sc_questions_multichoice_layout, container, false);
					 TextView question = (TextView)view1.findViewById(R.id.question_id);
					 RadioGroup radioGroup = (RadioGroup)view1.findViewById(R.id.radio_gorup);
					 question.setText(qInfo.question);
					 
					 RadioButton rbYes = new RadioButton(getActivity());
					 rbYes.setText("Yes");
					 rbYes.setTextColor(getResources().getColor(R.color.black));
					 radioGroup.addView(rbYes);
					 
					 RadioButton rbNo = new RadioButton(getActivity());
					 rbNo.setText("No");
					 rbNo.setTextColor(getResources().getColor(R.color.black));
					 radioGroup.addView(rbNo);
					 layout.addView(view1);
					 fillAnswer[i] = null;
					 rbGroup[i] = radioGroup;
				 }else{
					 View view1 = inflater.inflate(R.layout.sc_questions_infield_layout, container, false);
					 TextView question = (TextView)view1.findViewById(R.id.question_id);
					 EditText ansField = (EditText)view1.findViewById(R.id.answer_field);
					 fillAnswer[i] = ansField;
					 question.setText(qInfo.question);
					 layout.addView(view1);
					 rbGroup[i] = null;
				 }
			 }
		 }
		 
		 submitBtn = (Button)view.findViewById(R.id.question_submit_btn);
		 notes = (ImageView)view.findViewById(R.id.question_notes);
		 image = (ImageView)view.findViewById(R.id.question_image);
		 audio = (ImageView)view.findViewById(R.id.question_audio);
		 video = (ImageView)view.findViewById(R.id.question_video);
		 
		 notes.setOnClickListener(this);
		 image.setOnClickListener(this);
		 audio.setOnClickListener(this);
		 video.setOnClickListener(this);
		 submitBtn.setOnClickListener(this);
		 return view;
	}

	@Override
	public void onClick(View v) {
		
		if(v==notes){
			Intent intent = new Intent(getActivity(), SCAddCommentScreen.class);  
	        startActivity(intent);
		}else if(v==image){
			Intent intent = new Intent(getActivity(), SCImageTakenScreen.class);  
	        startActivity(intent);
		}else if(v==audio){
			Intent intent = new Intent(getActivity(), SCRecordingScreen.class);  
	        startActivity(intent);
		}else if(v==video){
			Toast.makeText(getActivity(), "VIDEO", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent("android.media.action.VIDEO_CAPTURE");
//	        intent.putExtra("android.intent.extra.durationLimit", 120);  
	        startActivityForResult(intent, SELECT_VIDEO);
		}else if(v==submitBtn){
			Vector<SCQuestionsInfo> vector = Resources.getResources().getQuestionsData();
			idsArray = new JSONArray();
	        ansArray = new JSONArray();
			for(int i = 0; i<vector.size(); i++){
				 SCQuestionsInfo qInfo = vector.get(i);
				 if(qInfo.questionTypeId.equals("3") && fillAnswer[i]!=null){
					 String myAns = fillAnswer[i].getText().toString();
					 Log.i("Question ID "+qInfo.questionId,"ANSWER "+myAns);
					 idsArray.put(qInfo.questionId);
					 if(myAns!=null && myAns.length()>0){
						 ansArray.put(myAns);
					 }
				 }else if((qInfo.questionTypeId.equals("1") || qInfo.questionTypeId.equals("2"))&& rbGroup[i]!=null){

					 RadioButton btn = (RadioButton)getActivity().findViewById(rbGroup[i].getCheckedRadioButtonId());
					 if(btn==null){
						 continue;
					 }
					 String text = btn.getText().toString();
					 Log.i("Question ID "+qInfo.questionId," CHECKED ANSWER "+text);
					 if(text.equalsIgnoreCase("Yes")){
						 text = "true";
					 }else if(text.equalsIgnoreCase("No")){
						 text = "false";
					 }
					 idsArray.put(qInfo.questionId);
					 ansArray.put(text);
				 }
			}
			Log.i("IDS", "IDS> "+idsArray.toString());
			Log.i("ANS", "ANS> "+ansArray.toString());
			if(vector.size()==ansArray.length()){
				new UpdateAnswersTask().execute(CONSTANTS.BASE_URL);
			}else{
				showAlertDialog("Info", "Please answer all questions");
			}
			
		}
		
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (resultCode == Activity.RESULT_OK) {

	        if (requestCode == SELECT_VIDEO){
	            System.out.println("SELECT_VIDEO");
	            Uri selectedImageUri = data.getData();
	            contentType = getContentType(selectedImageUri);
	            selectedVideoPath = getRealPathFromURI(selectedImageUri);
	            Log.i("VID Path","SELECT_VIDEO Path : " + selectedVideoPath);
	            new UploadVideoTask().execute(CONSTANTS.BASE_URL);
	        }
	   }
	}
	
	private String getContentType(Uri uri){
		ContentResolver cR = getActivity().getContentResolver();
		return cR.getType(uri);
	}
	
	public String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
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
				 Log.i("URL", "<><><>"+url);
				 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				 nameValuePairs.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(getActivity())));
				 nameValuePairs.add(new BasicNameValuePair("history_id", Resources.getResources().getTicketHistoryId()));
//				 nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
				 nameValuePairs.add(new BasicNameValuePair("action", "upload"));
				 nameValuePairs.add(new BasicNameValuePair("type", "video"));
				 nameValuePairs.add(new BasicNameValuePair("file", selectedVideoPath));
				 nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"+new Date().getTime()));
				 Log.i("<<CTYPE>>>"+contentType, "<<<<FILE PATH >>>>>"+selectedVideoPath);
				 
				 try {
					 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					 
					 for(int index=0; index < nameValuePairs.size(); index++) {
				            if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
				                // If the key equals to "image", we use FileBody to transfer the data
				            
				                entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue()),contentType));
				            } else {
				                // Normal string data
				                entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
				            }
				        }
				     httpPost.setEntity(entity);
				     HttpResponse response = httpClient.execute(httpPost, localContext);
					 StringBuilder sb=null;
					 String line = null;
					 if(response!=null){
						 InputStream in = response.getEntity().getContent();
						 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						 sb = new StringBuilder();
						 while((line = reader.readLine()) != null){
							 sb.append(line + "\n");
						 }
					 }
					 serverResp = sb.toString();
					 Log.i("SERVER RESP", "<><><>"+serverResp);	
					 JSONObject obj = new JSONObject(serverResp);
					 			
					 if(serverResp.contains("error")){
						 status = obj.getString("error");
						 return false;
					 }else{		
						 status = obj.getString("status");
						 return true;
					 }
				 }catch(Exception e){
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
		 new AlertDialog.Builder(getActivity())
		 .setIcon(R.drawable.info_icon)
		 .setTitle(title)
		 .setMessage(message)
		 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
							
			 public void onClick(DialogInterface dialog, int which) {
								
			 }
		 }).show();
	 }
	 

	 
		////////////////////ASYNC TASK//////////////////
	 private class UpdateAnswersTask extends AsyncTask<String, Integer, Boolean> {

 		private ProgressDialog pdialog;
 		String response;
 		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
 		@Override
 		protected Boolean doInBackground(String... params) {
 			try {
 				
 				Log.i("ANS  PASS URL", "<><>" + params[0]);
 				List<NameValuePair> listParams = new ArrayList<NameValuePair>();
 				listParams.add(new BasicNameValuePair("master_key", SCPreferences.getPreferences().getUserMasterKey(getActivity())));
 				listParams.add(new BasicNameValuePair("quest_id", idsArray.toString()));
 				listParams.add(new BasicNameValuePair("answer", ansArray.toString()));
 				listParams.add(new BasicNameValuePair("ticket_id", tInfo.ticketId));
 				listParams.add(new BasicNameValuePair("action", "update_answer"));
 				response = new HttpWorker().getData(params[0], listParams);
 				response = response.substring(3);
 				Log.i("RESPONSE", "Question Resp>> " + response);
 				JSONObject obj = new JSONObject(response);
 				String status = obj.getString("response");
 				if(status.equalsIgnoreCase("1")){
 					return true;
 				}else{
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
 			if(result){
 				showAlertDialog("Info", "Answers updated successfully");
 			}else{
 				showAlertDialog("Info", "Answers not updated");
 			}
 			Resources.getResources().setQuestionsSubmitted(true);
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
	 
	 
	 
	 
	 
	 	
}
