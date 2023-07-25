package com.fw.domain;

import java.sql.Date;
import java.util.List;

/**
 * 
 * @author Sumit Srivastava
 *
 */

public class TestScenarios {

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

	private String scenarioModificationStatus = "U";
	private List<TestStep> testStepsList;

	private String scenarioLatestVersion;
	private String scenarioSelectedVersion;

	/* version list on test case mapping page */
	private String existingVersions;

	// Generate Getters and Setters
	public int getTestScenarioId() {
		return testScenarioId;
	}

	public String getName() {
		return name;
	}

	public String getFeatureFileName() {
		return featureFileName;
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

	public void setTestScenarioId(int testScenarioId) {
		this.testScenarioId = testScenarioId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFeatureFileName(String featureFileName) {
		this.featureFileName = featureFileName;
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

	public String getScenarioModificationStatus() {
		return scenarioModificationStatus;
	}

	public void setScenarioModificationStatus(
			String scenarioModificationStatus) {
		this.scenarioModificationStatus = scenarioModificationStatus;
	}

	public List<TestStep> getTestStepsList() {
		return testStepsList;
	}

	public void setTestStepsList(List<TestStep> testStepsList) {
		this.testStepsList = testStepsList;
	}

	public int getScenarioSequence() {
		return scenarioSequence;
	}

	public void setScenarioSequence(int scenarioSequence) {
		this.scenarioSequence = scenarioSequence;
	}

	public boolean isScenarioOutline() {
		return isScenarioOutline;
	}

	public void setScenarioOutline(boolean isScenarioOutline) {
		this.isScenarioOutline = isScenarioOutline;
	}

	public String getScenarioLatestVersion() {
		return scenarioLatestVersion;
	}

	public void setScenarioLatestVersion(String scenarioLatestVersion) {
		this.scenarioLatestVersion = scenarioLatestVersion;
	}

	public String getScenarioSelectedVersion() {
		return scenarioSelectedVersion;
	}

	public void setScenarioSelectedVersion(String scenarioSelectedVersion) {
		this.scenarioSelectedVersion = scenarioSelectedVersion;
	}

	public String getExistingVersions() {
		return existingVersions;
	}

	public void setExistingVersions(String existingVersions) {
		this.existingVersions = existingVersions;
	}
}