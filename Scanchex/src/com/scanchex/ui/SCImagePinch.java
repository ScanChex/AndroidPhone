package com.scanchex.ui;

import com.scanchex.utils.TouchImageView;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SCImagePinch extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_pinch);

		Intent intent = getIntent();
		String imageUrl = intent.getStringExtra("ImageUrl");

		TouchImageView imageView = (TouchImageView) findViewById(R.id.imageView);

		try {
			Picasso.with(this) //
					.load(imageUrl) //
					.placeholder(R.drawable.scan_chexs_logo) //
					.error(R.drawable.app_icon) //
					.into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
