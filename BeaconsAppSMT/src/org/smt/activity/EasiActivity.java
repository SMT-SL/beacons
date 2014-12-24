package org.smt.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.smt.R;
import org.smt.adapters.PromotionListAdapter;
import org.smt.app.BeaconsApp;
import org.smt.model.RegionInfoDTO;
import org.smt.model.OfferDetailsDTO;
import org.smt.tasks.CheckPromocionesTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationClient;
//import org.smt.easibeacons.IBeacon;
//import org.smt.easibeacons.IBeaconListener;
//import org.smt.easibeacons.IBeaconProtocol;
//import org.smt.services.AltBeaconService;
//import org.smt.services.BeaconsMonitoringService;
//import com.smt.beacons.services.StarterService;

public class EasiActivity extends Activity implements BeaconConsumer, BootstrapNotifier, RangeNotifier {

	public static final int REQUEST_BLUETOOTH_ENABLE = 1;
	private CheckBox chkIos, chkAndroid;
	private ListView listBeacons;

	// private BeaconsAdapter _beaconsAdapter;
	// private static IBeaconProtocol _ibp;

	private static ArrayList<Beacon> _beacons;
	public static ArrayList<OfferDetailsDTO> promotions;
	public static PromotionListAdapter promotionsAdapter;
	private List<RegionInfoDTO> list = new ArrayList<RegionInfoDTO>();
	private Messenger serviceMessenger = null;
	private Menu _menu;
	private final static int REQUEST_ENABLE_BT = 1;
	private boolean saliendo;
	private ProgressBar spinner;
	private TextView txtState;

	java.util.Timer timer;

