package com.smt.beaconsmagnum.services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.Region;
import com.smt.beaconsmagnum.R;
import com.smt.beaconsmagnum.activities.ImageActivity;
import com.smt.beaconsmagnum.activities.MainActivity;
import com.smt.beaconsmagnum.activities.WebViewActivity;
import com.smt.beaconsmagnum.model.BeaconMagnum;
import com.smt.beaconsmagnum.utils.BeaconsApp;

public class BeaconsMonitoringService extends Service{
	
	private BeaconManager beaconManager;
//	private List<Beacon> beaconsNear;
	
	private String user;
	
	@Override
	  public void onCreate() {
		// Configure BeaconManager.
//        beaconManager = new BeaconManager(this);
        BeaconsApp appState = (BeaconsApp)this.getApplication();
        beaconManager = appState.getBm();
//        beaconsNear = new ArrayList<Beacon>();
        
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "Beacons monitoring service starting", Toast.LENGTH_SHORT).show();
	      
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
		  
		  
	        	  beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
	        	  
	        	  beaconManager.setMonitoringListener(new MonitoringListener() {
		                @Override
		                public void onEnteredRegion(Region region, List<Beacon> beacons) {
		              	  for (Beacon beacon: beacons){
		              		Log.i("BEACOON ", String.valueOf(beacon.getMinor()));
//		              		beaconsNear.add(beacon);
		              		BeaconMagnum bm = new BeaconMagnum(beacon, new Date());
		           	  		if (!BeaconsApp.getBeaconsDiscovered().isBeaconExisting(bm)){
		           	  			notifyEnterRegion(beacon.getMinor());
		           	  		}
		              	  }
		                }

		                @Override
		                public void onExitedRegion(Region region) {
		                	
		                	notifyExitRegion(region.getMinor());
//		                	for (Beacon b:beaconsNear){
//		                		if (b.getProximityUUID() == region.getProximityUUID() && b.getMajor() == region.getMajor() && b.getMinor() == region.getMinor()){
//		                			beaconsNear.remove(b);
//		                		}
//		                	}
		                	
		                }
		              });
	        	  
//	        	  beaconManager.setRangingListener(new RangingListener() {
//	        		  
//	                  @Override
//	                  public void onBeaconsDiscovered(Region paramRegion, List<Beacon> paramList) {
//	                      if (paramList != null && !paramList.isEmpty()) {
//	                    	  for (Beacon b: paramList){
////		                          Beacon beacon = paramList.get(0);
//		                          Proximity proximity = Utils.computeProximity(b);
//		                          if (proximity == Proximity.IMMEDIATE) {
//		                        	  notifyEnterRegion(b.getMinor());
//		                          } else if (proximity == Proximity.FAR) {
//		                              notifyExitRegion(b.getMinor());
//		                          }
//	                    	  }
//	                      }
//	                  }
//
//	              });
	        	  
	        	  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
	                  @Override
	                  public void onServiceReady() {
	                      try {
//	                          Log.d("TAG", "connected");
//	                          for (Region region : BEACONS) {
//	                            beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
//	                          	beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS_REGION);
	                          	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
	                          	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
	                          	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//	                          }
	                      } catch (RemoteException e) {
	                          Log.d("TAG", "Error while starting monitoring");
	                      }
	                  }
	              });
	        
