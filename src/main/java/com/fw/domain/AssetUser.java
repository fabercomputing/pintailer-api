package com.fw.domain;

import java.util.Date;

public class AssetUser {

	private int assetInfoId;
	private String assetUserId;
	private Date startDate;
	private Date endDate;
	private String createdBy;

	public int getAssetInfoId() {
		return assetInfoId;
	}

	public void setAssetInfoId(int assetInfoId) {
		this.assetInfoId = assetInfoId;
	}

	public String getAssetUserId() {
		return assetUserId;
	}

	public void setAssetUserId(String assetUserId) {
		this.assetUserId = assetUserId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

}
