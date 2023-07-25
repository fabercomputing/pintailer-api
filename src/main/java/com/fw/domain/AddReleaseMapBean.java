package com.fw.domain;

import java.util.List;

public class AddReleaseMapBean {

	private int releaseId;
	private List<String> testCaseIds;
	private String createdBy;
	private String modifiedBy;

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public List<String> getTestCaseIds() {
		return testCaseIds;
	}

	public void setTestCaseIds(List<String> testCaseIds) {
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
}