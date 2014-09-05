package com.smt.beaconssmt;

import com.smt.beaconssmt.activities.MainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PorraActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_porra);
		
		Button boton = (Button)findViewById(R.id.button1);
		boton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Se ha registrado tu resultado "+MainActivity.user+", suerte!!", Toast.LENGTH_LONG).show();
				onBackPressed();
				
			}
		});
	}
}
