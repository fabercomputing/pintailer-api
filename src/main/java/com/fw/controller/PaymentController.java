package com.fw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fw.bean.PaymentGatewayRequestBean;
import com.fw.bean.PaymentGatewayResponseBean;
import com.fw.domain.Payment;
import com.fw.exceptions.APIExceptions;

public interface PaymentController {

	void savePaymentInfo(Payment paymentForm) throws APIExceptions;

	ResponseEntity<List<Payment>> getAllPayments() throws APIExceptions;

	void deletePayment(Payment deletePayment) throws APIExceptions;

	void modifyPaymentByPaymentId(Payment paymentForm) throws APIExceptions;

	ResponseEntity<Payment> getPaymentById(Long id) throws APIExceptions;

	ResponseEntity<List<Payment>> getPaymentByUserId(Long userId)
			throws APIExceptions;

	ResponseEntity<PaymentGatewayResponseBean> paymentGatewayResponse(
			PaymentGatewayResponseBean pgrBean) throws APIExceptions;

	ResponseEntity<String> createNewPaymentRequest(
			PaymentGatewayRequestBean pgRequestBean) throws APIExceptions;

	ResponseEntity<String> getPaymentDetailsByTransactionId(
			PaymentGatewayRequestBean pgRequestBean) throws APIExceptions,
			JsonParseException, JsonMappingException, IOException;

}
