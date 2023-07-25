package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class TestStep {

	private long testStepId;
	private String name;
	private String hashCode;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private boolean isApplicable = true;
	private int clientProjectId;

	private String stepModificationStatus = "U";

	private String stepLatestVersion;
	private String stepSelectedVersion;

	// Generate Getters and Setters
	public long getTestStepId() {
		return testStepId;
	}

	public String getName() {
		return name;
	}

	public String getHashCode() {
		return hashCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean isApplicable() {
		return isApplicable;
	}

	public void setTestStepId(long testStepId) {
		this.testStepId = testStepId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setApplicable(boolean isApplicable) {
		this.isApplicable = isApplicable;
	}

	public String getStepModificationStatus() {
		return stepModificationStatus;
	}

	public void setStepModificationStatus(String stepModificationStatus) {
		this.stepModificationStatus = stepModificationStatus;
	}

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public String getStepLatestVersion() {
		return stepLatestVersion;
	}

	public void setStepLatestVersion(String stepLatestVersion) {
		this.stepLatestVersion = stepLatestVersion;
	}

	public String getStepSelectedVersion() {
		return stepSelectedVersion;
	}

	public void setStepSelectedVersion(String stepSelectedVersion) {
		this.stepSelectedVersion = stepSelectedVersion;
	}
}