package org.smt.receivers;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

public class StartServiceAtBootReceiver extends BroadcastReceiver implements BeaconConsumer, BootstrapNotifier, RangeNotifier {

	private static final String TAG = "BeaconReferenceApplication";
	private BeaconManager mBeaconManager;
	private Region mAllBeaconsRegion;

	private BackgroundPowerSaver mBackgroundPowerSaver;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;

	@Override
	public void onReceive(Context context, Intent intent) {

		mAllBeaconsRegion = new Region("all beacons", null, null, null);

		mBeaconManager = BeaconManager.getInstanceForApplication(context);
		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")); // iBeacons

		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // Estimotes

		mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=a7ae2eb7,i:4-19,i:20-21,i:22-23,p:24-24")); // easiBeacons
		setmBackgroundPowerSaver(new BackgroundPowerSaver(context));
		mBeaconManager.setBackgroundBetweenScanPeriod(3000);
		mBeaconManager.setBackgroundBetweenScanPeriod(5000);
		mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);

	}

	@Override
	public boolean bindService(Intent arg0, ServiceConnection arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBeaconServiceConnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbindService(ServiceConnection arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didEnterRegion(Region arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didExitRegion(Region arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		Log.e(TAG, "******************** Doing in background .....................");

	}

	public BackgroundPowerSaver getmBackgroundPowerSaver() {
		return mBackgroundPowerSaver;
	}

	public void setmBackgroundPowerSaver(BackgroundPowerSaver mBackgroundPowerSaver) {
		this.mBackgroundPowerSaver = mBackgroundPowerSaver;
	}

}
