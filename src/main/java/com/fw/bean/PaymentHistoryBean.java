package com.fw.bean;

import java.util.Date;

import com.fw.enums.PaymentDirection;
import com.fw.enums.PaymentReceiveMethod;

public class PaymentHistoryBean {

	private long paymentId;
	private String userName;
	private long packageId;
	private double amountRecieved;
	private double amountRequested;
	private String currency;
	private PaymentReceiveMethod receiveMethod;
	private PaymentDirection paymentDirections;
	private String paymentStatus;
	private Date paymentDateTime;
	private long createdBy;
	private long modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

	// Generate Getters and Setters
	public long getPaymentId() {
		return paymentId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
	}

	public Date getPaymentDateTime() {
		return paymentDateTime;
	}

	public void setPaymentDateTime(Date paymentDateTime) {
		this.paymentDateTime = paymentDateTime;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public double getAmountRecieved() {
		return amountRecieved;
	}

	public void setAmountRecieved(double amountRecieved) {
		this.amountRecieved = amountRecieved;
	}

	public double getAmountRequested() {
		return amountRequested;
	}

	public void setAmountRequested(double amountRequested) {
		this.amountRequested = amountRequested;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public PaymentReceiveMethod getReceiveMethod() {
		return receiveMethod;
	}

	public void setReceiveMethod(PaymentReceiveMethod receiveMethod) {
		this.receiveMethod = receiveMethod;
	}

	public PaymentDirection getPaymentDirections() {
		return paymentDirections;
	}

	public void setPaymentDirections(PaymentDirection credit) {
		this.paymentDirections = credit;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
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

}
