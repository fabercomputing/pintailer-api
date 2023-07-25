package com.fw.domain;

import java.util.Date;

public class ReleaseTestCaseMapping {

	private long releaseTestCaseMapId;
	private int clientProjectId;
	private int releaseId;
	private int testCaseId;
	private int testCaseVersionId;
	private int testCaseMapVersionId;
	private boolean isDeleted;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

	public long getReleaseTestCaseMapId() {
		return releaseTestCaseMapId;
	}

	public void setReleaseTestCaseMapId(long releaseTestCaseMapId) {
		this.releaseTestCaseMapId = releaseTestCaseMapId;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public int getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(int testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}

	public int getTestCaseMapVersionId() {
		return testCaseMapVersionId;
	}

	public void setTestCaseMapVersionId(int testCaseMapVersionId) {
		this.testCaseMapVersionId = testCaseMapVersionId;
	}
}