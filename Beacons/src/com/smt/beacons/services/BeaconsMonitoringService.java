package com.smt.beacons.services;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.smt.beacons.easibeacons.IBeacon;
import com.smt.beacons.easibeacons.IBeaconListener;
import com.smt.beacons.easibeacons.IBeaconProtocol;
import com.smt.beacons.model.BeaconInfoDTO;
import com.smt.beacons.tasks.CheckBeaconsTask;
import com.smt.beacons.tasks.ExitBeaconsTask;
import com.smt.beacons.utils.Utils;

public class BeaconsMonitoringService extends Service implements IBeaconListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	private static IBeaconProtocol _ibp;
	
	Location mCurrentLocation;
    LocationClient mLocationClient;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Toast.makeText(this, "MONITORING START", Toast.LENGTH_LONG).show();
	    Log.e("MonitoringService", "Received start id " + startId + ": " + intent);
	    
//	    _ibp = ((BeaconsApp)getApplicationContext()).getIbp();
	    _ibp = IBeaconProtocol.getInstance(this);
		_ibp.setListener(this);
	    
		scanBeacons();
		
		
		mLocationClient = new LocationClient(this, this, this);
        
	    String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	     
	     // if gps is disabled and network location services are disabled
	     // the user has no location services and must enable something
	     if (!locationProviders.contains("gps") && !locationProviders.contains("network")) {
	
	     	// build a new alert dialog to inform the user that they have no
	     	// location services enabled
	     	new AlertDialog.Builder(this)
	     	
	     			//set the message to display to the user
	     			.setMessage("No Location Services Enabled")
	     			// add the 'positive button' to the dialog and give it a click listener
	     			
	     			.setPositiveButton("Enable Location Services", new DialogInterface.OnClickListener() {
	     						// setup what to do when clicked
	     				public void onClick(DialogInterface dialog, int id) {
	     					// start the settings menu on the correct screen for the user
	     					startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	     				}
	     					// add the 'negative button' to the dialog and give it a click listener
	     			})
	     					
	     			.setNegativeButton("Close", new DialogInterface.OnClickListener() {
	     				// setup what to do when clicked
	     				public void onClick(DialogInterface dialog,int id) {
	     					// remove the dialog
	     					dialog.cancel();
	     				}
	     				// finish creating the dialog and show to the user
	     			}).create().show();
	     }
	     
	     mLocationClient.connect();
		
		
		// We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
//		Toast.makeText(this, "MONITORING STOP", Toast.LENGTH_LONG).show();
		Log.e("MonitoringService", "Destroy" );
		_ibp.stopScan();
		mLocationClient.disconnect();
		super.onDestroy();
	}
	
	public void stopService(){
		stopSelf();
	}
	
	private void scanBeacons(){
		
		if(_ibp.isScanning())
			_ibp.stopScan();
		_ibp.reset();
		_ibp.startScan();
	}
	
	@Override
	public void enterRegion(IBeacon ibeacon) {
		Toast.makeText(this, "Beacon found!! Minor --> "+ibeacon.getMinor(), Toast.LENGTH_SHORT).show();
		List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
		list.add(new BeaconInfoDTO(ibeacon.getMajor(), ibeacon.getMinor()));
		try{
			new CheckBeaconsTask(getApplicationContext(), list, String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude())).execute();
		} catch( Exception e){
			Log.e("Exception calling backend from service","In EasiActivity onResume");
		}
	}

	@Override
	public void exitRegion(IBeacon ibeacon) {
		Toast.makeText(this, "Beacon found!! Minor --> "+ibeacon.getMinor(), Toast.LENGTH_SHORT).show();
		List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
		list.add(new BeaconInfoDTO(ibeacon.getMajor(), ibeacon.getMinor()));
		try{
			new ExitBeaconsTask(getApplicationContext(), list, String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude())).execute();
		} catch( Exception e){
			Log.e("Exception calling backend from service","In EasiActivity onResume");
		}
	}

	@Override
	public void beaconFound(IBeacon ibeacon) {
		
	}

	@Override
	public void searchState(int state) {
		
	}

	@Override
	public void operationError(int status) {
		Log.e(Utils.LOG_TAG, "Bluetooth error: " + status);	
		
	}

	/*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		 /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
//            try {
                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult( this, 9000);
//                CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
//            showErrorDialog(connectionResult.getErrorCode());
        	 Toast.makeText(this, "Error with location service code:"+connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }

		
	}

	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle arg0) {
		// Display the connection status
//        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        mCurrentLocation = mLocationClient.getLastLocation();
		
	}

	/*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
	@Override
	public void onDisconnected() {
		// Display the connection status
//        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
		
	}
	  
}
