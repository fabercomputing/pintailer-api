package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;
import java.util.List;

public class TestCaseVersion {

	private int testCaseVersionId;
	private int testCaseId;
	private String testCaseNo;
	private String testData;
	private long moduleId;
	private String testSummary;
	private String preCondition;
	private List<String> tags;
	private String executionSteps;
	private String expectedResult;
	private boolean isAutomatable;
	private String remarks;
	private String fileName;
	private String automatedTestCaseNoFromFile;
	private String manualReason;
	private boolean applicable;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String hashCode;
	private String modulesNameHierarchy;
	private boolean isAutomaticMappingAvailable;
	private String testCaseVersion;
	private boolean is_hard_deleted;

	// added to make TestNG CSV automatic mapping as in cucumber tests
	private String featureName;
	private String scenarioName;

	// This will used to add comments when duplicate test case found during
	// import
	private String comments;

	// Generate Getters and Setters
	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseNo() {
		return testCaseNo;
	}

	public void setTestCaseNo(String testCaseNo) {
		this.testCaseNo = testCaseNo;
	}

	public String getTestData() {
		return testData;
	}

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getTestSummary() {
		return testSummary;
	}

	public void setTestSummary(String testSummary) {
		this.testSummary = testSummary;
	}

	public String getPreCondition() {
		return preCondition;
	}

	public void setPreCondition(String preCondition) {
		this.preCondition = preCondition;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tagList) {
		this.tags = tagList;
	}

	public String getExecutionSteps() {
		return executionSteps;
	}

	public void setExecutionSteps(String executionSteps) {
		this.executionSteps = executionSteps;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public boolean isAutomatable() {
		return isAutomatable;
	}

	public void setAutomatable(boolean isAutomatable) {
		this.isAutomatable = isAutomatable;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAutomatedTestCaseNoFromFile() {
		return automatedTestCaseNoFromFile;
	}

	public void setAutomatedTestCaseNoFromFile(
			String automatedTestCaseNoFromFile) {
		this.automatedTestCaseNoFromFile = automatedTestCaseNoFromFile;
	}

	public String getManualReason() {
		return manualReason;
	}

	public void setManualReason(String manualReason) {
		this.manualReason = manualReason;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
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

	public boolean isAutomaticMappingAvailable() {
		return isAutomaticMappingAvailable;
	}

	public void setAutomaticMappingAvailable(
			boolean isAutomaticMappingAvailable) {
		this.isAutomaticMappingAvailable = isAutomaticMappingAvailable;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(int testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}

	public String getTestCaseVersion() {
		return testCaseVersion;
	}

	public void setTestCaseVersion(String testCaseVersion) {
		this.testCaseVersion = testCaseVersion;
	}

	public boolean isIs_hard_deleted() {
		return is_hard_deleted;
	}

	public void setIs_hard_deleted(boolean is_hard_deleted) {
		this.is_hard_deleted = is_hard_deleted;
	}

	public String getModulesNameHierarchy() {
		return modulesNameHierarchy;
	}

	public void setModulesNameHierarchy(String modulesNameHierarchy) {
		this.modulesNameHierarchy = modulesNameHierarchy;
	}
}