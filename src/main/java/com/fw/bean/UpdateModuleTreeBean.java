package com.fw.bean;

public class UpdateModuleTreeBean {

	private int projectId;
	private long parentModuleId;
	private String newModuleName;
	private String userName;
	private String clientOrganization;

	public String getNewModuleName() {
		return newModuleName;
	}

	public void setNewModuleName(String newModuleName) {
		this.newModuleName = newModuleName;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientOrganization() {
		return clientOrganization;
	}

	public void setClientOrganization(String clientOrganization) {
		this.clientOrganization = clientOrganization;
	}

	public long getParentModuleId() {
		return parentModuleId;
	}

	public void setParentModuleId(long parentModuleId) {
		this.parentModuleId = parentModuleId;
	}

}