package com.scanchex.bo;

import java.util.ArrayList;

public class AssetsTicketsInfo {
	
	public String assetId;
	public String assetAddressTwo;
	public String assetLongitude;
	public String assetlatitude;
	public String assetTechnician;
	public String assetType;
	public String assetPhotoUrl;
	public String thumbPhotoUrl;
	public String assetDescription;
	public String assetCode;
	public String assetUNAssetId;
	public String assetTolerance;
	public String assetClientName;
	public String assetContact;
	public String assetPosition;
	public String assetPhone;
	public String assetDepartment;
	public String assetSerialKey;
	
	//AddressInfo
	public String addressStreet;
	public String addressCity;
	public String addressState;
	public String addressPostalCode;
	public String addressCountry;
	
	//TicketInfo
	public String ticketTableId;
	public String ticketId;
	public String ticketStartDate;
	public String ticketStartTime;
	public String ticketStatus;
	public String ticketOverDue;
	public int ticketNumberOfScans;
	public String ticketIsService;
	public String notes;
	public String reference;
	public String allowIdCardScan;
	public String ticket_type;
	public String employee;
	public ArrayList<ScCheckPoints> checkPoints;
	
	public String ticket_start_time;
	public String ticket_end_time;
	public String ticket_total_time;
	public String ticketTimeStamp;
	
	public AssetsTicketsInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getAssetReference() {
		return reference;
	}

	public void setAssetference(String reference) {
		this.reference = reference;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getAssetAddressTwo() {
		return assetAddressTwo;
	}

	public void setAssetAddressTwo(String assetAddressTwo) {
		this.assetAddressTwo = assetAddressTwo;
	}

	public String getAssetLongitude() {
		return assetLongitude;
	}

	public void setAssetLongitude(String assetLongitude) {
		this.assetLongitude = assetLongitude;
	}

	public String getAssetlatitude() {
		return assetlatitude;
	}

	public void setAssetlatitude(String assetlatitude) {
		this.assetlatitude = assetlatitude;
	}

	public String getAssetTechnician() {
		return assetTechnician;
	}

	public void setAssetTechnician(String assetTechnician) {
		this.assetTechnician = assetTechnician;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetPhotoUrl() {
		return assetPhotoUrl;
	}

	public void setAssetPhotoUrl(String assetPhotoUrl) {
		this.assetPhotoUrl = assetPhotoUrl;
	}

	public String getThumbPhotoUrl() {
		return thumbPhotoUrl;
	}

	public void setThumbPhotoUrl(String thumbPhotoUrl) {
		this.thumbPhotoUrl = thumbPhotoUrl;
	}

	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public String getTicketType() {
		return ticket_type;
	}

	public void setTicketType(String ticket_type) {
		this.ticket_type = ticket_type;
	}

	public String getAssetUNAssetId() {
		return assetUNAssetId;
	}

	public void setAssetUNAssetId(String assetUNAssetId) {
		this.assetUNAssetId = assetUNAssetId;
	}

	public String getAssetTolerance() {
		return assetTolerance;
	}

	public void setAssetTolerance(String assetTolerance) {
		this.assetTolerance = assetTolerance;
	}

	public String getAssetClientName() {
		return assetClientName;
	}

	public void setAssetClientName(String assetClientName) {
		this.assetClientName = assetClientName;
	}

	public String getAssetContact() {
		return assetContact;
	}

	public void setAssetContact(String assetContact) {
		this.assetContact = assetContact;
	}

	public String getAssetPosition() {
		return assetPosition;
	}

	public void setAssetPosition(String assetPosition) {
		this.assetPosition = assetPosition;
	}

	public String getAssetPhone() {
		return assetPhone;
	}

	public void setAssetPhone(String assetPhone) {
		this.assetPhone = assetPhone;
	}

	public String getAssetDepartment() {
		return assetDepartment;
	}

	public void setAssetDepartment(String assetDepartment) {
		this.assetDepartment = assetDepartment;
	}

	public String getAssetSerialKey() {
		return assetSerialKey;
	}

	public void setAssetSerialKey(String assetSerialKey) {
		this.assetSerialKey = assetSerialKey;
	}

	public String getAddressStreet() {
		return addressStreet;
	}

	public void setAddressStreet(String addressStreet) {
		this.addressStreet = addressStreet;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public String getAddressState() {
		return addressState;
	}

	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}

	public String getAddressPostalCode() {
		return addressPostalCode;
	}

	public void setAddressPostalCode(String addressPostalCode) {
		this.addressPostalCode = addressPostalCode;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public String getTicketTableId() {
		return ticketTableId;
	}

	public void setTicketTableId(String ticketTableId) {
		this.ticketTableId = ticketTableId;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketStartDate() {
		return ticketStartDate;
	}

	public void setTicketStartDate(String ticketStartDate) {
		this.ticketStartDate = ticketStartDate;
	}

	public String getTicketStartTime() {
		return ticketStartTime;
	}

	public void setTicketStartTime(String ticketStartTime) {
		this.ticketStartTime = ticketStartTime;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getTicketOverDue() {
		return ticketOverDue;
	}

	public void setTicketOverDue(String ticketOverDue) {
		this.ticketOverDue = ticketOverDue;
	}

	public int getTicketNumberOfScans() {
		return ticketNumberOfScans;
	}

	public void setTicketNumberOfScans(int ticketNumberOfScans) {
		this.ticketNumberOfScans = ticketNumberOfScans;
	}

	public String getTicketIsService() {
		return ticketIsService;
	}

	public void setTicketIsService(String ticketIsService) {
		this.ticketIsService = ticketIsService;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAllowIdCardScan() {
		return allowIdCardScan;
	}

	public void setAllowIdCardScan(String allowIdCardScan) {
		this.allowIdCardScan = allowIdCardScan;
	}

	public ArrayList<ScCheckPoints> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(ArrayList<ScCheckPoints> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public String getTicket_start_time() {
		return ticket_start_time;
	}

	public void setTicket_start_time(String ticket_start_time) {
		this.ticket_start_time = ticket_start_time;
	}

	public String getTicket_end_time() {
		return ticket_end_time;
	}

	public void setTicket_end_time(String ticket_end_time) {
		this.ticket_end_time = ticket_end_time;
	}

	public String getTicket_total_time() {
		return ticket_total_time;
	}

	public void setTicket_total_time(String ticket_total_time) {
		this.ticket_total_time = ticket_total_time;
	}

	public String getTicketTimeStamp() {
		return ticketTimeStamp;
	}

	public void setTicketTimeStamp(String ticketTimeStamp) {
		this.ticketTimeStamp = ticketTimeStamp;
	}
	
	
	
	

}
