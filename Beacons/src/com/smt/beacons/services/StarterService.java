package com.smt.beacons.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StarterService extends Service {
	private static final String TAG = "MyService";

	/**
	 * The started service starts the AlarmManager.
	 */
	@Override
	public void onStart(Intent intent, int startid) {
		
		Log.e("Starter Service", "Starting alarm manager to weak up beaconsmonitoringservice");
		
		Intent i = new Intent(StarterService.this, BeaconsMonitoringService.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(StarterService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) StarterService.this.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 10); // first time
		long frequency = 20 * 1000; // in ms
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
		
		Log.e("Starter Service", "alarm manager started");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}
}