package com.smt.beacons.activity;

import com.smt.beacons.R;
import com.smt.beacons.R.id;
import com.smt.beacons.R.layout;
import com.smt.beacons.R.menu;
import com.smt.beacons.services.BeaconsMonitoringService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
		if (saliendo){
			Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
			startService(i);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		saliendo = true;
		Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
		stopService(i);
		super.onResume();
	}
}
