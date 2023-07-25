package com.fw.bean;

import java.util.Date;

/**
 * 
 * @author Sumit Srivastava
 *
 */
public class PaymentResponseBean {

	private Order order;
	private PaymentOptions payment_options;

	// Generate Getters and setters
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public PaymentOptions getPayment_options() {
		return payment_options;
	}

	public void setPayment_options(PaymentOptions payment_options) {
		this.payment_options = payment_options;
	}

	public static class Order {

		private String id;
		private String transaction_id;
		private String status;
		private String currency;
		private double amount;
		private String name;
		private String email;
		private String phone;
		private String description;
		private String redirect_url;
		private String webhook_url;
		private Date created_at;
		private String resource_uri;
		private String customer_id;

		// Generate getters and setters
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTransaction_id() {
			return transaction_id;
		}

		public void setTransaction_id(String transaction_id) {
			this.transaction_id = transaction_id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getRedirect_url() {
			return redirect_url;
		}

		public void setRedirect_url(String redirect_url) {
			this.redirect_url = redirect_url;
		}

		public String getWebhook_url() {
			return webhook_url;
		}

		public void setWebhook_url(String webhook_url) {
			this.webhook_url = webhook_url;
		}

		public Date getCreated_at() {
			return created_at;
		}

		public void setCreated_at(Date created_at) {
			this.created_at = created_at;
		}

		public String getResource_uri() {
			return resource_uri;
		}

		public void setResource_uri(String resource_uri) {
			this.resource_uri = resource_uri;
		}

		public String getCustomer_id() {
			return customer_id;
		}

		public void setCustomer_id(String customer_id) {
			this.customer_id = customer_id;
		}

	}

	public static class PaymentOptions {
		private String payment_url;

		// Generate Getters and Setters
		public String getPayment_url() {
			return payment_url;
		}

		public void setPayment_url(String payment_url) {
			this.payment_url = payment_url;
		}

	}

}
