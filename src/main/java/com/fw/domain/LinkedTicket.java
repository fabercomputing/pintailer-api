package com.fw.domain;

import java.util.Date;

public class LinkedTicket {

	private int linkedTicketId;
	private long testExecutionId;
	private String ticketNumber;
	private boolean applicable;
	private boolean isDeleted;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

	public int getLinkedTicketId() {
		return linkedTicketId;
	}

	public void setLinkedTicketId(int linkedTicketId) {
		this.linkedTicketId = linkedTicketId;
	}

	public long getTestExecutionId() {
		return testExecutionId;
	}

	public void setTestExecutionId(long testExecutionId) {
		this.testExecutionId = testExecutionId;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}
}