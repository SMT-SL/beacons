package org.smt.activity;





import org.smt.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	private boolean saliendo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		saliendo = true;
	}
	
	@Override
	public void onBackPressed() {
		saliendo = false;
		super.onBackPressed();
	}
	@Override
	protected void onPause() {
//		if (saliendo){
//			Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
//			startService(i);
//		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		saliendo = true;
//		Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
//		stopService(i);
		super.onResume();
	}
}
