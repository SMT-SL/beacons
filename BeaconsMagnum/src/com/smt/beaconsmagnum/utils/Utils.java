package com.smt.beaconsmagnum.utils;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils.Proximity;

public class Utils {
	public static double computeAccuracy(Beacon beacon)
	  {
	     if (beacon.getRssi() == 0) 
	     {
	        return -1.0D;
	     }

	     double ratio = beacon.getRssi() / beacon.getMeasuredPower();
	     double rssiCorrection = 0.96D + Math.pow(Math.abs(beacon.getRssi()), 3.0D) % 10.0D / 150.0D;

	     if (ratio <= 1.0D) 
	     {
	        return Math.pow(ratio, 9.98D) * rssiCorrection;
	     }
	     return (0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection;
	  }

	  public static Proximity proximityFromAccuracy(double accuracy)
	  {
	     if (accuracy < 0.0D) 
	     {
	         return Proximity.UNKNOWN;
	     }
	     if (accuracy < 0.5D) 
	     {
	         return Proximity.IMMEDIATE;
	     }
	     if (accuracy <= 3.0D) {
	         return Proximity.NEAR;
	     }
	     return Proximity.FAR;
	 }



	  public static Proximity computeProximity(Beacon beacon) {
	      return proximityFromAccuracy(computeAccuracy(beacon));
	  }

}
