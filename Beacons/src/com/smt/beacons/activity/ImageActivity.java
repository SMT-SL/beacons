package com.smt.beacons.activity;

import com.smt.beacons.R;
import com.smt.beacons.utils.BeaconsApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Intent i = getIntent();
		String image = i.getStringExtra("image");
	    final ImageView imageView = (ImageView) findViewById(R.id.imageButton1);
//	    imageView.setImageURI(Uri.parse("android.resource://com.smt.beaconsmagnum/drawable/magnumfrac21"));//;Drawable(getResources().getDrawable(resId));
	    
	    ((BeaconsApp) this.getApplicationContext()).getImageLoader().displayImage(image, imageView);
	}
}
