package com.smt.beaconsmagnum.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.estimote.sdk.Beacon;

public class BeaconsDiscovered {
	
	private List<BeaconMagnum> beacons;
	
	public BeaconsDiscovered(){
		beacons = new ArrayList<BeaconMagnum>();
	}
	
	public BeaconsDiscovered(List<BeaconMagnum> beacons) {
		this.beacons = beacons;
	}
	
	public void addBeacon(BeaconMagnum newBeacon){
		beacons.add(newBeacon);
	}
	
	public List<BeaconMagnum> getBeacon() {
		return beacons;
	}
	public void setBeacon(List<BeaconMagnum> beacon) {
		this.beacons = beacon;
	}
	
	public boolean isBeaconExisting(BeaconMagnum newBeacon){
		
		boolean existing = false;
		
		for (BeaconMagnum b: beacons){
			if (b.getBeacon().getMajor() == newBeacon.getBeacon().getMajor() &&
					b.getBeacon().getMinor() == newBeacon.getBeacon().getMinor() && 
					((newBeacon.getDate().getTime()-b.getDate().getTime())<100)){
				existing = true;
				break;
			}
		}
		if (!existing){
			addBeacon(newBeacon);
		}
		return existing;
	}
	
}
