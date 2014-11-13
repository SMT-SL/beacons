package com.smt.beacons.model;
import java.util.Date;

import com.smt.beacons.easibeacons.IBeacon;


public class IBeaconsFound {
	private IBeacon beacon;
	private Date date;
	
	public IBeaconsFound(IBeacon beacon, Date date) {
		super();
		this.beacon = beacon;
		this.date = date;
	}
	
	public IBeacon getBeacon() {
		return beacon;
	}
	
	public void setBeacon(IBeacon beacon) {
		this.beacon = beacon;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
}
