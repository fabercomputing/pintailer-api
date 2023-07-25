package com.fw.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.fw.domain.Payment;
import com.fw.exceptions.APIExceptions;

public interface IPaymentManager {

	/**
	 * Persist the object normally.
	 * 
	 * @throws APIExceptions
	 */
	void persist(Payment entity) throws APIExceptions;

	List<Payment> getAllPaymentRowMapper() throws APIExceptions;;

	void deletePayment(Payment paymentId) throws APIExceptions;;

	void modifyPaymentsByPaymentId(Payment paymentId)
			throws DataAccessException, APIExceptions;

	Payment getPaymentById(Long id) throws APIExceptions;;

	List<Payment> getPaymentByUserId(Long id) throws APIExceptions;;

	void persistPaymentRequest(Payment paymentinfo) throws APIExceptions;;

	void updatePaymentResponse(Payment paymentinfo, String transactionId) throws APIExceptions;

	Payment getPaymentDetailsByTransactionId(String transactionId) throws APIExceptions;

}
