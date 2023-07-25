package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class TestCaseMap {

	private long testCaseMapId;
	private int testCaseId;
	private long testStepId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private int testScenarioId;

	// for version info on test case mapping page
	private String testCaseVersionId;
	private String testStepVersionId;
	private int testScenarioStepVersionId;

	// Generate Getters and Setters
	public long getTestCaseMapId() {
		return testCaseMapId;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public long getTestStepId() {
		return testStepId;
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

	public void setTestCaseMapId(long testCaseMapId) {
		this.testCaseMapId = testCaseMapId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public void setTestStepId(long testStepId) {
		this.testStepId = testStepId;
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

	public String getTestCaseVersionId() {
		return testCaseVersionId;
	}

	public void setTestCaseVersionId(String testCaseVersionId) {
		this.testCaseVersionId = testCaseVersionId;
	}

	public String getTestStepVersionId() {
		return testStepVersionId;
	}

	public void setTestStepVersionId(String testStepVersionId) {
		this.testStepVersionId = testStepVersionId;
	}

	public int getTestScenarioId() {
		return testScenarioId;
	}

	public void setTestScenarioId(int testScenarioId) {
		this.testScenarioId = testScenarioId;
	}

	public int getTestScenarioStepVersionId() {
		return testScenarioStepVersionId;
	}

	public void setTestScenarioStepVersionId(int testScenarioStepVersionId) {
		this.testScenarioStepVersionId = testScenarioStepVersionId;
	}

}