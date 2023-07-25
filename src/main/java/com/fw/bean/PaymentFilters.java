package com.fw.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fw.enums.PaymentDirection;

public class PaymentFilters {

	private Long loadRequestId;
	private String paymentStatus;
	private String userName;
	private Date startDate;
	private Date endDate;
	private PaymentDirection directionType;
	private Integer page;	
	private Integer limit;
	
	@JsonIgnore
	private  Long userId;
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getLoadRequestId() {
		return loadRequestId;
	}
	
	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setLoadRequestId(Long loadRequestId) {
		this.loadRequestId = loadRequestId;
	}
	
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public PaymentDirection getDirectionType() {
		return directionType;
	}

	public void setDirectionType(PaymentDirection directionType) {
		this.directionType = directionType;
	}


}
