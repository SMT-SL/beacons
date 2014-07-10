package com.smt.beaconssmt.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils.Proximity;
import com.smt.beaconssmt.ImageActivity;
import com.smt.beaconssmt.activities.MainActivity;
import com.smt.beaconssmt.activities.WebViewActivity;
import com.smt.beaconssmt.utils.BeaconsApp;
import com.smt.beaconssmt.utils.Utils;

public class BeaconsMonitoringService extends Service{
	
	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	
	private BeaconManager beaconManager;
	
	private String user;
	
	@Override
	  public void onCreate() {
		// Configure BeaconManager.
//        beaconManager = new BeaconManager(this);
        BeaconsApp appState = (BeaconsApp)this.getApplication();
        beaconManager = appState.getBm();
        
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "Beacons monitoring service starting", Toast.LENGTH_SHORT).show();
	      
//		  user = intent.getStringExtra("user");
	      SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
		  user = settings.getString("username", "");

	        // Check if device supports Bluetooth Low Energy.
	        if (!beaconManager.hasBluetooth()||!beaconManager.isBluetoothEnabled()) {
	          Toast.makeText(this, "Device does not have Bluetooth Low Energy or it is not enabled", Toast.LENGTH_LONG).show();
	          this.stopSelf();
	        }
	        
	          connectToService();
	      
	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
	      return null;
	  }

	  @Override
	  public void onDestroy() {
	    Toast.makeText(this, "Beacons monitoring service done", Toast.LENGTH_SHORT).show();
	  }
	  
	  private void connectToService() {
		  
//	      beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//	    	  
//	        @Override
//	        public void onServiceReady() {
//	        	notifyEnterRegion(0);
//	          try {
		  
		  
	        	  beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
	        	  
	        	  beaconManager.setRangingListener(new RangingListener() {
	        		  
	                  @Override
	                  public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
	                      if (paramList != null && !paramList.isEmpty()) {
	                          Beacon beacon = paramList.get(0);
	                          Proximity proximity = Utils.computeProximity(beacon);
	                          if (proximity == Proximity.IMMEDIATE) {
	                        	  notifyEnterRegion(beacon.getMinor());
	                          } else if (proximity == Proximity.FAR) {
	                              notifyExitRegion(beacon.getMinor());
	                          }
	                      }
	                  }

	              });
	        	  
	        	  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
	                  @Override
	                  public void onServiceReady() {
	                      try {
	                          Log.d("TAG", "connected");
//	                          for (Region region : BEACONS) {
	                              beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
//	                          }
	                      } catch (RemoteException e) {
	                          Log.d("TAG", "Error while starting monitoring");
	                      }
	                  }
	              });
//	              beaconManager.setMonitoringListener(new MonitoringListener() {
//	                @Override
//	                public void onEnteredRegion(Region region, List<Beacon> beacons) {
//	              	  Log.i("BEACOON ", String.valueOf(beacons.get(1).getMinor()));
//	              	for (Beacon beacon: beacons){
//	              		Log.i("BEACOON ", String.valueOf(beacon.getMinor()));
//	              		if (beacon.getMinor() == 64444) {
//	              			
//	              			notifyEnterRegion(6444);
//	              			
//	              		} else if (beacon.getMinor() == 36328) {
//	              			
//	              			notifyEnterRegion(36328);
//	              			
//	              		} else if (beacon.getMinor() == 31394) {
//	              			
//	              			notifyEnterRegion(31394);
//	              			
//	              		}
//	              	}
//	                }
//
//	                @Override
//	                public void onExitedRegion(Region region) {
//	                	
//	                	notifyExitRegion();
//	          			
//	                }
//	              });  
	        
//	        }
//	      });
	    }
	  
	  public void notifyEnterRegion(int code) {
		  
		  	Toast.makeText(this, "Beacon "+code, Toast.LENGTH_SHORT).show();
		  	Intent targetIntent;
		  	Notification noti = null;
   	  		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   	  		
		  	
			if (code == 64444){
				
				targetIntent = new Intent(this, WebViewActivity.class);
				targetIntent.putExtra("web", "http://musicexperience.cocacola.es/");
	       	  	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
	          
	       	  	noti = new Notification.Builder(this)
		         .setContentTitle("Felicidades "+user+"!")
		         .setContentText("Has ganado 2 entradas para el Coca-Cola Music Xperience sólo por estar aquí")
		         .setSmallIcon(com.smt.beaconssmt.R.drawable.beacon_gray)
		         .setContentIntent(contentIntent)
		         .getNotification();
	       	  	
	       	  	nManager.notify(1, noti);
	       	  	
			} else if (code == 36328){
				
				targetIntent = new Intent(this, ImageActivity.class);
	       	  	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
	          
	       	  	noti = new Notification.Builder(this)
		         .setContentTitle("Bienvenido "+user+"!")
		         .setContentText("Vente al curso de peluquería de L'Oreal al que Coca-Cola y Bacardi te invitan en la sala 5")
		         .setSmallIcon(com.smt.beaconssmt.R.drawable.beacon_gray)
		         .setContentIntent(contentIntent)
		         .getNotification();
	       	  	
	       	  	nManager.notify(2, noti);
	       	  	
			} else if (code == 31394) {
				
				targetIntent = new Intent(this, WebViewActivity.class);
				targetIntent.putExtra("web", "http://www.bacardi.com/es/lda");
	       	  	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
	          
	       	  	noti = new Notification.Builder(this)
		         .setContentTitle("Bienvenido "+user+"!")
		         .setContentText("Hoy, con tu Coca-Cola light + Bacardi Blanco te invitamos a hacerte un peinado de trenzas en la zona de la piscina")
		         .setSmallIcon(com.smt.beaconssmt.R.drawable.beacon_gray)
		         .setContentIntent(contentIntent)
		         .getNotification();
	       	  	
	       	  	nManager.notify(2, noti);
			}
   		 	
   	  		noti.defaults = Notification.DEFAULT_ALL;
       	  		
		}
	  
	  public void notifyExitRegion(int code){
			
			Intent targetIntent = new Intent(this, MainActivity.class);
     	  	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
     	  	
     	  	Notification noti = new Notification.Builder(this)
	         .setContentTitle("Hasta pronto "+user+"!")
	         .setContentText("Estas abandonando la zona del beacon "+code)
	         .setSmallIcon(com.smt.beaconssmt.R.drawable.beacon_gray)
	         .setContentIntent(contentIntent)
	         .getNotification();
 		 
     	  	noti.defaults = Notification.DEFAULT_ALL;
     	  	
     	  	NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     	  	nManager.notify(1, noti);
	  }

	  
}
