package org.smt.model;

import org.json.JSONException;
import org.json.JSONObject;

public class WalletPromocion {

	private int offerId;
	private String name;
	private String description;
	private String thumbnail;
	private int offerType;
	private String offerURL;
	private String location;

	public WalletPromocion(OfferDetailsDTO wpromoicon) {
		this.setDescription(wpromoicon.getDescription());
		this.setLocation("");
		this.setName(wpromoicon.getName());
		this.setOfferId(wpromoicon.getOfferId());
		this.setOfferURL(wpromoicon.getOfferURL());
		this.setThumbnail(wpromoicon.getThumbnail());
	}

	public WalletPromocion(JSONObject object) {
		try {
			offerId = object.isNull("offerId")?0:object.getInt("offerId");
			name =object.isNull("name")?"": object.getString("name");
			thumbnail = object.isNull("thumbnail")?"":object.getString("thumbnail");
			offerType = object.isNull("type")?0:object.getInt("offerType");
			offerURL = object.isNull("offerURL")?"":object.getString("offerURL");
			description = object.isNull("offerDescription")?"":object.getString("offerDescription");
			location="";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public WalletPromocion() {

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
