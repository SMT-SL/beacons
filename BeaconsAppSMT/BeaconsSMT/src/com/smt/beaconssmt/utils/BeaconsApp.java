package com.smt.beaconssmt.utils;


import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

public class BeaconsApp  extends Application { 
	
	public static String PREFS_NAME = "beaconsAppPrefs";
	public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("reg", null, null, null);
	public static final Region BEACONS_REGION_1 = new Region("r1", null, null, 64444);
	public static final Region BEACONS_REGION_2 = new Region("r2", null, null, 36328);
	public static final Region BEACONS_REGION_3 = new Region("r3", null, null, 31394);
	
	     private BeaconManager bm = new BeaconManager(this);
	     
	     public BeaconManager getBm(){
	    	 return bm;
	     }
}
