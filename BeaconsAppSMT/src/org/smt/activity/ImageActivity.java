package org.smt.activity;



import org.smt.R;
import org.smt.app.BeaconsApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
/**
 * Activity used to show image of promociones
 * @author Azam-smt
 *
 */
public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		Intent i = getIntent();
		String image = i.getStringExtra("image");
	    final ImageView imageView = (ImageView) findViewById(R.id.imageButton1);
	    
	    ((BeaconsApp) this.getApplicationContext()).getImageLoader().displayImage(image, imageView);
	}
}
