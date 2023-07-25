package com.fw.domain;

import java.util.Date;

import com.fw.enums.PaymentDirection;
import com.fw.enums.PaymentReceiveMethod;

public class Payment {

	private long paymentId;
	private String paymentGatewayRequestId;
	private String paymentGatewayPaymentId;
	private double amountRecieved;
	private double amountRequested;
	private String currency;
	private long packageId;
	private PaymentReceiveMethod receiveMethod;
	private long userId;
	private PaymentDirection paymentDirections;
	private String paymentStatus;
	private String paymentGatewayResponse;
	private Date paymentTime;
	private long createdBy;
	private long modifiedBy;
	private Date createdDate;
	private Date modifiedDate;

	// Generate Getters and Setters
	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentGatewayRequestId() {
		return paymentGatewayRequestId;
	}

	public void setPaymentGatewayRequestId(String paymentGatewayRequestId) {
		this.paymentGatewayRequestId = paymentGatewayRequestId;
	}

	public String getPaymentGatewayPaymentId() {
		return paymentGatewayPaymentId;
	}

	public void setPaymentGatewayPaymentId(String paymentGatewayPaymentId) {
		this.paymentGatewayPaymentId = paymentGatewayPaymentId;
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

	public long getPackageId() {
		return packageId;
	}

	public void setPackageId(long packageId) {
		this.packageId = packageId;
	}

	public PaymentReceiveMethod getReceiveMethod() {
		return receiveMethod;
	}

	public void setReceiveMethod(PaymentReceiveMethod receiveMethod) {
		this.receiveMethod = receiveMethod;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public String getPaymentGatewayResponse() {
		return paymentGatewayResponse;
	}

	public void setPaymentGatewayResponse(String string) {
		this.paymentGatewayResponse = string;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
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
