package org.smt.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.smt.activity.LoginActivity;
import org.smt.activity.MainActivity;
import org.smt.model.BeaconInfoDTO;
import org.smt.model.IBeaconsFound;
import org.smt.tasks.CheckPromocionesTask;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BeaconsApp extends Application implements BootstrapNotifier, RangeNotifier, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static List<IBeaconsFound> notifiedBeacons;
	private LoginActivity loginActivity;
	private MainActivity easiActivity;
	// private EasiActivity easiActivity;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private static final String TAG = "BeaconsApp";
	private BeaconManager mBeaconManager;
	private Region mAllBeaconsRegion;
	private List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
	private BackgroundPowerSaver mBackgroundPowerSaver;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;
	private Location mCurrentLocation;
	private LocationClient mLocationClient;

	@Override
	public void onCreate() {

		notifiedBeacons = new ArrayList<IBeaconsFound>();

		options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options).build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		// ********************* Configuracion de Beacons
		// *************************
		mAllBeaconsRegion = new Region("all beacons", null, Identifier.fromInt(1), Identifier.fromInt(1));
		mBeaconManager = BeaconManager.getInstanceForApplication(this);
		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")); // iBeacons

		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // Estimotes

		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=a7ae2eb7,i:4-19,i:20-21,i:22-23,p:24-24")); // easiBeacons
		setmBackgroundPowerSaver(new BackgroundPowerSaver(this));
		mBeaconManager.setForegroundBetweenScanPeriod(6000);
		mBeaconManager.setBackgroundBetweenScanPeriod(9000);
		mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);
		// super.onCreate();
	}

	public boolean isAlreadyNotifiedBeacon(Beacon beacon, Date date) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && date.getTime() - notifiedBeacons.get(i).getDate().getTime() > 30000) {
				return true;
			}
		}
		return false;
	}

	public void addBeacon(Beacon beacon, Date date) {
		boolean exist = false;

		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).equals(beacon)) {
				exist = true;
			}
		}

		if (!exist) {
			notifiedBeacons.add(new IBeaconsFound(beacon, date));
		}

	}

	public void deleteBeacon(Beacon beacon) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon)) {
				notifiedBeacons.remove(i);
			}
		}
	}

	public void refreshBeacon(Beacon beacon, Date date) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && notifiedBeacons.get(i).getDate().after(date)) {
				notifiedBeacons.remove(i);
				notifiedBeacons.add(new IBeaconsFound(beacon, date));
			}
		}
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public DisplayImageOptions getDisplayImageOptions() {
		return options;
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didEnterRegion(Region region) {
		boolean isNewRegion = isNewRegion(region);
		if (isNewRegion) {
			list.add(new BeaconInfoDTO(region.getId2() != null ? region.getId2().toInt() : 0, region.getId3() != null ? region.getId3().toInt() : 0));
			if (easiActivity != null) {
				mCurrentLocation = mLocationClient.getLastLocation();
				easiActivity.didEnterRegion(region);
			} else {
				// Comprobar is esta activo ubicacion o no. Intentar mandar
				// notificacion si tenemos ubicacion activado. Si no tenemos
				// activado ubicacin no mandaremos notificacion.
				@SuppressWarnings("deprecation")
				String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

				if (locationProviders.contains("gps") || locationProviders.contains("network")) {
					LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
					String locationProvider = LocationManager.NETWORK_PROVIDER;
					mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);

					if (mCurrentLocation != null) {
						new CheckPromocionesTask(this, list, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
					}
				}

			}
		}

	}

	public List<BeaconInfoDTO> getRangeList() {
		return this.list;
	}

	public boolean isNewRegion(Region region) {
		Log.e("Beacon found", "Major: " + region.getId2() != null ? region.getId2().toString() : "null" + " Minor: " + region.getId3() != null ? region.getId3().toString() : "null");

		boolean exists = true;
		for (BeaconInfoDTO bi : list) {
			if (bi.getMajor() == (region.getId2() != null ? region.getId2().toInt() : 0) && (region.getId3() != null ? region.getId3().toInt() : 0) == bi.getMinor()) {
				exists = false;
			}
		}
		return exists;
	}

	@Override
	public void didExitRegion(Region region) {
		if (easiActivity != null) {
			easiActivity.didExitRegion(region);
		}
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
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		Log.e("BeaconsApp", "****************** beacons *****************");

	}

	public Location getLocation() {
		return this.mCurrentLocation;
	}

	public void setLoginActivity(LoginActivity activity) {
		loginActivity = activity;
	}

	public void setEasyActivity(MainActivity activity) {
		easiActivity = activity;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mCurrentLocation = mLocationClient.getLastLocation();

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public static String getTag() {
		return TAG;
	}

	public BackgroundPowerSaver getmBackgroundPowerSaver() {
		return mBackgroundPowerSaver;
	}

	public void setmBackgroundPowerSaver(BackgroundPowerSaver mBackgroundPowerSaver) {
		this.mBackgroundPowerSaver = mBackgroundPowerSaver;
	}
}
