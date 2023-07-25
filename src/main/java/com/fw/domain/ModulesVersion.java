package com.fw.domain;

import java.util.Date;

public class ModulesVersion {

	private int modulesVersionId;
	private long moduleId;
	private String name;
	private long moduleParentId;
	private int clientProjectsId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String versionId;
	private boolean isHardDeleted;

	public int getModulesVersionId() {
		return modulesVersionId;
	}

	public void setModulesVersionId(int modulesVersionId) {
		this.modulesVersionId = modulesVersionId;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getModuleParentId() {
		return moduleParentId;
	}

	public void setModuleParentId(long moduleParentId) {
		this.moduleParentId = moduleParentId;
	}

	public int getClientProjectsId() {
		return clientProjectsId;
	}

	public void setClientProjectsId(int clientProjectsId) {
		this.clientProjectsId = clientProjectsId;
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

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public boolean isHardDeleted() {
		return isHardDeleted;
	}

	public void setHardDeleted(boolean isHardDeleted) {
		this.isHardDeleted = isHardDeleted;
	}
}