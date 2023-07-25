package com.fw.bean;

import java.util.List;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public class LoginResultBean {

	private String uniqueLDAPId;
	private String employeeNumber;
	private String userName;
	private String email;
	private List<String> userOrganizationalRole;
	private List<String> userOrganizations;
	private List<String> userApplicationsAndRoles;
	private String defaultOrganization;
	private String assignedProjectIds;

	private String token;

	public String getUniqueLDAPId() {
		return uniqueLDAPId;
	}

	public void setUniqueLDAPId(String uniqueLDAPId) {
		this.uniqueLDAPId = uniqueLDAPId;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<String> getUserOrganizationalRole() {
		return userOrganizationalRole;
	}

	public void setUserOrganizationalRole(List<String> userOrganizationalRole) {
		this.userOrganizationalRole = userOrganizationalRole;
	}

	public List<String> getUserOrganizations() {
		return userOrganizations;
	}

	public void setUserOrganizations(List<String> userOrganizations) {
		this.userOrganizations = userOrganizations;
	}

	public String getDefaultOrganization() {
		return defaultOrganization;
	}

	public void setDefaultOrganization(String defaultOrganization) {
		this.defaultOrganization = defaultOrganization;
	}

	public List<String> getUserApplicationsAndRoles() {
		return userApplicationsAndRoles;
	}

	public void setUserApplicationsAndRoles(
			List<String> userApplicationsAndRoles) {
		this.userApplicationsAndRoles = userApplicationsAndRoles;
	}

	public String getAssignedProjectIds() {
		return assignedProjectIds;
	}

	public void setAssignedProjectIds(String assignedProjectIds) {
		this.assignedProjectIds = assignedProjectIds;
	}
}
