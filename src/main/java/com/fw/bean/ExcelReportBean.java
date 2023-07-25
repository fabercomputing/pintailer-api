package com.fw.bean;

public class ExcelReportBean {
	private String moduleName;
	private int totalTestCase;
	private int totalExecutedTestCase;
	private int totalPassedTestCase;
	private int totalFailedTestCase;
	private int totalPendingTestCase;
	private int totalBlockedTestCase;
	private int totalBlockerBugs;
	private String remarks;
	private int backlogTestCaseCount;
	private String linkedBugs;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getTotalTestCase() {
		return totalTestCase;
	}

	public void setTotalTestCase(int totalTestCase) {
		this.totalTestCase = totalTestCase;
	}

	public int getTotalExecutedTestCase() {
		return totalExecutedTestCase;
	}

	public void setTotalExecutedTestCase(int totalExecutedTestCase) {
		this.totalExecutedTestCase = totalExecutedTestCase;
	}

	public int getTotalPassedTestCase() {
		return totalPassedTestCase;
	}

	public void setTotalPassedTestCase(int totalPassedTestCase) {
		this.totalPassedTestCase = totalPassedTestCase;
	}

	public int getTotalFailedTestCase() {
		return totalFailedTestCase;
	}

	public void setTotalFailedTestCase(int totalFailedTestCase) {
		this.totalFailedTestCase = totalFailedTestCase;
	}

	public int getTotalPendingTestCase() {
		return totalPendingTestCase;
	}

	public void setTotalPendingTestCase(int totalPendingTestCase) {
		this.totalPendingTestCase = totalPendingTestCase;
	}

	public int getTotalBlockedTestCase() {
		return totalBlockedTestCase;
	}

	public void setTotalBlockedTestCase(int totalBlockedTestCase) {
		this.totalBlockedTestCase = totalBlockedTestCase;
	}

	public int getTotalBlockerBugs() {
		return totalBlockerBugs;
	}

	public void setTotalBlockerBugs(int totalBlockerBugs) {
		this.totalBlockerBugs = totalBlockerBugs;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getBacklogTestCaseCount() {
		return backlogTestCaseCount;
	}

	public void setBacklogTestCaseCount(int backlogTestCaseCount) {
		this.backlogTestCaseCount = backlogTestCaseCount;
	}

	public String getLinkedBugs() {
		return linkedBugs;
	}

	public void setLinkedBugs(String linkedBugs) {
		this.linkedBugs = linkedBugs;
	}
}
