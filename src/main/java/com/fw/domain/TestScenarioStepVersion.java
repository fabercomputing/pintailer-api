package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;
import java.util.List;

public class TestScenarioStepVersion {

	private int testScenarioStepVersionId;
	private int testScenariosId;
	private String testScenariosHashcode;
	private int testScenariosVersionId;
	private String testStepIdVersionSequenceKeyword;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String testScenariosStepVersion;
	private boolean isHardDeleted;

	/*
	 * Below variable are to load the version data on the test case mapping page
	 */
	private TestScenarios testScenarios;
	private List<TestStep> testSteps;
	/**/

	public int getTestScenarioStepVersionId() {
		return testScenarioStepVersionId;
	}

	public void setTestScenarioStepVersionId(int testScenarioStepVersionId) {
		this.testScenarioStepVersionId = testScenarioStepVersionId;
	}

	public String getTestScenariosHashcode() {
		return testScenariosHashcode;
	}

	public void setTestScenariosHashcode(String testScenariosHashcode) {
		this.testScenariosHashcode = testScenariosHashcode;
	}

	public int getTestScenariosVersionId() {
		return testScenariosVersionId;
	}

	public void setTestScenariosVersionId(int testScenariosVersionId) {
		this.testScenariosVersionId = testScenariosVersionId;
	}

	public String getTestStepIdVersionSequenceKeyword() {
		return testStepIdVersionSequenceKeyword;
	}

	public void setTestStepIdVersionSequenceKeyword(
			String testStepIdVersionSequenceKeyword) {
		this.testStepIdVersionSequenceKeyword = testStepIdVersionSequenceKeyword;
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

	public String getTestScenariosStepVersion() {
		return testScenariosStepVersion;
	}

	public void setTestScenariosStepVersion(String testScenariosStepVersion) {
		this.testScenariosStepVersion = testScenariosStepVersion;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}

	public int getTestScenariosId() {
		return testScenariosId;
	}

	public void setTestScenariosId(int testScenariosId) {
		this.testScenariosId = testScenariosId;
	}

	public TestScenarios getTestScenarios() {
		return testScenarios;
	}

	public void setTestScenarios(TestScenarios testScenarios) {
		this.testScenarios = testScenarios;
	}

	public List<TestStep> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<TestStep> testSteps) {
		this.testSteps = testSteps;
	}
}