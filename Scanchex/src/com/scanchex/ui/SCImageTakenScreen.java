package com.scanchex.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scanchex.bo.AssetsTicketsInfo;
import com.scanchex.utils.CONSTANTS;
import com.scanchex.utils.Resources;
import com.scanchex.utils.SCPreferences;

public class SCImageTakenScreen extends BaseActivity{
	
	public static String selectedImagePath = "";
	private final int SELECT_PICTURE = 200;
	private static final int CAMERA_PIC_REQUEST = 1337;
	private String contentType = "";
	Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "ScanChex";
	private TextView tickectId;
	private AssetsTicketsInfo tInfo;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_imagetaken_screen);
		tickectId = (TextView) findViewById(R.id.tickect_id);
		
		tInfo = Resources.getResources().getAssetTicketInfo();
		tickectId.setText(tInfo.ticketId);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.imageTokeContainer);
		layout.setBackgroundColor((SCPreferences
				.getColor(SCImageTakenScreen.this)));

		selectedImagePath = "";
	}
	
	
	public void onCameraClick(View view) {
			
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	
	}

	public void onLibraryClick(View view) {
		
		Intent intent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//intent.setType("image/*");
		//intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent,SELECT_PICTURE);
	
	}
	
	public void onBackClick(View view) {
		
		this.finish();
	}
	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri =data.getData();
				contentType = getContentType(selectedImageUri);
				selectedImagePath = getPath(selectedImageUri);
				Log.i("IMAGE URL", "<>GALLERY<> "+ selectedImagePath);
				File imageFile = new File(selectedImagePath);
				Bitmap bm = decodeFile(imageFile, getCameraPhotoOrientation(this, selectedImageUri, selectedImagePath));
				/*try {
	                // We need to recyle unused bitmaps
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize = 8;
					InputStream stream = getContentResolver().openInputStream(data.getData());
					Bitmap preview_bitmap=BitmapFactory.decodeStream(stream,null,options);
	                stream.close();
	                img.setImageBitmap(preview_bitmap);
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }*/

			} 
			
			
			if (requestCode == CAMERA_PIC_REQUEST) {

				Uri selectedImageUri = fileUri;
				contentType = "image/jpeg";//getContentType(selectedImageUri);
				selectedImagePath = getPath(selectedImageUri);
				Log.i("IMAGE URL", "<>CAMERA<> "+ selectedImagePath);
				File imageFile = new File(selectedImagePath);
				Bitmap bm = decodeFile(imageFile, getCameraPhotoOrientation(this, selectedImageUri, selectedImagePath));
			}
			}
			if(selectedImagePath!=null && selectedImagePath.length()>0){
				new UploadTask().execute(CONSTANTS.BASE_URL);
			}
	
	}
	
 
	//decodes image and scales it to reduce memory consumption
		private Bitmap decodeFile(File f, int rotate){
		    try {
		        //Decode image size
		        BitmapFactory.Options o = new BitmapFactory.Options();
		        o.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(new FileInputStream(f),null,o);
		        //The new size we want to scale to
		        final int REQUIRED_SIZE=1000;
		        //Find the correct scale value. It should be the power of 2.
		        int scale=1;
		        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)scale*=2;
		        //Decode with inSampleSize
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize=scale;
		        Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);	        
		        Matrix matrix = new Matrix();
		        matrix.postRotate(rotate);
