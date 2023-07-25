package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class TestScenarioStep {

	private long testScenarioStepId;
	private int testScenarioId;
	private long testStepId;
	private int testStepSequence;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String stepKeyword;
	private String scenarioOutlineKeyValue;

	// Generate Getters and Setters
	public long getTestScenarioStepId() {
		return testScenarioStepId;
	}

	public int getTestScenarioId() {
		return testScenarioId;
	}

	public long getTestStepId() {
		return testStepId;
	}

	public int getTestStepSequence() {
		return testStepSequence;
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

	public void setTestScenarioStepId(long testScenarioStepId) {
		this.testScenarioStepId = testScenarioStepId;
	}

	public void setTestScenarioId(int testScenarioId) {
		this.testScenarioId = testScenarioId;
	}

	public void setTestStepId(long testStepId) {
		this.testStepId = testStepId;
	}

	public void setTestStepSequence(int testStepSequence) {
		this.testStepSequence = testStepSequence;
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

	public String getStepKeyword() {
		return stepKeyword;
	}

	public void setStepKeyword(String stepKeyword) {
		this.stepKeyword = stepKeyword;
	}

	public String getScenarioOutlineKeyValue() {
		return scenarioOutlineKeyValue;
	}

	public void setScenarioOutlineKeyValue(String scenarioOutlineKeyValue) {
		this.scenarioOutlineKeyValue = scenarioOutlineKeyValue;
	}

}