package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;
import java.util.List;

public class TestCaseClientBean {

	private int testCaseSequenceId;
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
	private boolean isDeleted;

	/*
	 * Selected version on the release mapping page. i.e. the version of test
	 * cases used to map with a specific release
	 */
	private String selectedVersion;
	/* Latest version of the test cases */
	private String latestVersion;
	/* if a specific test case is mapped to the release */
	private boolean mappedToRelease;
	/*
	 * to make fetching of list of test cases more efficient on import
	 * definitions page
	 */
	private String modulesNameHierarchy;

	private boolean isAutomationMappingExists = false;
	
	/*Add bug info*/
	private String bugsAndTypes;
	private boolean isProductionBug;

	// Generate Getters and Setters
	public int getTestCaseSequenceId() {
		return testCaseSequenceId;
	}

	public void setTestCaseSequenceId(int testCaseSequenceId) {
		this.testCaseSequenceId = testCaseSequenceId;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getModulesNameHierarchy() {
		return modulesNameHierarchy;
	}

	public void setModulesNameHierarchy(String modulesNameHierarchy) {
		this.modulesNameHierarchy = modulesNameHierarchy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getSelectedVersion() {
		return selectedVersion;
	}

	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

//	public List<TestCaseVersion> getTestCaseVersionList() {
//		return testCaseVersionList;
//	}
//
//	public void setTestCaseVersionList(List<TestCaseVersion> testCaseVersionList) {
//		this.testCaseVersionList = testCaseVersionList;
//	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public boolean isMappedToRelease() {
		return mappedToRelease;
	}

	public void setMappedToRelease(boolean mappedToRelease) {
		this.mappedToRelease = mappedToRelease;
	}

	public boolean isAutomationMappingExists() {
		return isAutomationMappingExists;
	}

	public void setAutomationMappingExists(boolean isAutomationMappingExists) {
		this.isAutomationMappingExists = isAutomationMappingExists;
	}

	public String getBugsAndTypes() {
		return bugsAndTypes;
	}

	public void setBugsAndTypes(String bugsAndTypes) {
		this.bugsAndTypes = bugsAndTypes;
	}

	public boolean isProductionBug() {
		return isProductionBug;
	}

	public void setProductionBug(boolean isProductionBug) {
		this.isProductionBug = isProductionBug;
	}
}