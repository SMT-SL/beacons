package com.smt.beaconssmt.activities;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.smt.beaconssmt.R;
import com.smt.beaconssmt.adapters.LeDeviceListAdapter;
import com.smt.beaconssmt.services.BeaconsMonitoringService;
import com.smt.beaconssmt.utils.BeaconsApp;


public class MainActivity extends Activity {

	private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
	
	private BeaconManager beaconManager;
	private LeDeviceListAdapter adapter;
	
	private String user;
	
	private Button runInBackBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
	    user = settings.getString("username", "");
		Toast.makeText(this, "Bienvenido "+user+"!", Toast.LENGTH_SHORT).show();
        
        // Configure device list.
        adapter = new LeDeviceListAdapter(this);
        ListView list = (ListView) findViewById(R.id.device_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(createOnItemClickListener());
        
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
//        BeaconsApp appState = (BeaconsApp)this.getApplication();
//        beaconManager = appState.getBm();
        
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
          @Override
          public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
            // Note that results are not delivered on UI thread.
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // Note that beacons reported here are already sorted by estimated
                // distance between device and beacon.
                getActionBar().setSubtitle("Beacons encontrados: " + beacons.size());
                adapter.replaceWith(beacons);
              }
            });
          }
        });
        
        runInBackBtn = (Button) findViewById(R.id.runInBackBtn);
        runInBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
				startService(intent);
				System.exit(0);
			}
		});
        
	}



	@Override
    protected void onStart() {
      super.onStart();

      // Check if device supports Bluetooth Low Energy.
      if (!beaconManager.hasBluetooth()) {
        Toast.makeText(this, "El dispositivo no soporta Bluetooth Low Energy, los beacons no serán detectados", Toast.LENGTH_LONG).show();
        return;
      }

      // If Bluetooth is not enabled, let user enable it.
      if (!beaconManager.isBluetoothEnabled()) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1234);
      } else {
//        connectToService();
    	  connectToMonitoringService();
      }
    }

    @Override
    protected void onStop() {
    	try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	super.onStop();
    }

    @Override
    protected void onResume() {
      super.onResume();
//      connectToService();
          connectToMonitoringService();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 1234) {
        if (resultCode == Activity.RESULT_OK) {
//          connectToService();
        	connectToMonitoringService();
        } else {
          Toast.makeText(this, "Bluetooth no activado", Toast.LENGTH_LONG).show();
          getActionBar().setSubtitle("Bluetooth no activado");
        }
      }
      super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
      beaconManager.disconnect();
      super.onDestroy();
    }
    
    private void connectToMonitoringService() {
        getActionBar().setSubtitle("Buscando...");
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
          @Override
          public void onServiceReady() {
            try {
              beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            } catch (RemoteException e) {
              Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
                  Toast.LENGTH_LONG).show();
            }
          }
        });
      }

    private AdapterView.OnItemClickListener createOnItemClickListener() {
      return new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Intent intent = new Intent(MainActivity.this, BeaconDataActivity.class);
              intent.putExtra("beacon", adapter.getItem(position));
              startActivity(intent);
        }
      };
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
    	
        switch (item.getItemId()) {
            
            case R.id.action_profile:
      			
      			Intent i = new Intent(MainActivity.this, SettingsActivity.class);
      			startActivity(i);
      			
                return true;
            case R.id.action_logout:
      			
//      			Intent intent = new Intent(MainActivity.this, BeaconsMonitoringService.class);
//      		    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            	
            	Intent intent = new Intent(MainActivity.this, BeaconsMonitoringService.class);
				stopService(intent);
      		    
      		    SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
      		    SharedPreferences.Editor editor = settings.edit();
      		    editor.putString("username", "");
      		    editor.commit();
      		    
      		    Toast.makeText(this, "Hasta pronto "+user+"!", Toast.LENGTH_SHORT).show();
      		    
      		    //Simulamos el tiempo de conexión al servidor...
    			SystemClock.sleep(500);
    			
    			System.exit(0);
      			
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
//    BeaconsMonitoringService localService;
//    private ServiceConnection mConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// TODO Auto-generated method stub
//			BeaconsMonitoringService binder = (BeaconsMonitoringService) service;
//			binder.stopSelf();
//
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
////			System.exit(0);
//		}
//    };
    
    


//  private void connectToService() {
//    getActionBar().setSubtitle("Scanning...");
//    adapter.replaceWith(Collections.<Beacon>emptyList());
//    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//      @Override
//      public void onServiceReady() {
////        try {
//      	  beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
//
//            beaconManager.setMonitoringListener(new MonitoringListener() {
//              @Override
//              public void onEnteredRegion(Region region, List<Beacon> beacons) {
//            	  Log.i("BEACOON ", String.valueOf(beacons.get(1).getMinor()));
//            	  getActionBar().setSubtitle("Found beacons: " + beacons.size());
//            	  adapter.replaceWith(beacons);
//            	for (Beacon beacon: beacons){
//            		Log.i("BEACOON ", String.valueOf(beacon.getMinor()));
//            		if (beacon.getMinor() == 64444) {
////            			postNotification("Entered Ushuaia region");
//            			// 1. Instantiate an AlertDialog.Builder with its constructor
//            			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//            			// 2. Chain together various setter methods to set the dialog characteristics
//            			builder.setMessage("Bienvenido ***! Sólo por estar aquí has ganado 2 entradas para el Cocacola Music Xperience")
//            			       .setTitle("Premio!");
//            			
//            			// Add the buttons
//            			builder.setPositiveButton("OK. Vamos!", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User clicked OK button
//            			        	   Intent i = new Intent(MainActivity.this, WebViewActivity.class);
//            		                   i.putExtra("web", "http://www.ushuaiaibiza.com/");
//            		                   startActivity(i);
//            			           }
//            			       });
//            			builder.setNegativeButton("Ahora no, gracias", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User cancelled the dialog
//            			           }
//            			       });
//
//            			// 3. Get the AlertDialog from create()
//            			AlertDialog dialog = builder.create();
//            			
//            			dialog.show();
//            		} else if (beacon.getMinor() == 36328) {
////            			postNotification("Entered SMT region");
//            			// 1. Instantiate an AlertDialog.Builder with its constructor
//            			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//            			// 2. Chain together various setter methods to set the dialog characteristics
//            			builder.setMessage("Bienvenido a SMT, deseas ver nuestro video?")
//            			       .setTitle("Hola!");
//            			
//            			// Add the buttons
//            			builder.setPositiveButton("OK. Vamos!", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User clicked OK button
//            			        	   Intent i = new Intent(MainActivity.this, WebViewActivity.class);
//            		                   i.putExtra("web", "http://www.youtube.com/");
//            		                   startActivity(i);
//            			           }
//            			       });
//            			builder.setNegativeButton("Ahora no, gracias", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User cancelled the dialog
//            			           }
//            			       });
//
//            			// 3. Get the AlertDialog from create()
//            			AlertDialog dialog = builder.create();
//            			
//            			dialog.show();
//            		} else if (beacon.getMinor() == 31394) {
////            			postNotification("Entered SMT2 region");
//            			// 1. Instantiate an AlertDialog.Builder with its constructor
//            			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//            			// 2. Chain together various setter methods to set the dialog characteristics
//            			builder.setMessage("Bienvenido a Ushuaia Ibiza, si te acercas a la sala 5 tendrás un curso de peluquería gratuito")
//            			       .setTitle("Hola!");
//            			
//            			// Add the buttons
//            			builder.setPositiveButton("OK. Vamos!", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User clicked OK button
//            			        	   Intent i = new Intent(MainActivity.this, WebViewActivity.class);
//            		                   i.putExtra("web", "http://www.ushuaiaibiza.com/");
//            		                   startActivity(i);
//            			           }
//            			       });
//            			builder.setNegativeButton("Ahora no, gracias", new DialogInterface.OnClickListener() {
//            			           public void onClick(DialogInterface dialog, int id) {
//            			               // User cancelled the dialog
//            			           }
//            			       });
//
//            			// 3. Get the AlertDialog from create()
//            			AlertDialog dialog = builder.create();
//            			
//            			dialog.show();
//            		}
//            	}
//              }
//
//              @Override
//              public void onExitedRegion(Region region) {
////                postNotification("Exited region");
//        			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//        			builder.setMessage("Hasta pronto!")
//        			       .setTitle("Adios!");
//        			
//        			builder.setPositiveButton("Ver web", new DialogInterface.OnClickListener() {
//        			           public void onClick(DialogInterface dialog, int id) {
//        			               // User clicked OK button
//        			        	   Intent i = new Intent(MainActivity.this, WebViewActivity.class);
//        		                   i.putExtra("web", "http://www.ushuaiaibiza.com/");
//        		                   startActivity(i);
//        			           }
//        			       });
//        			builder.setNegativeButton("Adios!", new DialogInterface.OnClickListener() {
//        			           public void onClick(DialogInterface dialog, int id) {
//        			               // User cancelled the dialog
//        			           }
//        			       });
//
//        			AlertDialog dialog = builder.create();
//        			
//        			dialog.show();
//              }
//            });  
//      
//      }
//    });
//  }
	
}
