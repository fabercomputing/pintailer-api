package com.fw.bean;

import java.util.List;
import java.util.Map;

public class AutomationReportBean {

	private int clientProjectId;
	private List<Integer> totalTestCaseIds;
	private List<Integer> totalAutomatableTestCaseIds;
	private List<Integer> totalManualTestCaseIds;
	private List<Integer> automatedTestCaseIds;
	private List<Integer> pendingAutomatedTestCaseIds;
	private Map<String, List<Integer>> testCaseIdsWithCriticality;
	private Map<String, List<Integer>> automableTestCaseIdsWithCriticality;
	private Map<String, List<Integer>> automatedTestCaseIdsWithCriticality;

	// variables for data in percentage driven by above variables:
	private float automatablePercentage;
	private float automatedPercentage;
	private float pendingAutomatedPercentage;
	private float manualPercentage;
	
	private List<Integer> overallMappedTestCasesInSpecificPeriod;

	// generate getters and setters
	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public List<Integer> getTotalTestCaseIds() {
		return totalTestCaseIds;
	}

	public void setTotalTestCaseIds(List<Integer> totalTestCaseIds) {
		this.totalTestCaseIds = totalTestCaseIds;
	}

	public List<Integer> getTotalAutomatableTestCaseIds() {
		return totalAutomatableTestCaseIds;
	}

	public void setTotalAutomatableTestCaseIds(
			List<Integer> totalAutomatableTestCaseIds) {
		this.totalAutomatableTestCaseIds = totalAutomatableTestCaseIds;
	}

	public List<Integer> getTotalManualTestCaseIds() {
		return totalManualTestCaseIds;
	}

	public void setTotalManualTestCaseIds(List<Integer> totalManualTestCaseIds) {
		this.totalManualTestCaseIds = totalManualTestCaseIds;
	}

	public List<Integer> getAutomatedTestCaseIds() {
		return automatedTestCaseIds;
	}

	public void setAutomatedTestCaseIds(List<Integer> automatedTestCaseIds) {
		this.automatedTestCaseIds = automatedTestCaseIds;
	}

	public List<Integer> getPendingAutomatedTestCaseIds() {
		return pendingAutomatedTestCaseIds;
	}

	public void setPendingAutomatedTestCaseIds(
			List<Integer> pendingAutomatedTestCaseIds) {
		this.pendingAutomatedTestCaseIds = pendingAutomatedTestCaseIds;
	}

	public Map<String, List<Integer>> getTestCaseIdsWithCriticality() {
		return testCaseIdsWithCriticality;
	}

	public void setTestCaseIdsWithCriticality(
			Map<String, List<Integer>> testCaseIdsWithCriticality) {
		this.testCaseIdsWithCriticality = testCaseIdsWithCriticality;
	}

	public Map<String, List<Integer>> getAutomableTestCaseIdsWithCriticality() {
		return automableTestCaseIdsWithCriticality;
	}

	public void setAutomableTestCaseIdsWithCriticality(
			Map<String, List<Integer>> automableTestCaseIdsWithCriticality) {
		this.automableTestCaseIdsWithCriticality = automableTestCaseIdsWithCriticality;
	}

	public Map<String, List<Integer>> getAutomatedTestCaseIdsWithCriticality() {
		return automatedTestCaseIdsWithCriticality;
	}

	public void setAutomatedTestCaseIdsWithCriticality(
			Map<String, List<Integer>> automatedTestCaseIdsWithCriticality) {
		this.automatedTestCaseIdsWithCriticality = automatedTestCaseIdsWithCriticality;
	}

	public float getAutomatablePercentage() {
		return automatablePercentage;
	}

	public void setAutomatablePercentage(float automatablePercentage) {
		this.automatablePercentage = automatablePercentage;
	}

	public float getAutomatedPercentage() {
		return automatedPercentage;
	}

	public void setAutomatedPercentage(float automatedPercentage) {
		this.automatedPercentage = automatedPercentage;
	}

	public float getPendingAutomatedPercentage() {
		return pendingAutomatedPercentage;
	}

	public void setPendingAutomatedPercentage(float pendingAutomatedPercentage) {
		this.pendingAutomatedPercentage = pendingAutomatedPercentage;
	}

	public float getManualPercentage() {
		return manualPercentage;
	}

	public void setManualPercentage(float manualPercentage) {
		this.manualPercentage = manualPercentage;
	}

	public List<Integer> getOverallMappedTestCasesInSpecificPeriod() {
		return overallMappedTestCasesInSpecificPeriod;
	}

	public void setOverallMappedTestCasesInSpecificPeriod(
			List<Integer> overallMappedTestCasesInSpecificPeriod) {
		this.overallMappedTestCasesInSpecificPeriod = overallMappedTestCasesInSpecificPeriod;
	}
}