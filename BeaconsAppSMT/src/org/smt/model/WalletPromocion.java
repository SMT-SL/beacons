package org.smt.model;

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
