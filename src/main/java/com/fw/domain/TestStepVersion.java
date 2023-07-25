package com.fw.domain;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;

public class TestStepVersion {

	private long testStepVersionId;
	private long testStepId;
	private String name;
	private String hashCode;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private int clientProjectId;
	private String testStepVersion;
	private boolean isHardDeleted;

	public long getTestStepVersionId() {
		return testStepVersionId;
	}

	public void setTestStepVersionId(long testStepVersionId) {
		this.testStepVersionId = testStepVersionId;
	}

	public long getTestStepId() {
		return testStepId;
	}

	public void setTestStepId(long testStepId) {
		this.testStepId = testStepId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
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

	public int getClientProjectId() {
		return clientProjectId;
	}

	public void setClientProjectId(int clientProjectId) {
		this.clientProjectId = clientProjectId;
	}

	public String getTestStepVersion() {
		return testStepVersion;
	}

	public void setTestStepVersion(String testStepVersion) {
		this.testStepVersion = testStepVersion;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}

}