	Location mCurrentLocation;
	LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easi);

		promotions = new ArrayList<OfferDetailsDTO>();

		if (_beacons == null)
			_beacons = new ArrayList<Beacon>();

		promotionsAdapter = new PromotionListAdapter(this);

		listBeacons = (ListView) findViewById(R.id.listView1);
		spinner = (ProgressBar) findViewById(R.id.pbHeaderProgress);
		listBeacons.setAdapter(promotionsAdapter);
		txtState = (TextView) findViewById(R.id.txtState);

		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (locationProviders.contains("gps") || locationProviders.contains("network")) {

			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
		}

		listBeacons.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// Toast.makeText(EasiActivity.this,
				// String.valueOf(((IBeacon)arg0.getAdapter().getItem(arg2)).getMinor()),
				// Toast.LENGTH_SHORT).show();
				saliendo = false;

				if (((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferType() != 2) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferURL())));
				} else {
					Intent i = new Intent(EasiActivity.this, ImageActivity.class);
					String web = ((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferURL();
					i.putExtra("image", web);
					startActivity(i);
				}

			}
		});

		actualizarEstadoMensajesError();
		saliendo = true;

	}

	@Override
	protected void onStop() {
		((BeaconsApp) this.getApplication()).setEasyActivity(null);
		super.onStop();
	}

	@Override
	protected void onPause() {
		((BeaconsApp) this.getApplication()).setEasyActivity(null);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// ((BeaconsApp)this.getApplication()).setEasyActivity(this);
		// //----------------------------------------check this error
		List<RegionInfoDTO> tempRangeList = ((BeaconsApp) this.getApplication()).getRangeList();
		if (!tempRangeList.equals(list)) {
			list = tempRangeList;
			if (mCurrentLocation == null) {
				LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				String locationProvider = LocationManager.NETWORK_PROVIDER;
				mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
			}
			if (mCurrentLocation != null) {
				new CheckPromocionesTask(this, list, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
			}
			actualizarEstadoMensajesError();
		}
		actualizarEstadoMensajesError();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
			if (resultCode == Activity.RESULT_OK) {
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
		// case R.id.action_refresh:
		// scanBeacons();
		// return true;

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

	private void scanBeacons() {
		_beacons.clear();
		list.clear();

	}

	@SuppressLint("InflateParams")
	private void startRefreshAnimation() {
		// MenuItem item = _menu.findItem(R.id.action_refresh);
		// LayoutInflater inflater =
		// (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// ImageView iv = (ImageView)inflater.inflate(R.layout.refresh_button,
		// null);
		// Animation rotation = AnimationUtils.loadAnimation(this,
		// R.anim.rotate_refresh);
		// rotation.setRepeatCount(Animation.INFINITE);
		// iv.startAnimation(rotation);
		// item.setActionView(iv);
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.checkLocal:
			if (checked) {
				activarLocation(); // Activamos Location
			}
			break;
		case R.id.checkBlueTooth:
			if (checked) {
				activarBluetooth();
			}

			break;
		}
		actualizarEstadoMensajesError();
	}

	// private void stopRefreshAnimation(){
	// try{
	// MenuItem item = _menu.findItem(R.id.action_refresh);
	// if(item.getActionView()!=null){
	// // Remove the animation.
	// item.getActionView().clearAnimation();
	// item.setActionView(null);
	// }
	// } catch (Exception e){
	// Log.e("Easi Activity stopRefreshAnimation", e.getMessage().toString());
	// }

	// }

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	// @Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, 9000);
				// CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {

			Toast.makeText(this, "Error with location service code:" + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
		}

	}

	public void didEnterRegion(Region region) {
		list.add(new RegionInfoDTO(region.getId2() != null ? region.getId2().toInt() : 0, region.getId3() != null ? region.getId3().toInt() : 0));
		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (locationProviders.contains("gps") || locationProviders.contains("network")) {

			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
			if (mCurrentLocation == null) {
				locationProvider = LocationManager.GPS_PROVIDER;

				mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
			}
		}
		if (mCurrentLocation != null) {

			new CheckPromocionesTask(this, list, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
		}
	}

	public void didExitRegion(Region region) {

		Log.e("Region Exit ", "Major: " + region.getId2().toString() + " Minor: " + region.getId3().toString());
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getMajor() == region.getId2().toInt() && region.getId3().toInt() == list.get(i).getMinor()) {
					list.remove(i);
				}
			}

		}

	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {

	}

	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBeaconServiceConnect() {
		// TODO Auto-generated method stub

	}

	private void activarLocation() {
		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if gps is disabled and network location services are disabled
		// the user has no location services and must enable something
		if (!locationProviders.contains("gps") && !locationProviders.contains("network")) {

			// build a new alert dialog to inform the user that they have no
			// location services enabled
			new AlertDialog.Builder(this)

			// set the message to display to the user
					.setMessage("No Location Services Enabled")
					// add the 'positive button' to the dialog and give it a
					// click listener

					.setPositiveButton("Enable Location Services", new DialogInterface.OnClickListener() {
						// setup what to do when clicked
						public void onClick(DialogInterface dialog, int id) {
							// start the settings menu on the correct
							// screen for the user
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
						// add the 'negative button' to the dialog and
						// give it a click listener
					})

					.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						// setup what to do when clicked
						public void onClick(DialogInterface dialog, int id) {
							// remove the dialog
							dialog.cancel();
							// finish();

						}
						// finish creating the dialog and show to the
						// user
					}).create().show();
		}

	}

	private void activarBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			// Device does not support Bluetooth
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	private void actualizarEstadoMensajesError() {
		RelativeLayout bluetoothMessage = (RelativeLayout) findViewById(R.id.blueToothError);
		final RelativeLayout locationMessage = (RelativeLayout) findViewById(R.id.localizacionError);
		if (isLocationActivado()) {
			locationMessage.setVisibility(View.GONE);
		} else {
			locationMessage.setVisibility(View.VISIBLE);
		}

		if (isBloothActivated()) {
			bluetoothMessage.setVisibility(View.GONE);
		} else {
			bluetoothMessage.setVisibility(View.VISIBLE);
		}
	}

	private boolean isLocationActivado() {
		String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!locationProviders.contains("gps") && !locationProviders.contains("network")) {
			return false;
		}
		return true;
	}

	private boolean isBloothActivated() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			return false;
		}
		return true;
	}

}