//	        }
//	      });
	    }
	  
	  public void notifyEnterRegion(int code) {
		  
		  	Toast.makeText(this, "Beacon "+code, Toast.LENGTH_SHORT).show();
		  	Intent targetIntent = null;
		  	Notification noti = null;
   	  		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   	  		String notifTitle = null, notifText = null;
   	  		int notifSmallIcon = 0, notifId = 0;
   	  		Bitmap notifBigIcon = null;
   	  		
   	  		
			if (code == 64444){
				
//				targetIntent = new Intent(this, WebViewActivity.class);
//				targetIntent.putExtra("web", "http://www.frigo.es/Brand/Magnum.aspx");
				targetIntent = new Intent(this, ImageActivity.class);
				targetIntent.putExtra("image", "magnumfrac32");
				
				notifTitle = "Bienvenido "+user+"!";
				notifText = "Hace un d�a perfecto para tomarse un Magnum Frac, hoy 3x2!";
	   	  		notifSmallIcon = R.drawable.beacon_blue;
	   	  		notifBigIcon = BitmapFactory.decodeResource(this.getResources(),
		   	  	    	R.drawable.magnumfrac);
	   	  		notifId = 111;
	   	  		
	   	  		nManager.cancel(1111);
	       	  	
			} else if (code == 36328){
				
				targetIntent = new Intent(this, ImageActivity.class);
//				targetIntent.putExtra("image", "magnum_sandwich");
				targetIntent.putExtra("image", "magnumfrac21");
				
				notifTitle = "Bienvenido "+user+"! Hoy tu frac...";
//				notifText = "Hoy comprando tu Magnum Sandwich, te regalamos otro para un doble disfrute!";
				notifText = "Hoy tenemos 2x1 comprando tu Magnum Frac!! Disfrutalo en pareja";
	   	  		notifSmallIcon = R.drawable.beacon_purple;
	   	  		notifBigIcon = BitmapFactory.decodeResource(this.getResources(),
		   	  	    	R.drawable.magnumfrac21);
	   	  		notifId = 112;
	   	  		
	   	  		nManager.cancel(1122);
	       	  	
			} else if (code == 31394) {
				
//				targetIntent = new Intent(this, ImageActivity.class);
//				targetIntent.putExtra("image", "fresa");
//				targetIntent = new Intent(this, WebViewActivity.class);
//				targetIntent.putExtra("web", "https://www.youtube.com/watch?v=dIfKd0aVeE8");
//				
////				notifTitle = "Felicidades "+user+"!";
////				notifText = "S�lo por estar aqu� te regalamos un sabroso Magnum Strawberry & White";
//				notifTitle = "Hola "+user+"!";
//				notifText = "Mira lo que te tenemos preparado en Magnum";
//	   	  		notifSmallIcon = R.drawable.beacon_green;
//	   	  	    notifBigIcon = BitmapFactory.decodeResource(this.getResources(),
//	   	  	    	R.drawable.fresa1);
				targetIntent = new Intent(this, ImageActivity.class);
				targetIntent.putExtra("image", "magnumfrac43");
				
				notifTitle = "Bienvenido "+user+"!";
				notifText = "Hace un d�a perfecto para tomarse un Magnum Frac, hoy 4x3!";
	   	  		notifSmallIcon = R.drawable.beacon_green;
	   	  		notifBigIcon = BitmapFactory.decodeResource(this.getResources(),
		   	  	    	R.drawable.magnumfrac);
	   	  		notifId = 113;
	   	  		
	   	  		nManager.cancel(1133);
			}
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
	          
//       	  	noti = new Notification.Builder(this)
//	         .setContentTitle(notifTitle)
//	         .setContentText(notifText)
//	         .setSmallIcon(notifSmallIcon)
//	         .setOnlyAlertOnce(true)
//	         .setAutoCancel(true)
//	         .setDefaults(Notification.DEFAULT_ALL)
//	         .setContentIntent(contentIntent)
//	         .setLargeIcon(notifBigIcon)
//	         .build();
       	  	
       	  	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
    	    .setSmallIcon(notifSmallIcon)
    	    .setContentTitle(notifTitle)
    	    .setContentText(notifText)
    	    .setOnlyAlertOnce(true)
	        .setAutoCancel(true)
	        .setDefaults(Notification.DEFAULT_ALL)
	        .setContentIntent(contentIntent)
	        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notifBigIcon));
       	  	
       	  	noti = mBuilder.build();
       	  	
       	  	nManager.notify(notifId, noti);
       	  		
		}
	  
	  public void notifyExitRegion(int code){
 		 
     	  	Toast.makeText(this, "Saliendo de la zona Beacon "+code, Toast.LENGTH_SHORT).show();
		  	Intent targetIntent = new Intent(BeaconsMonitoringService.this, ImageActivity.class);
			targetIntent.putExtra("image", "beacons_exit_region");
		  	Notification noti = null;
   	  		NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   	  		String notifTitle = "Hasta pronto "+user+"!", notifText = "Estas abandonando la zona del beacon "+code;
   	  		int notifSmallIcon = 0, notifId = 0;
   	  		
			if (code == 64444){
				
	   	  		notifSmallIcon = R.drawable.beacon_gray;
	   	  		notifId = 1111;
	   	  		nManager.cancel(111);
	       	  	
			} else if (code == 36328){
				
	   	  		notifSmallIcon = R.drawable.beacon_gray;
	   	  		notifId = 1122;
	   	  		nManager.cancel(112);
	       	  	
			} else if (code == 31394) {
				
				notifSmallIcon = R.drawable.beacon_gray;
	   	  		notifId = 1133;
	   	  		nManager.cancel(113);
	   	  		
			}
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
	          
       	  	noti = new Notification.Builder(this)
	         .setContentTitle(notifTitle)
	         .setContentText(notifText)
	         .setSmallIcon(notifSmallIcon)
	         .setOnlyAlertOnce(true)
	         .setAutoCancel(true)
	         .setDefaults(Notification.DEFAULT_ALL)
	         .setContentIntent(contentIntent)
	         .build();
       	  	
       	  	nManager.notify(notifId, noti);
       	  	
	  }

	  
}
