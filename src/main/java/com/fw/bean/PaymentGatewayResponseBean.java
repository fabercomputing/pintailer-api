package com.fw.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PaymentGatewayResponseBean {
	
	Double amount;
	String buyer;
	String buyer_name;
	String buyer_phone;
	String currency;
	Double fees;
	String longurl;
	String mac;
	String payment_id;
	String payment_request_id;
	String purpose;
	String shorturl;
	String status;
	
	 public String toString() {
	     return new ToStringBuilder(this).
	       append("amount", amount).
	       append("buyer", buyer).
	       append("buyer_name", buyer_name).
	       append("buyer_phone", buyer_phone).
	       append("currency", currency).
	       append("fees", fees).
	       append("longurl", longurl).
	       append("mac", mac).
	       append("payment_id", payment_id).
	       append("payment_request_id", payment_request_id).
	       append("purpose", purpose).
	       append("shorturl", shorturl).
	       append("status", status).
	       toString();
	   }
	 
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getBuyer() {
		return buyer;
	}
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	public String getBuyer_name() {
		return buyer_name;
	}
	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}
	public String getBuyer_phone() {
		return buyer_phone;
	}
	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getFees() {
		return fees;
	}
	public void setFees(Double fees) {
		this.fees = fees;
	}
	public String getLongurl() {
		return longurl;
	}
	public void setLongurl(String longurl) {
		this.longurl = longurl;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}
	public String getPayment_request_id() {
		return payment_request_id;
	}
	public void setPayment_request_id(String payment_request_id) {
		this.payment_request_id = payment_request_id;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getShorturl() {
		return shorturl;
	}
	public void setShorturl(String shorturl) {
		this.shorturl = shorturl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
