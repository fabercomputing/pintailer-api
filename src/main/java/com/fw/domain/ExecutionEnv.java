package com.fw.domain;

import java.util.Date;

public class ExecutionEnv {

	private int executionEnvId;
	private String executionEnvName;
	private String clientOrganization;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

	public int getExecutionEnvId() {
		return executionEnvId;
	}

	public void setExecutionEnvId(int executionEnvId) {
		this.executionEnvId = executionEnvId;
	}

	public String getExecutionEnvName() {
		return executionEnvName;
	}

	public void setExecutionEnvName(String executionEnvName) {
		this.executionEnvName = executionEnvName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getClientOrganization() {
		return clientOrganization;
	}

	public void setClientOrganization(String clientOrganization) {
		this.clientOrganization = clientOrganization;
	}
}