package com.tourist;

public class Locality {
	
	private int iLOC_ID;
	private String sLOC_Title;
	private String sLOC_Address;
	private String sLOC_Phone;
	private String sLOC_Website;
	private double dLOC_Lat;
	private double dLOC_Lng;
	private String sLOC_Description;
	private String sLOC_PhotoIcon;
	
	public Locality() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Locality(int iLOCID, String sLOCTitle, String sLOCAddress, String sLOCPhone,
			 String sLOCWebsite, double dLOCLat, double dLOCLng, String sLOCDescription, String sLOCPhotoIcon) {
		super();
		iLOC_ID = iLOCID;
		sLOC_Title = sLOCTitle;
		sLOC_Address = sLOCAddress;
		sLOC_Phone = sLOCPhone;
		sLOC_Website = sLOCWebsite;
		dLOC_Lat = dLOCLat;
		dLOC_Lng = dLOCLng;
		sLOC_Description = sLOCDescription;
		sLOC_PhotoIcon = sLOCPhotoIcon;
	}
	
	public int getID() {
		return iLOC_ID;
	}
	public void setID(int iLOCID) {
		iLOC_ID = iLOCID;
	}

	
	public String getTitle() {
		return sLOC_Title;
	}
	public void setTitle(String sLOCTitle) {
		sLOC_Title = sLOCTitle;
	}

	
	public String getAddress() {
		return sLOC_Address;
	}
	public void setAddress(String sLOCAddress) {
		sLOC_Address = sLOCAddress;
	}
	
	
	public String getPhone() {
		return sLOC_Phone;
	}
	public void setPhone(String sLOCPhone) {
		sLOC_Phone = sLOCPhone;
	}
	
	
	public String getWebsite() {
		return sLOC_Website;
	}
	public void setWebsite(String sLOCWebsite) {
		sLOC_Website = sLOCWebsite;
	}

	
	public double getLat() {
		return dLOC_Lat;
	}
	public void setLat(double dLOCLat) {
		dLOC_Lat = dLOCLat;
	}

	
	public double getLng() {
		return dLOC_Lng;
	}
	public void setLng(double dLOCLng) {
		dLOC_Lng = dLOCLng;
	}

	
	public String getDescription() {
		return sLOC_Description;
	}
	public void setDescription(String sLOCDescription) {
		sLOC_Description = sLOCDescription;
	}

	
	public String getPhotoIcon() {
		return sLOC_PhotoIcon;
	}
	public void setPhotoIcon(String sLOCPhotoIcon) {
		sLOC_PhotoIcon = sLOCPhotoIcon;
	}

}
