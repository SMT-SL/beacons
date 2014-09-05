package com.smt.beaconssmt.activities;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.smt.beaconssmt.PorraActivity;
import com.smt.beaconssmt.R;
import com.smt.beaconssmt.adapters.LeDeviceListAdapter;
import com.smt.beaconssmt.services.BeaconsMonitoringService;
import com.smt.beaconssmt.utils.BeaconsApp;


public class MainActivity extends Activity {
	
	private BeaconManager beaconManager;
	private LeDeviceListAdapter adapter;
	
	public static String user;
	
	private Button runInBackBtn;
	
	public static boolean saliendo = true;
	
	public static boolean isSaliendo() {
		return saliendo;
	}


	public static void setSaliendo(boolean saliendo) {
		MainActivity.saliendo = saliendo;
	}

	
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
//        beaconManager = new BeaconManager(this);
        BeaconsApp appState = (BeaconsApp)this.getApplication();
        beaconManager = appState.getBm();
        
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
        
//        runInBackBtn = (Button) findViewById(R.id.runInBackBtn);
//        runInBackBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
//				startService(intent);
//				System.exit(0);
//			}
//		});
        
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
//    	  try {
//    		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//    	  } catch (RemoteException e) {
//			e.printStackTrace();
//    	  }
//    	  Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
    	  Intent intent = new Intent(this, BeaconsMonitoringService.class);
          stopService(intent);
    	  connectToRangingService();
      }
      
    }

    @Override
    protected void onResume() {
      super.onResume();
//      try {
//    	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//      	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//  	  } catch (RemoteException e) {
//			e.printStackTrace();
//  	  }
//      Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
      Intent intent = new Intent(this, BeaconsMonitoringService.class);
      stopService(intent);
      connectToRangingService();
    }
    
    @Override
	protected void onPause() {
    	if (saliendo){
    		try {
    			beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//    			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//            	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//    			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//    			Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
    			Intent intent = new Intent(this, BeaconsMonitoringService.class);
    			startService(intent);
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}
    	}
		
		super.onPause();
	}
	
	 @Override
	    protected void onStop() {
		 if (saliendo){
			 try {
					beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//					beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//		        	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//					beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//					Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
					Intent intent = new Intent(this, BeaconsMonitoringService.class);
					startService(intent);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
		    	
		 }
		 super.onStop();
	    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == 1234) {
        if (resultCode == Activity.RESULT_OK) {
//        	try {
//        		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//            	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//    			beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//        	} catch (RemoteException e) {
//    			e.printStackTrace();
//        	}
//        	Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
        	Intent intent = new Intent(this, BeaconsMonitoringService.class);
            stopService(intent);
        	connectToRangingService();
        } else {
          Toast.makeText(this, "Bluetooth no activado", Toast.LENGTH_LONG).show();
          getActionBar().setSubtitle("Bluetooth no activado");
        }
      }
      super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
//      beaconManager.disconnect();
    	try {
			beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//			Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
			Intent intent = new Intent(this, BeaconsMonitoringService.class);
		      startService(intent);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
      super.onDestroy();
    }
    
    private void connectToRangingService() {
        getActionBar().setSubtitle("Buscando...");
        adapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
          @Override
          public void onServiceReady() {
            try {
              beaconManager.startRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
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
        	
        	String code = String.valueOf(adapter.getItem(position).getMinor());
			Intent targetIntent;
			if (code.equals("64444")){
				
				targetIntent = new Intent(MainActivity.this, WebViewActivity.class);
				targetIntent.putExtra("web", "http://musicexperience.cocacola.es/");
				startActivity(targetIntent);
				saliendo = false;
	       	  	
			} else if (code.equals("36328")){
				
				targetIntent = new Intent(MainActivity.this, PorraActivity.class);
				startActivity(targetIntent);
	   	  		saliendo=false;
	       	  	
			} else if (code.equals("31394")) {
				
				targetIntent = new Intent(Intent.ACTION_VIEW);
				targetIntent.setData(Uri.parse("market://details?id=com.whatsred.whatsred"));
				startActivity(targetIntent);
//				saliendo = false;
				
//				targetIntent = new Intent(MainActivity.this, PorraActivity.class);
//				startActivity(targetIntent);
	   	  		saliendo=false;
			}
        	
//              Intent intent = new Intent(MainActivity.this, BeaconDataActivity.class);
//              intent.putExtra("beacon", adapter.getItem(position));
//              startActivity(intent);
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

//	@Override
//	protected void onPause() {
//		try {
//			beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		super.onPause();
//	}
//
//
//
//	@Override
//    protected void onStart() {
//      super.onStart();
//
//      // Check if device supports Bluetooth Low Energy.
//      if (!beaconManager.hasBluetooth()) {
//        Toast.makeText(this, "El dispositivo no soporta Bluetooth Low Energy, los beacons no serán detectados", Toast.LENGTH_LONG).show();
//        return;
//      }
//
//      // If Bluetooth is not enabled, let user enable it.
//      if (!beaconManager.isBluetoothEnabled()) {
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableBtIntent, 1234);
//      } else {
//    	  try {
//    		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//    	  } catch (RemoteException e) {
//			e.printStackTrace();
//    	  }
//    	  connectToRangingService();
//      }
//      
//    }
//
//    @Override
//    protected void onStop() {
//    	try {
//			beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//    	super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//      super.onResume();
//      try {
//    	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//      	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//  	  } catch (RemoteException e) {
//			e.printStackTrace();
//  	  }
//      connectToRangingService();
//    }
//    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//      if (requestCode == 1234) {
//        if (resultCode == Activity.RESULT_OK) {
//        	try {
//        		beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_1);
//            	beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_2);
//    			beaconManager.stopMonitoring(BeaconsApp.BEACONS_REGION_3);
//        	} catch (RemoteException e) {
//    			e.printStackTrace();
//        	}
//        	connectToRangingService();
//        } else {
//          Toast.makeText(this, "Bluetooth no activado", Toast.LENGTH_LONG).show();
//          getActionBar().setSubtitle("Bluetooth no activado");
//        }
//      }
//      super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    protected void onDestroy() {
////      beaconManager.disconnect();
//    	try {
//			beaconManager.stopRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_1);
//        	beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_2);
//			beaconManager.startMonitoring(BeaconsApp.BEACONS_REGION_3);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//      super.onDestroy();
//    }
//    
//    private void connectToRangingService() {
//        getActionBar().setSubtitle("Buscando...");
//        adapter.replaceWith(Collections.<Beacon>emptyList());
//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//          @Override
//          public void onServiceReady() {
//            try {
//              beaconManager.startRanging(BeaconsApp.ALL_ESTIMOTE_BEACONS_REGION);
//            } catch (RemoteException e) {
//              Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
//                  Toast.LENGTH_LONG).show();
//            }
//          }
//        });
//      }
//
//    private AdapterView.OnItemClickListener createOnItemClickListener() {
//      return new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              Intent intent = new Intent(MainActivity.this, BeaconDataActivity.class);
//              intent.putExtra("beacon", adapter.getItem(position));
//              startActivity(intent);
//        }
//      };
//    }
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//    	
//        switch (item.getItemId()) {
//            
//            case R.id.action_profile:
//      			
//      			Intent i = new Intent(MainActivity.this, SettingsActivity.class);
//      			startActivity(i);
//      			
//                return true;
//            case R.id.action_logout:
//      			
////      			Intent intent = new Intent(MainActivity.this, BeaconsMonitoringService.class);
////      		    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//            	
//            	Intent intent = new Intent(MainActivity.this, BeaconsMonitoringService.class);
//				stopService(intent);
//      		    
//      		    SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
//      		    SharedPreferences.Editor editor = settings.edit();
//      		    editor.putString("username", "");
//      		    editor.commit();
//      		    
//      		    Toast.makeText(this, "Hasta pronto "+user+"!", Toast.LENGTH_SHORT).show();
//      		    
//      		    //Simulamos el tiempo de conexión al servidor...
//    			SystemClock.sleep(500);
//    			
//    			System.exit(0);
//      			
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
    
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
    
}
