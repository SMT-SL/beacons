package org.smt.model;

public class BeaconInfoDTO {
	private int major;
	private int minor;
	
	public BeaconInfoDTO(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}
	
}
