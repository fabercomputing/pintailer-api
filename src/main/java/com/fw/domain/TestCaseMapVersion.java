package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class TestCaseMapVersion {

	private int testCaseMapVersionId;
	private int testCaseId;
	private int testCaseVersionId;
	private String selectedTestStepsIdAndVersion;
	private int testScenarioStepVersionId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private String testCaseMapVersion;
	private boolean isHardDeleted;
	
	private TestScenarios testScenarios;

	public int getTestCaseMapVersionId() {
		return testCaseMapVersionId;
	}

	public void setTestCaseMapVersionId(int testCaseMapVersionId) {
		this.testCaseMapVersionId = testCaseMapVersionId;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public int getTestScenarioStepVersionId() {
		return testScenarioStepVersionId;
	}

	public void setTestScenarioStepVersionId(int testScenarioStepVersionId) {
		this.testScenarioStepVersionId = testScenarioStepVersionId;
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

	public String getTestCaseMapVersion() {
		return testCaseMapVersion;
	}

	public void setTestCaseMapVersion(String testCaseMapVersion) {
		this.testCaseMapVersion = testCaseMapVersion;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}

	public int getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(int testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}

	public String getSelectedTestStepsIdAndVersion() {
		return selectedTestStepsIdAndVersion;
	}

	public void setSelectedTestStepsIdAndVersion(
			String selectedTestStepsIdAndVersion) {
		this.selectedTestStepsIdAndVersion = selectedTestStepsIdAndVersion;
	}

	public TestScenarios getTestScenarios() {
		return testScenarios;
	}

	public void setTestScenarios(TestScenarios testScenarios) {
		this.testScenarios = testScenarios;
	}
}