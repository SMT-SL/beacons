package com.smt.beacons.receivers;

import com.smt.beacons.services.BeaconsMonitoringService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduledReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	 
		Log.e("Scheduled Receiver", "Alarm manager worked and event received. BeaconsMonitoring Service starting.");
		
		Intent scheduledIntent = new Intent(context, BeaconsMonitoringService.class);
		scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(scheduledIntent);
	 
		Log.e("Scheduled Receiver", "Alarm manager worked and event received. BeaconsMonitoring Service started.");
	
	}

}