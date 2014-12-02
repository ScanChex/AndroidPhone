package com.scanchex.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scanchex.utils.SCPreferences;

public class SCHistoryNotesViewScreen extends BaseActivity{
	
	
	private EditText notText;
	private CharSequence messageArray[];
	int count = 0;
	ViewPager pager;
	String ticketid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sc_slider_screen);
		ticketid = getIntent().getExtras().getString("tickectid");
		// setContentView(R.layout.sc_history_notes_screen);
		LinearLayout layout = (LinearLayout) findViewById(R.id.sliderScreen);
		layout.setBackgroundColor(SCPreferences
				.getColor(SCHistoryNotesViewScreen.this));

		messageArray = getIntent().getExtras().getCharSequenceArray("PATH");
		ticketid = getIntent().getExtras().getString("tickectid");
		//Log.v("tickect val in notesactivity ", "tickect val in notesactivity"
		//		+ ticketid);

		int pagerPosition = 0;

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(messageArray));
		pager.setCurrentItem(pagerPosition);
		
	}
	
	public void onNextClick(View view){
		if(count<messageArray.length-1){
			count = count+1;
			pager.setAdapter(new ImagePagerAdapter(messageArray));
			pager.setCurrentItem(count);
		}	

	}
	
	public void onPrevClick(View view){
		if(count>0){
			count = count-1;
			pager.setAdapter(new ImagePagerAdapter(messageArray));
			pager.setCurrentItem(count);
		}	
	}
	
	

	private class ImagePagerAdapter extends PagerAdapter {

		private CharSequence images[];
		private LayoutInflater inflater;

		ImagePagerAdapter(CharSequence images[]) {
			this.images = images;
			inflater = getLayoutInflater();
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
			View imageLayout = inflater.inflate(
					R.layout.sc_history_notes_screen, view, false);
			// assert imageLayout != null;
			EditText notText = (EditText) imageLayout
					.findViewById(R.id.notext_text_edit);
			TextView text = (TextView) imageLayout
					.findViewById(R.id.tickect_id);
			TextView countShow = (TextView) imageLayout
					.findViewById(R.id.textViewCount);
			notText.setText(images[position]);

			text.setText(ticketid);
			String countshowString = (position + 1) + "/" + images.length;
			countShow.setText(countshowString);
			
		 
			 
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
