package com.fw.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fw.bean.PaymentGatewayRequestBean;
import com.fw.exceptions.APIExceptions;
import com.fw.validations.DataValidations;
import com.instamojo.wrapper.api.Instamojo;
import com.instamojo.wrapper.api.InstamojoImpl;
import com.instamojo.wrapper.exception.ConnectionException;
import com.instamojo.wrapper.exception.InvalidPaymentOrderException;
import com.instamojo.wrapper.model.PaymentOrder;
import com.instamojo.wrapper.response.CreatePaymentOrderResponse;
import com.instamojo.wrapper.response.PaymentOrderDetailsResponse;

public class InstamojoPaymentGateway {

	static Properties propFile;
	private final static Logger logger = Logger
			.getLogger(InstamojoPaymentGateway.class);
	private static String CLIENT_ID;
	private static String CLIENT_SECRET;
	private static String API_ENDPOINT;
	private static String AUTH_ENDPOINT;

	static {
		try {
			propFile = new Properties();
			propFile.load(InstamojoPaymentGateway.class.getClassLoader()
					.getResourceAsStream("PaymentGateway.properties"));
			CLIENT_ID = propFile.getProperty("CLIENT_ID");
			CLIENT_SECRET = propFile.getProperty("CLIENT_SECRET");
			API_ENDPOINT = propFile.getProperty("API_ENDPOINT");
			AUTH_ENDPOINT = propFile.getProperty("AUTH_ENDPOINT");

		} catch (IOException e) {
			String message = "Error occured while reading the resource file :"
					+ e.getMessage();
			logger.error(message);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	public static JSONObject paymentOrder(
			PaymentGatewayRequestBean pgRequestBean, String userName)
			throws APIExceptions {

		JSONObject jsonObject = null;
		PaymentOrder order = new PaymentOrder();
		DataValidations dataValidations = new DataValidations();

		order.setName(pgRequestBean.getUserName());
		order.setEmail(pgRequestBean.getUserEmail());

		// The payement gateway does not work with phone numbers appended with
		// country
		// code, we need to remove it.
		dataValidations.phoneNumberValidation(userName);
		String phoneNumber = userName;
		if (phoneNumber.length() == 12)
			phoneNumber = phoneNumber.substring(2, phoneNumber.length());
		order.setPhone(phoneNumber);

		dataValidations.paymentGatewayMinimunAmountValidation(pgRequestBean
				.getAmountRecieved());
		order.setAmount(pgRequestBean.getAmountRecieved());

		order.setDescription(pgRequestBean.getPaymentDescription());
		order.setTransactionId(pgRequestBean.getTransactionId());

		order.setRedirectUrl(pgRequestBean.getRedirectUrl());
		order.setCurrency("INR");

		Instamojo api = null;

		try {
			api = InstamojoImpl.getApi(CLIENT_ID, CLIENT_SECRET, API_ENDPOINT,
					AUTH_ENDPOINT);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}

		boolean isOrderValid = order.validate();

		if (isOrderValid) {
			try {
				CreatePaymentOrderResponse createPaymentOrderResponse = api
						.createNewPaymentOrder(order);
				jsonObject = new JSONObject(
						createPaymentOrderResponse.getJsonResponse());

				logger.info("status of the payment order:-"
						+ createPaymentOrderResponse.getPaymentOrder()
								.getStatus());
			} catch (InvalidPaymentOrderException e) {
				logger.error(e.toString(), e);

				if (order.isTransactionIdInvalid()) {
					logger.error("Transaction id is invalid. This is mostly due to duplicate  transaction id.");
				}
				if (order.isCurrencyInvalid()) {
					logger.error("Currency is invalid.");
				}
			} catch (ConnectionException | JSONException e) {
				logger.error(e.toString(), e);
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		} else {
			// inform validation errors to the user.
			if (order.isTransactionIdInvalid()) {
				logger.error("Transaction id is invalid.");
			}
			if (order.isAmountInvalid()) {
				logger.error("Amount can not be less than 9.00.");
			}
			if (order.isCurrencyInvalid()) {
				logger.error("Please provide the currency.");
			}
			if (order.isDescriptionInvalid()) {
				logger.error("Description can not be greater than 255 characters.");
			}
			if (order.isEmailInvalid()) {
				logger.error("Please provide valid Email Address.");
			}
			if (order.isNameInvalid()) {
				logger.error("Name can not be greater than 100 characters.");
			}
			if (order.isPhoneInvalid()) {
				logger.error("Phone is invalid.");
			}
			if (order.isRedirectUrlInvalid()) {
				logger.error("Please provide valid Redirect url.");
			}

			if (order.isWebhookInvalid()) {
				logger.error("Provide a valid webhook url");
			}
		}
		return jsonObject;
	}

	public static JSONObject getDetailsByTransactionId(String transactionID) {
		JSONObject jsonObject = null;
		try {
			Instamojo api = InstamojoImpl.getApi(CLIENT_ID, CLIENT_SECRET,
					API_ENDPOINT, AUTH_ENDPOINT);

			PaymentOrderDetailsResponse paymentOrderDetailsResponse = api
					.getPaymentOrderDetailsByTransactionId(transactionID);
			jsonObject = new JSONObject(
					paymentOrderDetailsResponse.getJsonResponse());
			logger.info("JSON RESPONSE (By Transaction Number) >>>"
					+ paymentOrderDetailsResponse.getJsonResponse());
			logger.info("status of the payment order by ID >>>>"
					+ paymentOrderDetailsResponse.getStatus());
		} catch (ConnectionException | JSONException e) {
			logger.error(e.toString(), e);
			return null;
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return jsonObject;
	}
}
