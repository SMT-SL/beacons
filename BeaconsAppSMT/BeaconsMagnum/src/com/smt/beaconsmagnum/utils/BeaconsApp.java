package com.smt.beaconsmagnum.utils;


import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.smt.beaconsmagnum.model.BeaconsDiscovered;

public class BeaconsApp  extends Application { 
	
	public static String PREFS_NAME = "beaconsAppPrefs";
	public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("reg", null, null, null);
	public static final Region ALL_EASI_BEACONS_REGION = new Region("reg2", "A7AE2EB7-1F00-4168-B99B-A749BAC1CA64", 1, 1);
	public static final Region BEACONS_REGION_1 = new Region("r1", null, null, 64444);
	public static final Region BEACONS_REGION_2 = new Region("r2", null, null, 36328);
	public static final Region BEACONS_REGION_3 = new Region("r3", null, null, 31394);
	
	private static BeaconsDiscovered beaconsDiscovered;
	
	private BeaconManager bm;
	
	@Override
	public void onCreate() {
		beaconsDiscovered = new BeaconsDiscovered();
		bm = new BeaconManager(this);
		super.onCreate();
	}
	     
	public BeaconManager getBm(){
		return bm;
	}

	public static BeaconsDiscovered getBeaconsDiscovered() {
		return beaconsDiscovered;
	}

	public static void setBeaconsDiscovered(BeaconsDiscovered beaconsDiscovered) {
		BeaconsApp.beaconsDiscovered = beaconsDiscovered;
	}
}
