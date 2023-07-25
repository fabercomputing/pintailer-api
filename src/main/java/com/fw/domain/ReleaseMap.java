package com.fw.domain;

import java.util.Date;

public class ReleaseMap {

	private int releaseMapId;
	private int releaseId;
	private int testCaseId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private int testCaseVersionId;

	public int getReleaseMapId() {
		return releaseMapId;
	}

	public void setReleaseMapId(int releaseMapId) {
		this.releaseMapId = releaseMapId;
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

	public int getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(int testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}
}