package com.smt.beacons.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.smt.beacons.R;
import com.smt.beacons.adapters.PromotionListAdapter;
import com.smt.beacons.easibeacons.IBeacon;
import com.smt.beacons.easibeacons.IBeaconListener;
import com.smt.beacons.easibeacons.IBeaconProtocol;
import com.smt.beacons.model.BeaconInfoDTO;
import com.smt.beacons.model.OfferDetailsDTO;
import com.smt.beacons.receivers.StartServiceAtBootReceiver;
import com.smt.beacons.services.BeaconsMonitoringService;
import com.smt.beacons.services.StarterService;
import com.smt.beacons.tasks.CheckBeaconsTask;
import com.smt.beacons.utils.Utils;

// This activity implements IBeaconListener to receive events about iBeacon discovery
public class EasiActivity extends Activity implements IBeaconListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{
	
	public static final int REQUEST_BLUETOOTH_ENABLE = 1;

	private ListView listBeacons;
	
//	private BeaconsAdapter _beaconsAdapter;
	private static IBeaconProtocol _ibp;
	
	private static ArrayList<IBeacon> _beacons;
	public static ArrayList<OfferDetailsDTO> promotions;
	public static PromotionListAdapter promotionsAdapter;
	private List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
	
	private Menu _menu;
	
	private boolean saliendo;
	
	java.util.Timer timer;
	
	Location mCurrentLocation;
    LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		promotions = new ArrayList<OfferDetailsDTO>();
		
		if(_beacons == null)
			_beacons = new ArrayList<IBeacon>();
//		_beaconsAdapter = new BeaconsAdapter(this,_beacons);
		promotionsAdapter = new PromotionListAdapter(this);
				
		listBeacons = (ListView) findViewById(R.id.listView1);
//		listBeacons.setAdapter(_beaconsAdapter);
		listBeacons.setAdapter(promotionsAdapter);
		
		listBeacons.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				Toast.makeText(EasiActivity.this, String.valueOf(((IBeacon)arg0.getAdapter().getItem(arg2)).getMinor()), Toast.LENGTH_SHORT).show();
				saliendo = false;
				
				if (((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferType()!=2){
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL())));
				} else {
					Intent i = new Intent(EasiActivity.this, ImageActivity.class);
					String web = ((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL();
					i.putExtra("image", web);
					startActivity(i);
				}
//				else if (((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferType()==3){
//					Intent i = new Intent(EasiActivity.this, VideoActivity.class);
//					String web = ((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL();
//					i.putExtra("videoURL", web);
//					startActivity(i);
//				}	
				
			}
		});
		
		_ibp = IBeaconProtocol.getInstance(this);
		_ibp.setListener(this);
		
		saliendo = true;
		
		
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
	     					finish();
	     				}
	     				// finish creating the dialog and show to the user
	     			}).create().show();
	     }

	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		_ibp.stopScan();
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		timer.cancel();
		_ibp.stopScan();
		if (saliendo){
			Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
			startService(i);
			
			Intent in = new Intent(getApplicationContext(), StartServiceAtBootReceiver.class);
			
			Log.e("Starter Service", "Starting alarm manager to weak up beaconsmonitoringservice");

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1357, in, 0);

			AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, 20); // first time
			long frequency = 15 * 1000; // in ms
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		mLocationClient.connect();
		
		saliendo = true;
		
		try{
			Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
			stopService(i);
		} catch (Exception e){
			Log.e("Exception stopping Service","In EasiActivity onResume");
		}
		
		timer = new java.util.Timer();
		
		timer.schedule( 
				new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	runOnUiThread(new Runnable() {
		        			@Override
		        			public void run() {
		        				scanBeacons();
		        			}
		            	});
		            }
		        },1500, 15000
		);
		
		Intent intent = new Intent(getApplicationContext(), StartServiceAtBootReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1357, intent, 0);
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	    alarmManager.cancel(pendingIntent);
		
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BLUETOOTH_ENABLE){
			if(resultCode == Activity.RESULT_OK){
				scanBeacons();
			}
		}
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beacon_list, menu);
		_menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
			case R.id.action_refresh:
				scanBeacons();
				return true;
				
			case R.id.action_logout:
	    	
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("email", "");
				editor.commit();
				
		    	Intent intent = new Intent(EasiActivity.this, LoginActivity.class);
				startActivity(intent);
				
				timer.cancel();
				saliendo = false;
				Toast.makeText(this, "Hasta pronto!", Toast.LENGTH_LONG).show();
				finish();
				return true;
				
			case R.id.action_settings:
		    	Intent intent2 = new Intent(EasiActivity.this, SettingsActivity.class);
				startActivity(intent2);
				saliendo = false;
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void scanBeacons(){
		_beacons.clear();
		list.clear();
		
		// Check Bluetooth every time
		Log.i(Utils.LOG_TAG,"Scanning");
		
		// Filter based on default easiBeacon UUID, remove if not required
		//_ibp.setScanUUID(UUID here);

		if(!IBeaconProtocol.configureBluetoothAdapter(this)){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );
		}else{
			if(_ibp.isScanning())
				_ibp.stopScan();
			_ibp.reset();
			_ibp.startScan();		
		}		
	}
	
	
	@SuppressLint("InflateParams")
	private void startRefreshAnimation(){
		MenuItem item = _menu.findItem(R.id.action_refresh);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView)inflater.inflate(R.layout.refresh_button, null);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);		
	}
	
	private void stopRefreshAnimation(){
		try{
			MenuItem item = _menu.findItem(R.id.action_refresh);
	        if(item.getActionView()!=null){
	            // Remove the animation.
	            item.getActionView().clearAnimation();
	            item.setActionView(null);
	        }
		} catch (Exception e){
//			Log.e("Easi Activity stopRefreshAnimation", e.getMessage().toString());
		}
		
	}
	
	@Override
	public void beaconFound(IBeacon ibeacon) {
		
		Log.e("Beacon found", "Major: "+ibeacon.getMajor()+" Minor: "+ibeacon.getMinor());
		boolean exists = false;
		for (BeaconInfoDTO bi : list){
			if (bi.getMajor() == ibeacon.getMajor() && bi.getMinor()== ibeacon.getMinor()){
				exists = true;
			}
		}
		if(!exists){
			list.add(new BeaconInfoDTO(ibeacon.getMajor(), ibeacon.getMinor()));
			new CheckBeaconsTask(this,list, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
		}
		
	}
	
	@Override
	public void enterRegion(IBeacon ibeacon) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exitRegion(IBeacon ibeacon) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void operationError(int status) {
		Log.i(Utils.LOG_TAG, "Bluetooth error: " + status);	
		
	}
	
	@Override
	public void searchState(int state) {
		if(state == IBeaconProtocol.SEARCH_STARTED){
			startRefreshAnimation();
		}else if (state == IBeaconProtocol.SEARCH_END_EMPTY || state == IBeaconProtocol.SEARCH_END_SUCCESS){
			stopRefreshAnimation();
		}
		
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
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult( this, 9000);
//                CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
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