package com.fw.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {

	private static final long serialVersionUID = 1L;
	private String userOrganizationalRole;
	private String email;
	private String userOrganizations;
	private String userProjectRole;
	private String defaultOrg;
	private String assignedProjectIds;

	private String token;

	public CustomUser(String username, String password,
			Collection<? extends GrantedAuthority> authorities,
			String userOrganizationalRole, String email,
			String userOrganizations, String userProjectRole,
			String defaultOrg, String assignedProjectIds, String token) {
		super(username, password, authorities);
		this.userOrganizationalRole = userOrganizationalRole;
		this.email = email;
		this.userOrganizations = userOrganizations;
		this.userProjectRole = userProjectRole;
		this.defaultOrg = defaultOrg;
		this.assignedProjectIds = assignedProjectIds;
		this.setToken(token);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserOrganizationalRole() {
		return userOrganizationalRole;
	}

	public void setUserOrganizationalRole(String userOrganizationalRole) {
		this.userOrganizationalRole = userOrganizationalRole;
	}

	public String getUserOrganizations() {
		return userOrganizations;
	}

	public void setUserOrganizations(String userOrganizations) {
		this.userOrganizations = userOrganizations;
	}

	public String getDefaultOrg() {
		return defaultOrg;
	}

	public void setDefaultOrg(String defaultOrg) {
		this.defaultOrg = defaultOrg;
	}

	public String getUserProjectRole() {
		return userProjectRole;
	}

	public void setUserProjectRole(String userProjectRole) {
		this.userProjectRole = userProjectRole;
	}

	public String getAssignedProjectIds() {
		return assignedProjectIds;
	}

	public void setAssignedProjectIds(String assignedProjectIds) {
		this.assignedProjectIds = assignedProjectIds;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
