package com.smt.beaconsmagnum.model;



import java.util.Date;

import com.estimote.sdk.Beacon;

public class BeaconMagnum {
	
	private Beacon beacon;
	private Date date;
	
	public BeaconMagnum(){
		
	}
	
	public BeaconMagnum(Beacon beacon, Date date) {
		super();
		this.beacon = beacon;
		this.date = date;
	}
	public Beacon getBeacon() {
		return beacon;
	}
	public void setBeacon(Beacon beacon) {
		this.beacon = beacon;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
