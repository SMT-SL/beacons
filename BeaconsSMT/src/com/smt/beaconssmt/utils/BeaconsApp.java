package com.smt.beaconssmt.utils;


import android.app.Application;

import com.estimote.sdk.BeaconManager;

public class BeaconsApp  extends Application { 
	
	public static String PREFS_NAME = "beaconsAppPrefs";
	
	     private BeaconManager bm = new BeaconManager(this);
	     
	     public BeaconManager getBm(){
	    	 return bm;
	     }
}
