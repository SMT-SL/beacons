package com.smt.beaconssmt.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.smt.beaconssmt.R;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Intent i = getIntent();
		String image = i.getStringExtra("image");
		int resId = getResources().getIdentifier(image, "drawable", this.getPackageName());
	    ImageView imageView = (ImageView) findViewById(R.id.imageButton1);
	    imageView.setImageDrawable(getResources().getDrawable(resId));
	}
	
	@Override
	protected void onPause() {
		MainActivity.setSaliendo(true);
		super.onPause();
	}
}
