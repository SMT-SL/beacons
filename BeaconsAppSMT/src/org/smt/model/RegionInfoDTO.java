package org.smt.model;

import java.util.Date;

public class RegionInfoDTO {
	private int major;
	private int minor;
	private boolean isExistedFromRegion;
	private long  existTime;
	
	public RegionInfoDTO(int major, int minor) {
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

	public boolean isExistedFromRegion() {
		return isExistedFromRegion;
	}

	public void setExistedFromRegion(boolean isExistedFromRegion) {
		this.isExistedFromRegion = isExistedFromRegion;
	}

	/**
	 * @return the existTime
	 */
	public long getExistTime() {
		return existTime;
	}

	/**
	 * @param existTime the existTime to set
	 */
	public void setExistTime(long existTime) {
		this.existTime = existTime;
	}
	
}
