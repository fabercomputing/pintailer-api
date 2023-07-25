package com.fw.bean;

public class ContactDetailUserInfo {

	@SuppressWarnings("unused")
	private String uniqueLDAPId;
	@SuppressWarnings("unused")
	private String employeeNumber;
	@SuppressWarnings("unused")
	private String firstName;
	@SuppressWarnings("unused")
	private String lastName;
	private String fullName;
	private String email;
	@SuppressWarnings("unused")
	private String emergencyContactName;
	@SuppressWarnings("unused")
	private String emergencyContactNumber;
	private String contactNumber;
	@SuppressWarnings("unused")
	private String grid;
	@SuppressWarnings("unused")
	private String postalAddress;
	@SuppressWarnings("unused")
	private String userName;
	@SuppressWarnings("unused")
	private String groups;
	
	public String getFullName() {
		return fullName;
	}
	public String getEmail() {
		return email;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setUniqueLDAPId(String uniqueLDAPId) {
		this.uniqueLDAPId = uniqueLDAPId;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setEmergencyContactName(String emergencyContactName) {
		this.emergencyContactName = emergencyContactName;
	}
	public void setEmergencyContactNumber(String emergencyContactNumber) {
		this.emergencyContactNumber = emergencyContactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public void setGrid(String grid) {
		this.grid = grid;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}

	
}
