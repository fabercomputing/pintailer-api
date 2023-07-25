package com.fw.domain;

import java.util.ArrayList;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import java.util.Date;
import java.util.List;

public class HelpTopics {

	private int topicId;
	private String title;
	private int topicParentId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
	private String hierarchy;
	private List<HelpTopics> children = new ArrayList<HelpTopics>();

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTopicParentId() {
		return topicParentId;
	}

	public void setTopicParentId(int topicParentId) {
		this.topicParentId = topicParentId;
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

	public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public List<HelpTopics> getChildren() {
		return children;
	}

	public void setChildren(List<HelpTopics> children) {
		this.children = children;
	}
}