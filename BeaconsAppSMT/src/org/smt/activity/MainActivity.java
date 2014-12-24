package org.smt.activity;

import java.util.ArrayList;
import java.util.List;

import org.altbeacon.beacon.Region;
import org.smt.R;
import org.smt.adapters.NavDrawerListAdapter;
import org.smt.app.BeaconsApp;
import org.smt.fragments.PerfilFragment;
import org.smt.fragments.PromocionesFragment;
import org.smt.fragments.WalletFragment;
import org.smt.model.RegionInfoDTO;
import org.smt.model.NavDrawerItem;
import org.smt.model.OfferDetailsDTO;
import org.smt.model.WalletPromocion;
import org.smt.tasks.CheckPromocionesTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
;

public class MainActivity extends Activity  {
	public static  List<RegionInfoDTO> regionsEncontrado = new ArrayList<RegionInfoDTO>();
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public static Location mCurrentLocation;
//	private LocationClient mLocationClient;
	// nav drawer title
	private CharSequence mDrawerTitle;
	private final static int REQUEST_ENABLE_BT = 1;
	private static final String TAG = "MainActivity findlocation";
	// used to store app title
	private CharSequence mTitle;
	private static ArrayList<WalletPromocion> walletPromocionList;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Promociones
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Wallet
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Perfil
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Desconectar
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		// nav menu toggle icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
		// nav drawer open - description for accessibility
				R.string.app_name
		// nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
//		mLocationClient = new LocationClient(this, this, this);
//		mLocationClient.connect();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav adf asdf asdf asdf asdf asdf asd
			// cvf asdf asdf asdf asdf asdf asdfa sdfdrawer item
			displayView(position);
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new PromocionesFragment(this);
			break;
		case 1:
			fragment = new WalletFragment(this);
			break;
		case 2:
			fragment = new PerfilFragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void didEnterRegion(Region region, Location mlocation) {
		regionsEncontrado.add(new RegionInfoDTO(region.getId2() != null ? region.getId2().toInt() : 0, region.getId3() != null ? region.getId3().toInt() : 0));
		if (mlocation == null) {
			mCurrentLocation=((BeaconsApp) this.getApplication()).getLocation();
		} else {
			mCurrentLocation = mlocation;
		}

		if (mCurrentLocation != null) {

			new CheckPromocionesTask(this, regionsEncontrado, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
		}else{
			((Activity) this).runOnUiThread(new Runnable() {
	    	    public void run() {
	    	    	((TextView) findViewById(R.id.txtState)).setText("No se ha podido obtener localizacion");
	     	    	    		
	    	    }
	    	});
			
		}
	}
	

	@Override
	protected void onResume() {
		((BeaconsApp) this.getApplication()).setEasyActivity(this);
		List<RegionInfoDTO> tempRangeList = ((BeaconsApp) this.getApplication()).getRangeList();
		if (mCurrentLocation == null) {
			mCurrentLocation=((BeaconsApp) this.getApplication()).getLocation();
		}
		if (!tempRangeList.equals(regionsEncontrado)) {
			regionsEncontrado = tempRangeList;
		}
		if (mCurrentLocation != null&& regionsEncontrado!=null &&regionsEncontrado.size()>0) {
				new CheckPromocionesTask(this, regionsEncontrado, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
			}else{
				((Activity) this).runOnUiThread(new Runnable() {
		    	    public void run() {
		    	    	((TextView) findViewById(R.id.txtState)).setText("No se ha podido obtener localizacion");
		     	    	    		
		    	    }
		    	});
			}
		

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		displayView(0);
		PromocionesFragment.actualizarEstadoApp();
		((BeaconsApp) super.getApplication()).clearAllNotificaciones();
		super.onResume();
	}

	public void didExitRegion(Region region) {

		Log.e("Region Exit ", "Major: " + region.getId2().toString() + " Minor: " + region.getId3().toString());
		if (regionsEncontrado != null && regionsEncontrado.size() > 0) {
			for (int i = 0; i < regionsEncontrado.size(); i++) {
				if (regionsEncontrado.get(i).getMajor() == region.getId2().toInt() && region.getId3().toInt() == regionsEncontrado.get(i).getMinor()) {
					regionsEncontrado.remove(i);
				}
			}

		}

		if(PromocionesFragment.promotions!=null&&PromocionesFragment.promotions.size()>0){
//			int i = users.size()-1; i >= 0; i--
			for(int i=PromocionesFragment.promotions.size()-1;i>=0;i--){
				if(PromocionesFragment.promotions.get(i).getMajor()==region.getId2().toInt()&&PromocionesFragment.promotions.get(i).getMinor()==region.getId3().toInt()){
					PromocionesFragment.promotions.remove(i);
				}
			}
		}
		
		if(PromocionesFragment.promotions.size()==0){
			PromocionesFragment.actualizarEstadoApp();
		}
		
		((Activity) this).runOnUiThread(new Runnable() {
    	    public void run() {
    	    	PromocionesFragment.promotionsAdapter.notifyDataSetChanged();
     	    	    		
    	    }
    	});
		
	}

//	private void actualizarEstadoMensajesError() {
//		RelativeLayout bluetoothMessage = (RelativeLayout) findViewById(R.id.blueToothError);
//		final RelativeLayout locationMessage = (RelativeLayout) findViewById(R.id.localizacionError);
////		txtState.setText("Comprobando la configuracion");
//		boolean isBluetoothOk=isBloothActivated();
//		boolean isLocationActivado=isLocationActivado();
//		boolean isConfiguracionOk=false;
//		
//		if (isLocationActivado && isBluetoothOk) {
//			locationMessage.setVisibility(View.GONE);
//			bluetoothMessage.setVisibility(View.GONE);
//			isConfiguracionOk=true;
//		} else if(!isLocationActivado && !isBluetoothOk) {
//			locationMessage.setVisibility(View.VISIBLE);
//			bluetoothMessage.setVisibility(View.VISIBLE);
//			isConfiguracionOk=false;
//			
//		}else if(!isLocationActivado){
//			locationMessage.setVisibility(View.VISIBLE);
//			bluetoothMessage.setVisibility(View.GONE);
//			isConfiguracionOk=false;
//			
//		}else if(!isBluetoothOk){
//			locationMessage.setVisibility(View.GONE);
//			bluetoothMessage.setVisibility(View.VISIBLE);
//			isConfiguracionOk=false;
//		}
//
//		if(!isConfiguracionOk){
////			txtState.setText("Error en configuracion, compruebalo ");
//		}
//		if(MainActivity.mCurrentLocation==null&&isConfiguracionOk){
////			txtState.setText("No se ha podido obtener localizacion");
//		}
//		
//		if(PromocionesFragment.promotions!=null && PromocionesFragment.promotions.size()>0&&isConfiguracionOk){
//			txtState.setText("Promicones encotnradas");
//			spinner.setVisibility(View.GONE);
//			
//		}
//	}

	private boolean isLocationActivado() {
		@SuppressWarnings("deprecation")
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
		PromocionesFragment.actualizarEstadoApp();
	}

	public void onEditTextClicked(View view) {
		EditText editText = ((EditText) view);// .setEnabled(false);
		editText.setEnabled(true);
		Button btnGuardar = (Button) findViewById(R.id.btnGuardar);
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnGuardar.setVisibility(View.VISIBLE);
		btnCancelar.setVisibility(View.VISIBLE);
	}

	private void activarLocation() {
		@SuppressWarnings("deprecation")
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

	public static ArrayList<WalletPromocion> getWalletPromocionList() {
		return walletPromocionList;
	}

	public static void setWalletPromocionList(ArrayList<WalletPromocion> walletPromocionList) {
		MainActivity.walletPromocionList = walletPromocionList;
	}
	public static void addToWalletList(OfferDetailsDTO promocionAguardar) {
		if(!isExistPromocion(promocionAguardar)){
			WalletPromocion wPromocionObject = new WalletPromocion();
			if (walletPromocionList == null) {
				walletPromocionList = new ArrayList<WalletPromocion>();
			}
			wPromocionObject.setDescription(promocionAguardar.getDescription());
			wPromocionObject.setLocation("");
			wPromocionObject.setName(promocionAguardar.getName());
			wPromocionObject.setOfferId(promocionAguardar.getOfferId());
			wPromocionObject.setOfferURL(promocionAguardar.getOfferURL());
			wPromocionObject.setThumbnail(promocionAguardar.getThumbnail());

			walletPromocionList.add(wPromocionObject);
		}
		
	}
	
	private static boolean isExistPromocion(OfferDetailsDTO promocionAguardar){
		Boolean existePromocion=false;
		if(walletPromocionList!=null&& walletPromocionList.size()>0){
			for(int i=0;i<walletPromocionList.size();i++){
				if(walletPromocionList.get(i).getOfferId()==promocionAguardar.getOfferId()){
					i=walletPromocionList.size();
					existePromocion=true;
				}
			}
		}
		
		return existePromocion;
	}

	public static void removeFromWalletList(WalletPromocion promo) {
		if(walletPromocionList!=null&& walletPromocionList.size()>0){
			for(int i=0;i<walletPromocionList.size();i++){
				if(walletPromocionList.get(i).getOfferId()==promo.getOfferId()){
					walletPromocionList.remove(i);
					WalletFragment.walletListAdapter.notifyDataSetChanged();
					
				}
			}
		}
		
	}

}
