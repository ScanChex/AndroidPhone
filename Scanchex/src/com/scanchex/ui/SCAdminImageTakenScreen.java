package com.scanchex.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.scanchex.utils.SCPreferences;

public class SCAdminImageTakenScreen extends Activity{
	
	public static String selectedImagePath = "";
	private final int SELECT_PICTURE = 200;
	private static final int CAMERA_PIC_REQUEST = 1337;
	private String contentType = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_imagetaken_screen);
		LinearLayout layout = (LinearLayout)findViewById(R.id.imageTokeContainer);
		layout.setBackgroundColor((SCPreferences.getColor(SCAdminImageTakenScreen.this)));
		
		selectedImagePath = "";
	}
	
	
	public void onCameraClick(View view) {
			
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	
	}

	public void onLibraryClick(View view) {
		
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_PICTURE);
	
	}
	
	public void onBackClick(View view) {
		
		this.finish();
	}
	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				
				Uri selectedImageUri = data.getData();
				contentType = getContentType(selectedImageUri);
				selectedImagePath = getPath(selectedImageUri);
				Log.i("IMAGE URL", "<>GALLERY<> "+ selectedImagePath);
				File imageFile = new File(selectedImagePath);
				Bitmap bm = decodeFile(imageFile, getCameraPhotoOrientation(this, selectedImageUri, selectedImagePath));
			} else if (requestCode == CAMERA_PIC_REQUEST) {

				Uri selectedImageUri = data.getData();
				contentType = getContentType(selectedImageUri);
				selectedImagePath = getPath(selectedImageUri);
				Log.i("IMAGE URL", "<>CAMERA<> "+ selectedImagePath);
				File imageFile = new File(selectedImagePath);
				Bitmap bm = decodeFile(imageFile, getCameraPhotoOrientation(this, selectedImageUri, selectedImagePath));
			}
			
			if(selectedImagePath!=null && selectedImagePath.length()>0){
				SCAdminShowAssetDetailsScreen.selectedImagePath = selectedImagePath;
				SCAdminShowAssetDetailsScreen.contentType = contentType;
				this.finish();
			}
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
		        final int REQUIRED_SIZE=500;
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

			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
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
}
