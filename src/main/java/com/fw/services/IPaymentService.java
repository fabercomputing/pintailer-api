package com.fw.services;

import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.dao.DataAccessException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fw.bean.PaymentGatewayRequestBean;
import com.fw.domain.Payment;
import com.fw.exceptions.APIExceptions;

public interface IPaymentService {

	void getPaymentInfo(Payment paymentEntity) throws APIExceptions;

	List<Payment> getAllPayments() throws APIExceptions;

	void deletePayments(Payment paymentEntity) throws APIExceptions;

	void updatePayments(Payment paymentEntity) throws DataAccessException,
			APIExceptions;

	Payment getPaymentInfoById(Long paymentEntity) throws APIExceptions;

	List<Payment> getPaymentInfoByUserId(Long paymentEntity)
			throws APIExceptions;

	JSONObject createNewPaymentRequest(PaymentGatewayRequestBean pgRequestBean)
			throws APIExceptions;

	JSONObject getPaymentDetailsByTransactionId(String transactionId)
			throws JsonParseException, JsonMappingException, IOException,
			APIExceptions;
}
