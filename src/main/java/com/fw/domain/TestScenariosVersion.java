package com.fw.domain;

import java.sql.Date;

/**
 * 
 * @author Sumit Srivastava
 *
 */

public class TestScenariosVersion {

	private int testScenariosVersionId;
	private int testScenarioId;
	private String name;
	private String featureFileName;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String hashCode;
	private int clientProjectId;
	private String scenarioTag;
	private boolean isBackground;
	private boolean isFeature;
	private boolean isScenarioOutline;
	private int scenarioSequence;
	private String versionId;
	private boolean isHardDeleted;

	public int getTestScenariosVersionId() {
		return testScenariosVersionId;
	}

	public void setTestScenariosVersionId(int testScenariosVersionId) {
		this.testScenariosVersionId = testScenariosVersionId;
	}

	public int getTestScenarioId() {
		return testScenarioId;
	}

	public void setTestScenarioId(int testScenarioId) {
		this.testScenarioId = testScenarioId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFeatureFileName() {
		return featureFileName;
	}

	public void setFeatureFileName(String featureFileName) {
		this.featureFileName = featureFileName;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public String getScenarioTag() {
		return scenarioTag;
	}

	public void setScenarioTag(String scenarioTag) {
		this.scenarioTag = scenarioTag;
	}

	public boolean isBackground() {
		return isBackground;
	}

	public void setBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

	public boolean isFeature() {
		return isFeature;
	}

	public void setFeature(boolean isFeature) {
		this.isFeature = isFeature;
	}

	public boolean isScenarioOutline() {
		return isScenarioOutline;
	}

	public void setScenarioOutline(boolean isScenarioOutline) {
		this.isScenarioOutline = isScenarioOutline;
	}

	public int getScenarioSequence() {
		return scenarioSequence;
	}

	public void setScenarioSequence(int scenarioSequence) {
		this.scenarioSequence = scenarioSequence;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}
}