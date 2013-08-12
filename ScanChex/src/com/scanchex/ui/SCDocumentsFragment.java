package com.scanchex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.scanchex.adapters.SCDocumentsListAdapter;
import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.bo.SCDocumentInfo;
import com.scanchex.network.HttpWorker;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCDocumentsFragment extends ListFragment {

	private TextView assetId;
	private TextView assetDescription;
	private TextView assetAddress;
	AssetsTicketsInfo tInfo;
	SCDocumentsListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SCDocumentsListAdapter(getActivity(), Resources.getResources().getDocumentsData());
		setListAdapter(adapter);
		tInfo = Resources.getResources().getAssetTicketInfo();
		if(Resources.getResources().getDocumentsData()==null){
			new DocumentsTask().execute(CONSTANTS.BASE_URL);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.sc_documentsfragment_screen,container, false);
		tInfo = Resources.getResources().getAssetTicketInfo();
		assetId = (TextView) view.findViewById(R.id.asset_id);
		assetDescription = (TextView) view.findViewById(R.id.des_id);
		assetAddress = (TextView) view.findViewById(R.id.add_id);
		assetId.setText(tInfo.assetUNAssetId);
		assetDescription.setText(tInfo.assetDescription);
		assetAddress.setText(tInfo.addressStreet + "\n" + tInfo.addressCity
				+ "," + tInfo.addressState + " " + tInfo.addressPostalCode);

		return view;
	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		SCDocumentInfo dInfo = (SCDocumentInfo)lv.getItemAtPosition(position);
		/*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dInfo.documentUrl));
		startActivity(browserIntent);*/
		/*Intent pdf = new Intent(getActivity(), TestShowPDF.class);
		pdf.putExtra("PATH", dInfo.documentUrl);
		startActivity(pdf);*/
		
		final String googleDocsUrl = "http://docs.google.com/viewer?url=";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(googleDocsUrl + dInfo.documentUrl), "text/html");
		startActivity(intent);
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
				listParams.add(new BasicNameValuePair("master_key",SCPreferences.getPreferences().getUserMasterKey(getActivity())));
				listParams.add(new BasicNameValuePair("asset_id", tInfo.assetId));
//				listParams.add(new BasicNameValuePair("asset_id", "4"));
				listParams.add(new BasicNameValuePair("action","show_documents"));
				response = new HttpWorker().getData(params[0], listParams);
				response = response.substring(3);
				Log.i("RESPONSE", "Login Resp>> " + response);
				JSONArray jArr = new JSONArray(response);
				vector = new Vector<SCDocumentInfo>();
				if (jArr != null && jArr.length() > 0) {
					for (int i = 0; i < jArr.length(); i++) {
						SCDocumentInfo docInfo = new SCDocumentInfo();
						JSONObject jObj = jArr.getJSONObject(i);
						docInfo.documentSubject = jObj.getString("subject");
						docInfo.documentUrl = jObj.getString("link");
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

}
