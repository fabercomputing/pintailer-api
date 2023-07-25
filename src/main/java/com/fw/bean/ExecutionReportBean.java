package com.fw.bean;

import java.util.List;
import java.util.Map;

public class ExecutionReportBean {
	private String clientOrganisation;
	private int clientProjectId;
	private long moduleId;
	private int envId;
	private int releaseId;
	private int totalTestCaseCount;
	private int totalExecutedTestCaseCount;

	// Below map is designed as Map <TestCaseExecutionStatus, List<TestCaseId>>>
	private Map<String, List<String>> testCaseExecutionStatusInfo;

	// Below map is designed as Map <TestCaseId, Map<TestStepExecutionStatus,
	// List<TestStepsDefinition>>>
	private Map<Integer, Map<String, List<String>>> testStepsStatus;

	// Below map is designed as Map<TestStepExecutionStatus,
	// List<TestStepsDefinition>>
	private Map<String, Integer> durationInfo;

	public String getClientOrganisation() {
		return clientOrganisation;
	}

	public void setClientOrganisation(String clientOrganisation) {
		this.clientOrganisation = clientOrganisation;
	}

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public int getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(int releaseId) {
		this.releaseId = releaseId;
	}

	public int getTotalTestCaseCount() {
		return totalTestCaseCount;
	}

	public void setTotalTestCaseCount(int totalTestCaseCount) {
		this.totalTestCaseCount = totalTestCaseCount;
	}

	public int getTotalExecutedTestCaseCount() {
		return totalExecutedTestCaseCount;
	}

	public void setTotalExecutedTestCaseCount(int totalExecutedTestCaseCount) {
		this.totalExecutedTestCaseCount = totalExecutedTestCaseCount;
	}

	public Map<String, List<String>> getTestCaseExecutionStatusInfo() {
		return testCaseExecutionStatusInfo;
	}

	public void setTestCaseExecutionStatusInfo(
			Map<String, List<String>> testCaseExecutionStatusInfo) {
		this.testCaseExecutionStatusInfo = testCaseExecutionStatusInfo;
	}

	public Map<Integer, Map<String, List<String>>> getTestStepsStatus() {
		return testStepsStatus;
	}

	public void setTestStepsStatus(
			Map<Integer, Map<String, List<String>>> testStepsStatus) {
		this.testStepsStatus = testStepsStatus;
	}

	public Map<String, Integer> getDurationInfo() {
		return durationInfo;
	}

	public void setDurationInfo(Map<String, Integer> durationInfo) {
		this.durationInfo = durationInfo;
	}
}