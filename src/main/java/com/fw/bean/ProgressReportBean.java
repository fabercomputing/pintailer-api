package com.fw.bean;

import java.util.List;
import java.util.Map;

public class ProgressReportBean {

	private int clientProjectId;
	private List<Integer> addedTestCaseIds;
	private List<Integer> deletedTestCaseIds;
	private List<Integer> addedTestCaseMappingIds;
	private List<Integer> deletedTestCaseMappingIds;
	private List<Integer> overallMappedTestCasesInSpecificPeriod;
	private List<Integer> newlyAddedAndDeletedTestCaseIds;

	private Map<Integer, String> addedTestCaseIdsMap;
	private Map<Integer, String> deletedTestCaseIdsMap;
	private Map<Integer, String> addedTestCaseMappingIdsMap;
	private Map<Integer, String> deletedTestCaseMappingIdsMap;
	private Map<Integer, String> newlyAddedAndDeletedTestCaseIdsMap;

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public List<Integer> getAddedTestCaseIds() {
		return addedTestCaseIds;
	}

	public void setAddedTestCaseIds(List<Integer> addedTestCaseIds) {
		this.addedTestCaseIds = addedTestCaseIds;
	}

	public List<Integer> getDeletedTestCaseIds() {
		return deletedTestCaseIds;
	}

	public void setDeletedTestCaseIds(List<Integer> deletedTestCaseIds) {
		this.deletedTestCaseIds = deletedTestCaseIds;
	}

	public List<Integer> getAddedTestCaseMappingIds() {
		return addedTestCaseMappingIds;
	}

	public void setAddedTestCaseMappingIds(
			List<Integer> addedTestCaseMappingIds) {
		this.addedTestCaseMappingIds = addedTestCaseMappingIds;
	}

	public List<Integer> getDeletedTestCaseMappingIds() {
		return deletedTestCaseMappingIds;
	}

	public void setDeletedTestCaseMappingIds(
			List<Integer> deletedTestCaseMappingIds) {
		this.deletedTestCaseMappingIds = deletedTestCaseMappingIds;
	}

	public List<Integer> getOverallMappedTestCasesInSpecificPeriod() {
		return overallMappedTestCasesInSpecificPeriod;
	}

	public void setOverallMappedTestCasesInSpecificPeriod(
			List<Integer> overallMappedTestCasesInSpecificPeriod) {
		this.overallMappedTestCasesInSpecificPeriod = overallMappedTestCasesInSpecificPeriod;
	}

	public List<Integer> getNewlyAddedAndDeletedTestCaseIds() {
		return newlyAddedAndDeletedTestCaseIds;
	}

	public void setNewlyAddedAndDeletedTestCaseIds(
			List<Integer> newlyAddedAndDeletedTestCaseIds) {
		this.newlyAddedAndDeletedTestCaseIds = newlyAddedAndDeletedTestCaseIds;
	}

	public Map<Integer, String> getAddedTestCaseIdsMap() {
		return addedTestCaseIdsMap;
	}

	public void setAddedTestCaseIdsMap(
			Map<Integer, String> addedTestCaseIdsMap) {
		this.addedTestCaseIdsMap = addedTestCaseIdsMap;
	}

	public Map<Integer, String> getDeletedTestCaseIdsMap() {
		return deletedTestCaseIdsMap;
	}

	public void setDeletedTestCaseIdsMap(
			Map<Integer, String> deletedTestCaseIdsMap) {
		this.deletedTestCaseIdsMap = deletedTestCaseIdsMap;
	}

	public Map<Integer, String> getAddedTestCaseMappingIdsMap() {
		return addedTestCaseMappingIdsMap;
	}

	public void setAddedTestCaseMappingIdsMap(
			Map<Integer, String> addedTestCaseMappingIdsMap) {
		this.addedTestCaseMappingIdsMap = addedTestCaseMappingIdsMap;
	}

	public Map<Integer, String> getDeletedTestCaseMappingIdsMap() {
		return deletedTestCaseMappingIdsMap;
	}

	public void setDeletedTestCaseMappingIdsMap(
			Map<Integer, String> deletedTestCaseMappingIdsMap) {
		this.deletedTestCaseMappingIdsMap = deletedTestCaseMappingIdsMap;
	}

	public Map<Integer, String> getNewlyAddedAndDeletedTestCaseIdsMap() {
		return newlyAddedAndDeletedTestCaseIdsMap;
	}

	public void setNewlyAddedAndDeletedTestCaseIdsMap(
			Map<Integer, String> newlyAddedAndDeletedTestCaseIdsMap) {
		this.newlyAddedAndDeletedTestCaseIdsMap = newlyAddedAndDeletedTestCaseIdsMap;
	}
}