//		        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); 
		        
		        
		        if (bmp.getHeight()>800 && bmp.getWidth()>800 && bmp.getWidth() > bmp.getHeight()) {
		    
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, 800, 612, matrix, true); 		            
		        }else if (bmp.getHeight()>800 && bmp.getWidth()>800 && bmp.getWidth() < bmp.getHeight()){
		        	
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, 612, 800, matrix, true);		            
		        }else if(bmp.getWidth() < 612 && bmp.getHeight()>800){
		            
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth()/3, 800, matrix, true);		            
		        }else if(bmp.getWidth() >800 && bmp.getHeight()<612){
		            
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, 800, bmp.getHeight()/4, matrix, true);		            
		        }else if(bmp.getWidth() > 612 && bmp.getHeight()>800){
		            
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, 612, 800, matrix, true);		            
		        }else if(bmp.getWidth() >800 && bmp.getHeight()>612){
		            
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, 800, 612, matrix, true);
		        }else{
		        	bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		        }
		        
		        Log.i("IMAGE WIDTH "+bmp.getWidth(), "IMAGE HEIGHT "+bmp.getHeight());

		        String path = Environment.getExternalStorageDirectory().toString();
		        OutputStream fOut = null;
		        File file = new File(path, "ScanCheXTemp"+new Date().getTime()+".jpg");
		        fOut = new FileOutputStream(file);

		        bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		        fOut.flush();
		        fOut.close();

		        MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
		        Log.i("ABSOLUTE PATH", "<<<<>>>>"+file.getAbsolutePath());
		        selectedImagePath = file.getAbsolutePath();
		        return bmp;
		    } catch (FileNotFoundException e) {
		    	e.printStackTrace();
		    }	catch(IOException e){
		    	e.printStackTrace();
		    }
		    return null;
		}
		
		
		public String getPath(Uri uri) {

			  String result;
			    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			    if (cursor == null) { // Source is Dropbox or other similar local file path
			        result = uri.getPath();
			    } else { 
			        cursor.moveToFirst(); 
			        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			        result = cursor.getString(idx);
			        cursor.close();
			    }
			    return result;
		}


		public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
		     int rotate = 0;
		     try {
		         context.getContentResolver().notifyChange(imageUri, null);
		         File imageFile = new File(imagePath);
		         ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
		         int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		         switch (orientation) {
		         case ExifInterface.ORIENTATION_ROTATE_270://8
		             rotate = 270;
		             break;
		         case ExifInterface.ORIENTATION_ROTATE_180://3
		             rotate = 180;
		             break;
		         case ExifInterface.ORIENTATION_ROTATE_90://6
		             rotate = 90;
		             break;
		         }
		         Log.v("ORENTATION", "Exif orientation: " + orientation);
		     } catch (Exception e) {
		         e.printStackTrace();
		     }
		    return rotate;
		 }
		
		private String getContentType(Uri uri){
			ContentResolver cR = getContentResolver();
//			MimeTypeMap mime = MimeTypeMap.getSingleton();
			return cR.getType(uri);
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
				 Log.i("URL", "<><><>"+url);
				 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				 nameValuePairs.add(new BasicNameValuePair("master_key", ""+SCPreferences.getPreferences().getUserMasterKey(SCImageTakenScreen.this)));
				 nameValuePairs.add(new BasicNameValuePair("history_id", Resources.getResources().getTicketHistoryId()));
//				 nameValuePairs.add(new BasicNameValuePair("history_id", "97"));
				 nameValuePairs.add(new BasicNameValuePair("action", "upload"));
				 nameValuePairs.add(new BasicNameValuePair("type", "images"));
				 nameValuePairs.add(new BasicNameValuePair("file", selectedImagePath));
				 nameValuePairs.add(new BasicNameValuePair("upload_array", ""));
				 
				 nameValuePairs.add(new BasicNameValuePair("file_name", "ScanCheX"+new Date().getTime()));
				 Log.i("FILE PATH TO BE UPLOADED >CTYPE>"+contentType, "<<<<>>>>>"+SCImageTakenScreen.selectedImagePath);
				 
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
				Log.i("DONE DONE", "DONE DONE");
				pdialog.dismiss();
				pdialog = null;
				if (result) {
					File file = new File(SCImageTakenScreen.selectedImagePath);
					//boolean deleted = file.delete();
					showAlertDialog("Info", status);
				} else {
					showAlertDialog("Info", status);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pdialog = new ProgressDialog(SCImageTakenScreen.this);
				pdialog.setCancelable(false);
				pdialog.setTitle("Uploading Image");
				pdialog.setMessage("Please Wait...");
				pdialog.show();

			}

		private void showAlertDialog(String title, String message) {
			new AlertDialog.Builder(SCImageTakenScreen.this)
					.setIcon(R.drawable.info_icon)
					.setTitle(title)
					.setMessage(message)
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Intent returnIntent = new Intent(
											SCImageTakenScreen.this,
											SCQuestionsFragment.class);
									returnIntent.putExtra("result", "newvalue");
									setResult(RESULT_OK, returnIntent);
									SCImageTakenScreen.this.finish();

								}
							}).show();
		}
	}

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

		private static File getOutputMediaFile(int type) {

			// External sdcard location
			File mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					IMAGE_DIRECTORY_NAME);

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					return null;
				}
			}

			// Create a media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.getDefault()).format(new Date());
			File mediaFile;
			if (type == MEDIA_TYPE_IMAGE) {
				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ "IMG_" + timeStamp + ".jpg");
			}  else {
				return null;
			}

			return mediaFile;
		}

}
