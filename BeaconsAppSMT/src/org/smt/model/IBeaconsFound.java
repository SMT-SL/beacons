package org.smt.model;
import java.util.Date;

import org.altbeacon.beacon.Beacon;

public class IBeaconsFound {
	private Beacon beacon;
	private Date date;
	
	public IBeaconsFound(Beacon beacon, Date date) {
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
