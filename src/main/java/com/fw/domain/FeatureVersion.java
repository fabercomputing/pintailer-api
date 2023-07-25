package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class FeatureVersion {

	private int featureVersionId;
	private int clientProjectId;
	private String featureFileName;
	private String testScenariosHashVersionInfo;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private String featureVersion;
	private boolean isHardDeleted;

	public int getFeatureVersionId() {
		return featureVersionId;
	}

	public void setFeatureVersionId(int featureVersionId) {
		this.featureVersionId = featureVersionId;
	}

	public String getFeatureFileName() {
		return featureFileName;
	}

	public void setFeatureFileName(String featureFileName) {
		this.featureFileName = featureFileName;
	}

	public String getTestScenariosHashVersionInfo() {
		return testScenariosHashVersionInfo;
	}

	public void setTestScenariosHashVersionInfo(
			String testScenariosHashVersionInfo) {
		this.testScenariosHashVersionInfo = testScenariosHashVersionInfo;
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

	public String getFeatureVersion() {
		return featureVersion;
	}

	public void setFeatureVersion(String featureVersion) {
		this.featureVersion = featureVersion;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}
}