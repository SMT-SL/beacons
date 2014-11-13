package com.smt.beacons.receivers;

import com.smt.beacons.services.BeaconsMonitoringService;
import com.smt.beacons.services.StarterService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartServiceAtBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
			Log.e("Autostart", "BOOT_COMPLETED broadcast received. Executing BeaconsMonitoringService service.");
		
//		if ("android.intent.action.BOOT_COMPLETED".equals(arg1.getAction())) {
		
	        Intent serviceLauncher = new Intent(arg0, BeaconsMonitoringService.class);
	        arg0.startService(serviceLauncher);
//	        Log.v("TEST", "Service loaded at start");
		
//	    }
		
//	        Intent i = new Intent(arg0, ScheduledReceiver.class);
//			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//			PendingIntent pi = PendingIntent.getBroadcast(arg0, 0, i,
//					PendingIntent.FLAG_UPDATE_CURRENT);
//
//			// Repeat the notification every 15 seconds (15000)
//			AlarmManager am = (AlarmManager) arg0.getSystemService(Context.ALARM_SERVICE);
//			am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//					System.currentTimeMillis(), 60000, pi);

//			Toast.makeText(arg0, "My Service started", Toast.LENGTH_LONG).show();
			Log.e("Autostart", "BOOT_COMPLETED StartServiceAtBootReceiver completed. Executing AlarmManager from here.");
	        
	}

}