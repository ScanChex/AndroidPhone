package com.scanchex.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scanchex.utils.SCPreferences;
import com.squareup.picasso.Picasso;

public class SCHistoryImageViewScreen extends Activity{
	
	 
	private CharSequence imageUrl[];
	int count = 0;
	ViewPager pager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_slider_screen);
	//setContentView(R.layout.sc_history_image_screen);
		LinearLayout layout = (LinearLayout)findViewById(R.id.sliderScreen);
		layout.setBackgroundColor(SCPreferences.getColor(SCHistoryImageViewScreen.this));
		int pagerPosition = 0;
	
		imageUrl = getIntent().getExtras().getCharSequenceArray("PATH");
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(this,imageUrl));
		pager.setCurrentItem(pagerPosition);

	}
	
	public void onNextClick(View view){
		if(count<imageUrl.length-1){
			count = count+1;
			pager.setCurrentItem(count);
		}
//		.fit() //	

	}
	
	public void onPrevClick(View view){
		if(count>0){
			count = count-1;
			pager.setCurrentItem(count);
		 
		}
		
	}
	

	private class ImagePagerAdapter extends PagerAdapter {

		private CharSequence images[];
		private LayoutInflater inflater;
		private Context context;

		ImagePagerAdapter(Context context,CharSequence images[]) {
			this.images = images;
			inflater = getLayoutInflater();
			this.context = context;
			}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.sc_history_image_screen,
					view, false);
			//assert imageLayout != null;
			ImageView image = (ImageView)imageLayout.findViewById(R.id.image);
			
			TextView countShow = (TextView)imageLayout.findViewById(R.id.textViewCount);
			String countshowString = (position+1) + "/" + images.length; 
			countShow.setText(countshowString);
			Log.i("Image URL"+imageUrl.length, "<> "+imageUrl[count]);
			
			try{
			Picasso.with(context) //
			.load(imageUrl[position].toString()) //
			.placeholder(R.drawable.scan_checks_biglogo) //
			.error(R.drawable.app_icon).fit() //
			.into(image);
			}catch(Exception e){
				e.printStackTrace();
			}
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

}
