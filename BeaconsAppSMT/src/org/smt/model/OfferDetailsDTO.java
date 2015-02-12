package org.smt.model;

import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

public class OfferDetailsDTO {

	private int offerId;
	private String name;
	private String description;
	private String thumbnail;
	private int offerType;
	private String offerURL;
	private int major;
	private int minor;

	public OfferDetailsDTO(JSONObject object) {
		try {
			offerId = object.isNull("offerId")?0:object.getInt("offerId");
			name = object.isNull("name")?"":object.getString("name");
			thumbnail = object.isNull("thumbnail")?"":object.getString("thumbnail");
			offerType = object.isNull("offerType")?0:object.getInt("offerType");
			offerURL = object.isNull("offerURL")?"":object.getString("offerURL");
			description = object.isNull("offerDescription")?"":object.getString("offerDescription");
			major=object.isNull("major")?0:object.getInt("major");
			minor=object.isNull("minor")?0:object.getInt("minor");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getOfferId() {
		return offerId;
	}

	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getOfferType() {
		return offerType;
	}

	public void setOfferType(int offerType) {
		this.offerType = offerType;
	}

	public String getOfferURL() {
		return offerURL;
	}

	public void setOfferURL(String offerURL) {
		this.offerURL = offerURL;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OfferDetailsDTO) {
			if (((OfferDetailsDTO) o).getOfferId() == this.getOfferId()) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
