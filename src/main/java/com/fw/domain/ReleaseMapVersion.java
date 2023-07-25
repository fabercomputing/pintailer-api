package com.fw.domain;

import java.util.Date;

public class ReleaseMapVersion {

	private int releaseTestCaseVersionId;
	private int releaseId;
	private String testCaseIds;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private String releaseMapVersionId;
	private boolean isHardDeleted;

	public int getReleaseTestCaseVersionId() {
		return releaseTestCaseVersionId;
	}

	public void setReleaseTestCaseVersionId(int releaseTestCaseVersionId) {
		this.releaseTestCaseVersionId = releaseTestCaseVersionId;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public String getTestCaseIds() {
		return testCaseIds;
	}

	public void setTestCaseIds(String testCaseIds) {
		this.testCaseIds = testCaseIds;
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

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}

	public String getReleaseMapVersionId() {
		return releaseMapVersionId;
	}

	public void setReleaseMapVersionId(String releaseMapVersionId) {
		this.releaseMapVersionId = releaseMapVersionId;
	}
}