package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

import com.fw.enums.TestResults;

public class TestCaseExecutions {

	private long testCaseExecutionsId;
	private int testCaseId;
	private long testStepId;
	private String testRunBy;
	private String actualResult;
	private Date executionDate;
	private TestResults testResult;
	private int environmentId;
	private int actualLOE;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private int releaseId;

	private String linkedBug;
	private String stepKeyword;

	public long getTestCaseExecutionsId() {
		return testCaseExecutionsId;
	}

	public void setTestCaseExecutionsId(long testCaseExecutionsId) {
		this.testCaseExecutionsId = testCaseExecutionsId;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public long getTestStepId() {
		return testStepId;
	}

	public void setTestStepId(long testStepId) {
		this.testStepId = testStepId;
	}

	public String getTestRunBy() {
		return testRunBy;
	}

	public void setTestRunBy(String testRunBy) {
		this.testRunBy = testRunBy;
	}

	public String getActualResult() {
		return actualResult;
	}

	public void setActualResult(String actualResult) {
		this.actualResult = actualResult;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public TestResults getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResults testResult) {
		this.testResult = testResult;
	}

	public int getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(int environmentId) {
		this.environmentId = environmentId;
	}

	public int getActualLOE() {
		return actualLOE;
	}

	public void setActualLOE(int actualLOE) {
		this.actualLOE = actualLOE;
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

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public String getLinkedBug() {
		return linkedBug;
	}

	public void setLinkedBug(String linkedBug) {
		this.linkedBug = linkedBug;
	}

	public String getStepKeyword() {
		return stepKeyword;
	}

	public void setStepKeyword(String stepKeyword) {
		this.stepKeyword = stepKeyword;
	}
}