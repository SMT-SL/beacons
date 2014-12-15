package org.smt.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OfferDetailsDTO {

	private int offerId;
	private String name;
	private String description;
	private String thumbnail;
	private int offerType;
	private String offerURL;

	public OfferDetailsDTO(JSONObject object) {
		try {
			offerId = object.getInt("offerId");
			name = object.getString("name");
			thumbnail = object.getString("thumbnail");
			offerType = object.getInt("offerType");
			offerURL = object.getString("offerURL");
			description = object.getString("offerDescription");
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

}
