package org.smt.fragments;

import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;
import org.smt.R;
import org.smt.adapters.PromotionListAdapter;
import org.smt.model.OfferDetailsDTO;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
//import org.smt.activity.EasiActivity;
//import org.smt.activity.ImageActivity;

public class PromocionesFragment extends Fragment {
	public static final int REQUEST_BLUETOOTH_ENABLE = 1;
	// private CheckBox chkIos, chkAndroid;
	private ListView listBeacons;

	// private BeaconsAdapter _beaconsAdapter;
	// private static IBeaconProtocol _ibp;

	private static ArrayList<Beacon> _beacons;
	public static ArrayList<OfferDetailsDTO> promotions;
	public static PromotionListAdapter promotionsAdapter;
	// private List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
	// private Messenger serviceMessenger = null;
	// private Menu _menu;
	// private final static int REQUEST_ENABLE_BT = 1;
	// private boolean saliendo;
	private ProgressBar spinner;
	private TextView txtState;
	private Context context;
	private View rootView;
	java.util.Timer timer;

	Location mCurrentLocation;
	LocationClient mLocationClient;

	public PromocionesFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		promotions = new ArrayList<OfferDetailsDTO>();

		if (_beacons == null)
			_beacons = new ArrayList<Beacon>();
		rootView = inflater.inflate(R.layout.fragment_promociones, container, false);
		promotionsAdapter = new PromotionListAdapter(context);

		listBeacons = (ListView) rootView.findViewById(R.id.listView1);
		setSpinner((ProgressBar) rootView.findViewById(R.id.pbHeaderProgress));
		listBeacons.setAdapter(promotionsAdapter);
		setTxtState((TextView) rootView.findViewById(R.id.txtState));

		@SuppressWarnings("deprecation")
		String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (locationProviders.contains("gps") || locationProviders.contains("network")) {

			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
		}

		listBeacons.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// Toast.makeText(EasiActivity.this,
				// String.valueOf(((IBeacon)arg0.getAdapter().getItem(arg2)).getMinor()),
				// Toast.LENGTH_SHORT).show();
				// saliendo = false;
				Toast.makeText(context, "Stop Clicking me", Toast.LENGTH_SHORT).show();
				if (((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferType() != 2) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferURL())));
				} else {
					// Intent i = new Intent(EasiActivity.this,
					// ImageActivity.class);
					// String web =
					// ((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL();
					// i.putExtra("image", web);
					// startActivity(i);
				}

			}
		});

		actualizarEstadoMensajesError();
		// saliendo = true;
		return rootView;
	}

	// private void activarLocation(){
	// String locationProviders =
	// Settings.Secure.getString(context.getContentResolver(),
	// Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	// // if gps is disabled and network location services are disabled
	// // the user has no location services and must enable something
	// if (!locationProviders.contains("gps") &&
	// !locationProviders.contains("network")) {
	//
	// // build a new alert dialog to inform the user that they have no
	// // location services enabled
	// new AlertDialog.Builder(context)
	//
	// //set the message to display to the user
	// .setMessage("No Location Services Enabled")
	// // add the 'positive button' to the dialog and give it a click listener
	//
	// .setPositiveButton("Enable Location Services", new
	// DialogInterface.OnClickListener() {
	// // setup what to do when clicked
	// public void onClick(DialogInterface dialog, int id) {
	// // start the settings menu on the correct screen for the user
	// startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	// }
	// // add the 'negative button' to the dialog and give it a click listener
	// })
	//
	// .setNegativeButton("Close", new DialogInterface.OnClickListener() {
	// // setup what to do when clicked
	// public void onClick(DialogInterface dialog,int id) {
	// // remove the dialog
	// dialog.cancel();
	// // finish();
	//
	// }
	// // finish creating the dialog and show to the user
	// }).create().show();
	// }
	//
	// }

	// private void activarBluetooth(){
	// BluetoothAdapter mBluetoothAdapter =
	// BluetoothAdapter.getDefaultAdapter();
	// if (mBluetoothAdapter != null) {
	// // Device does not support Bluetooth
	// if (!mBluetoothAdapter.isEnabled()) {
	// Intent enableBtIntent = new
	// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	// }
	// }
	// }
	private void actualizarEstadoMensajesError() {
		RelativeLayout bluetoothMessage = (RelativeLayout) rootView.findViewById(R.id.blueToothError);
		final RelativeLayout locationMessage = (RelativeLayout) rootView.findViewById(R.id.localizacionError);
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
		@SuppressWarnings("deprecation")
		String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
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

	// public void onCheckboxClicked(View view) {
	// // Is the view now checked?
	// boolean checked = ((CheckBox) view).isChecked();
	//
	// // Check which checkbox was clicked
	// switch(view.getId()) {
	// case R.id.checkLocal:
	// if (checked){
	// activarLocation(); //Activamos Location
	// }
	// break;
	// case R.id.checkBlueTooth:
	// if (checked){
	// activarBluetooth();
	// }
	//
	// break;
	// }
	// actualizarEstadoMensajesError();
	// }

	public TextView getTxtState() {
		return txtState;
	}

	public void setTxtState(TextView txtState) {
		this.txtState = txtState;
	}

	public ProgressBar getSpinner() {
		return spinner;
	}

	public void setSpinner(ProgressBar spinner) {
		this.spinner = spinner;
	}
}
