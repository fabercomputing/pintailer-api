package com.fw.domain;

import java.util.ArrayList;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;
import java.util.List;

public class Modules {

	private long moduleId;
	private String name;
	private long moduleParentId;
	private int clientProjectsId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private boolean isDeleted;
	private String hierarchy;
	private List<Modules> children = new ArrayList<Modules>();

	// Generate Getters and Setters
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

	public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public List<Modules> getChildren() {
		return children;
	}

	public void setChildren(List<Modules> children) {
		this.children = children;
	}
}