package com.fw.bean;

import java.sql.Timestamp;
import java.util.List;

/** Common bean for both JSON and XML type execution report */
public class ExecutionDataBean {
	// Automation Test Step Info
	private String featureName;
	private String scenarioName;
	private String stepKeyword;
	private String stepDefinition;
	private String stepDescription;
	private String stepComment;
	private int lineNumber;

	// Manual Test cases info
	private int testCaseId;
	private String remarks;
	private String linkedDefects;
	private List<String> mappedTestStepDefinitions;

	private Timestamp startTime;
	private String status;
	private float duration;
	private String reportType;

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

	public String getStepKeyword() {
		return stepKeyword;
	}

	public void setStepKeyword(String stepKeyword) {
		this.stepKeyword = stepKeyword;
	}

	public String getStepDefinition() {
		return stepDefinition;
	}

	public void setStepDefinition(String stepDefinition) {
		this.stepDefinition = stepDefinition;
	}

	public String getStepDescription() {
		return stepDescription;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getLinkedDefects() {
		return linkedDefects;
	}

	public void setLinkedDefects(String linkedDefects) {
		this.linkedDefects = linkedDefects;
	}

	public List<String> getMappedTestStepDefinitions() {
		return mappedTestStepDefinitions;
	}

	public void setMappedTestStepDefinitions(
			List<String> mappedTestStepDefinitions) {
		this.mappedTestStepDefinitions = mappedTestStepDefinitions;
	}

	public String getStepComment() {
		return stepComment;
	}

	public void setStepComment(String stepComment) {
		this.stepComment = stepComment;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
