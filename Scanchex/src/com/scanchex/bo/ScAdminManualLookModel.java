package com.scanchex.bo;

import java.util.ArrayList;

public class ScAdminManualLookModel {

	private String id;
	private String asset_id;
	private String department;
	private String description;
	private String address;
	private String asset_photo;
	private String asset_code;
	private String asset_url;
	private String serial_number;
	private String name;
	private String address_id;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String user_id;
	private String full_name;
	private String client_id;
	private String employee_url;
	private ArrayList<ScAdminManualLookModel> clientAddress = new ArrayList<ScAdminManualLookModel>();

	public ArrayList<ScAdminManualLookModel> getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(ArrayList<ScAdminManualLookModel> clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getEmployee_url() {
		return employee_url;
	}

	public void setEmployee_url(String employee_url) {
		this.employee_url = employee_url;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	// Display Spinner Name
	private String displaySpinnerName;

	public String getDisplaySpinnerName() {
		return displaySpinnerName;
	}

	public void setDisplaySpinnerName(String displaySpinnerName) {
		this.displaySpinnerName = displaySpinnerName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAsset_id() {
		return asset_id;
	}

	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAsset_photo() {
		return asset_photo;
	}

	public void setAsset_photo(String asset_photo) {
		this.asset_photo = asset_photo;
	}

	public String getAsset_code() {
		return asset_code;
	}

	public void setAsset_code(String asset_code) {
		this.asset_code = asset_code;
	}

	public String getAsset_url() {
		return asset_url;
	}

	public void setAsset_url(String asset_url) {
		this.asset_url = asset_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress_id() {
		return address_id;
	}

	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
