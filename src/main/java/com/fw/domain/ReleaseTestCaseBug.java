package com.fw.domain;

import java.util.Date;

public class ReleaseTestCaseBug {

	private int releaseTestCaseBugId;
	private int releaseId;
	private int testCaseId;
	private String testCaseVersionId;
	private String bugId;
	private String bugType;
	private int executionEnvId;
	private String remarks;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean applicable;
	private boolean isDeleted;

	public int getReleaseTestCaseBugId() {
		return releaseTestCaseBugId;
	}

	public void setReleaseTestCaseBugId(int releaseTestCaseBugId) {
		this.releaseTestCaseBugId = releaseTestCaseBugId;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(String testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}

	public String getBugId() {
		return bugId;
	}

	public void setBugId(String bugId) {
		this.bugId = bugId;
	}

	public String getBugType() {
		return bugType;
	}

	public void setBugType(String bugType) {
		this.bugType = bugType;
	}

	public int getExecutionEnvId() {
		return executionEnvId;
	}

	public void setExecutionEnvId(int executionEnvId) {
		this.executionEnvId = executionEnvId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}