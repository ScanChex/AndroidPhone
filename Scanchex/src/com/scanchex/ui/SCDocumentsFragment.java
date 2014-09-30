package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scanchex.adapters.SCDocumentsListAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCDocumentsFragment extends ListFragment implements
		OnClickListener {

	private TextView assetId;
	private TextView assetDescription;
	private TextView assetAddress;
	AssetsTicketsInfo tInfo;
	SCDocumentsListAdapter adapter;
	private Button ScanTicketButton;
	Activity mActivity;
	String selectedPdfPath;
	String contentType;
	Uri pathName;
String ticketId = "";
	private static final String PDF_MIME_TYPE = "application/pdf";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		adapter = new SCDocumentsListAdapter(getActivity(), Resources
				.getResources().getDocumentsData());
		setListAdapter(adapter);
		tInfo = Resources.getResources().getAssetTicketInfo();
		if (Resources.getResources().getDocumentsData() == null) {
			new DocumentsTask().execute(CONSTANTS.BASE_URL);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.sc_documentsfragment_screen,
				container, false);
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.documentScreen);
		layout.setBackgroundColor(SCPreferences.getColor(getActivity()));

		tInfo = Resources.getResources().getAssetTicketInfo();
		assetId = (TextView) view.findViewById(R.id.asset_id);
		assetDescription = (TextView) view.findViewById(R.id.des_id);
		assetAddress = (TextView) view.findViewById(R.id.add_id);
		ScanTicketButton = (Button) view.findViewById(R.id.scan_button);
		assetId.setText(tInfo.assetUNAssetId);
		assetDescription.setText(tInfo.assetDescription);
		assetAddress.setText(tInfo.addressStreet + "\n" + tInfo.addressCity
				+ "," + tInfo.addressState + " " + tInfo.addressPostalCode);
		
		ticketId = tInfo.ticketId;
		
		ScanTicketButton.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("On Resume Called", "ASSETS!!");
		if (Resources.getResources().isFirstScanDone()) {
			ScanTicketButton.setVisibility(View.INVISIBLE);
			((SCDetailsFragmentScreen) getActivity()).updateName();
		}

		if (pathName != null) {
			selectedPdfPath = getPath(pathName);
			contentType = "PDF";
			new UploadTask().execute(CONSTANTS.BASE_URL);
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

		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		SCDocumentInfo dInfo = (SCDocumentInfo) lv.getItemAtPosition(position);

		if (Resources.getResources().isFirstScanDone()) {

			if (!(dInfo.fillable)) {
				String pdfUrl = dInfo.documentUrl;	
				String filename = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
				if(filename.equalsIgnoreCase(".pdf")){
					Intent pdf = new Intent(getActivity(), TestShowPDF.class);
					pdf.putExtra("PATH", dInfo.documentUrl);
					startActivity(pdf);
				} else {
					downloadAndOpenPDF(mActivity, dInfo.documentUrl);
				}
			} else {
				if (isPDFSupported(mActivity)) {
					downloadAndOpenPDF(mActivity, dInfo.documentUrl);

				}
			}

		} else {
			if (!(dInfo.fillable)) {

				String pdfUrl = dInfo.documentUrl;
				Log.i("SCDocuments", "file Url: " + pdfUrl);

				String filename = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
				if(filename.contains(".pdf")){
					Intent pdf = new Intent(getActivity(), TestShowPDF.class);
					pdf.putExtra("PATH", dInfo.documentUrl);
					startActivity(pdf);
				} else {
					downloadAndOpenPDF(mActivity, dInfo.documentUrl);
				}
			} 
			else {
				
//				Toast.makeText(getActivity(),
//						"Please Scan First to view this Document",
//						Toast.LENGTH_SHORT).show();
				if (isPDFSupported(mActivity)) {
					downloadAndOpenPDF(mActivity, dInfo.documentUrl);

				}
				
			}

		}
	}

	// //////////////////ASYNC TASK//////////////////
	private class DocumentsTask extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pdialog;
		String response;
		AssetsTicketsInfo tInfo = Resources.getResources().getAssetTicketInfo();
		Vector<SCDocumentInfo> vector;

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
				listParams.add(new BasicNameValuePair("action",
						"show_documents1"));
				response = new HttpWorker().getData(params[0], listParams);
				//response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONObject jObject = new JSONObject(response);
				JSONArray nonFillable = new JSONArray();
				JSONArray fillable = new JSONArray();
				nonFillable = jObject.getJSONArray("non_fillable");
				vector = new Vector<SCDocumentInfo>();
				if (nonFillable != null && nonFillable.length() > 0) {
					for (int i = 0; i < nonFillable.length(); i++) {
						SCDocumentInfo docInfo = new SCDocumentInfo();
						JSONObject jObj = nonFillable.getJSONObject(i);
						docInfo.documentSubject = jObj.getString("subject");
						docInfo.documentUrl = jObj.getString("link");
						docInfo.fillable = false;
						vector.add(docInfo);
						Resources.getResources().setDocumentsData(vector);
						adapter.setExtraInfo(vector);
					}
				}

				fillable = jObject.getJSONArray("fillable");

				if (fillable != null && fillable.length() > 0) {
					for (int i = 0; i < fillable.length(); i++) {
						SCDocumentInfo docInfo = new SCDocumentInfo();
						JSONObject jObj = fillable.getJSONObject(i);
						docInfo.documentSubject = jObj.getString("subject");
						docInfo.documentUrl = jObj.getString("link");
						docInfo.fillable = true;
						vector.add(docInfo);
						Resources.getResources().setDocumentsData(vector);
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
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(getActivity());
			pdialog.setCancelable(false);
			pdialog.setIcon(R.drawable.info_icon);
			pdialog.setTitle("Documents");
			pdialog.setMessage("Working...");
			pdialog.show();
		}
	}

	public String getPath(Uri uri) {

		String result;
		Cursor cursor = mActivity.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file
								// path
			result = uri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	private class UploadTask extends AsyncTask<String, Void, Boolean> {

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
					+ SCPreferences.getPreferences()
							.getUserMasterKey(mActivity)));
			nameValuePairs.add(new BasicNameValuePair("history_id", Resources
					.getResources().getTicketHistoryId()));
			// nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
			nameValuePairs.add(new BasicNameValuePair("action", "upload"));
			nameValuePairs.add(new BasicNameValuePair("type", "pdf"));
			nameValuePairs.add(new BasicNameValuePair("file", selectedPdfPath));
			nameValuePairs.add(new BasicNameValuePair("upload_array", ""));

			nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"
					+ new Date().getTime()));
			Log.i("FILE PATH TO BE UPLOADED >CTYPE>" + contentType, "<<<<>>>>>"
					+ SCImageTakenScreen.selectedImagePath);

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
			Log.i("DONE DONE", "DONE DONE");
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
			pdialog = new ProgressDialog(mActivity);
			pdialog.setCancelable(false);
			pdialog.setTitle("Uploading PDF File");
			pdialog.setMessage("Please Wait...");
			pdialog.show();

		}

		private void showAlertDialog(String title, String message) {
			new AlertDialog.Builder(mActivity)
					.setIcon(R.drawable.info_icon)
					.setTitle(title)
					.setMessage(message)
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).show();
		}
	}

	private void showAlertDialog2(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

		final TextView messageText = new TextView(mActivity);
		messageText.setGravity(Gravity.CENTER);
		final SpannableString s = new SpannableString(message);
		Linkify.addLinks(s, Linkify.WEB_URLS);
		messageText.setText(s);
		messageText.setMovementMethod(LinkMovementMethod.getInstance());

		builder.setIcon(R.drawable.info_icon).setTitle(title)
				.setView(messageText)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

		AlertDialog welcomeAlert = builder.create();
		welcomeAlert.show();

	}

	private String getContentType(Uri uri) {
		ContentResolver cR = mActivity.getContentResolver();
		return cR.getType(uri);
	}

	@SuppressLint("NewApi")
	public void downloadAndOpenPDF(final Context context, final String pdfUrl) {
		// Get filename
		String filename = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1);
		
		// timestamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		filename =  ticketId + " " + timeStamp+filename;
		// The place where the downloaded PDF file will be put
		final File tempFile = new File(
				context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
				filename);
		if (tempFile.exists()) {
			// If we have downloaded the file before, just go ahead and show it.

			Log.i("SCDocuments", "file exists.");
			openPDF(context, Uri.fromFile(tempFile));
			return;
		}

		// Show progress dialog while downloading
		final ProgressDialog progress = ProgressDialog.show(context,
				context.getString(R.string.pdf_show_local_progress_title),
				context.getString(R.string.pdf_show_local_progress_content),
				true);

		// Create the download request
		DownloadManager.Request r = new DownloadManager.Request(
				Uri.parse(pdfUrl));
		r.setDestinationInExternalFilesDir(context,
				Environment.DIRECTORY_DOWNLOADS, filename);
		final DownloadManager dm = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		BroadcastReceiver onComplete = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (!progress.isShowing()) {
					return;
				}
				context.unregisterReceiver(this);

				progress.dismiss();
				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				Cursor c = dm.query(new DownloadManager.Query()
						.setFilterById(downloadId));

				if (c.moveToFirst()) {
					int status = c.getInt(c
							.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (status == DownloadManager.STATUS_SUCCESSFUL) {
						openPDF(context, Uri.fromFile(tempFile));
					}
				}
				c.close();
			}
		};
		context.registerReceiver(onComplete, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// Enqueue the request
		dm.enqueue(r);
	}

	public void openPDF(Context context, Uri localUri) {

		String filepath = localUri.toString();
		// Log.i("SCDocuments", "filepath: " + filepath);

		int index = filepath.lastIndexOf('.');
		String extension = (filepath.substring(index + 1));

		Log.i("SCDocuments", "extension: " + extension);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);

		if (extension.equalsIgnoreCase("pdf")) {

			intent.setDataAndType(localUri, "application/pdf");

		} else if (extension.equalsIgnoreCase("xlsx")) {

			intent.setDataAndType(localUri, "application/msword");

		} else if (extension.equalsIgnoreCase("xls")) {
			intent.setDataAndType(localUri, "application/msword");

		} else if (extension.equalsIgnoreCase("docx")) {
			intent.setDataAndType(localUri, "application/msword");

		} else if (extension.equalsIgnoreCase("pptx")) {
			intent.setDataAndType(localUri, "application/msword");

		} else if (extension.equalsIgnoreCase("txt")) {

			intent.setDataAndType(localUri, "text/plain");

		} else if (extension.equalsIgnoreCase("jpg")
				|| extension.equalsIgnoreCase("jpeg")) {

			intent.setDataAndType(localUri, "image/jpeg");

		} else if (extension.equalsIgnoreCase("png")) {

			intent.setDataAndType(localUri, "image/png");

		} else if (extension.equalsIgnoreCase("gif")) {

			intent.setDataAndType(localUri, "image/gif");

		} else {

			intent.setDataAndType(localUri, "application/msword");

		}

		mActivity.startActivity(intent);

	}

	public boolean isPDFSupported(Context context) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		final File tempFile = new File(
				context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
				"test.pdf");
		i.setDataAndType(Uri.fromFile(tempFile), PDF_MIME_TYPE);
		return context.getPackageManager()
				.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY)
				.size() > 0;
	}
}
