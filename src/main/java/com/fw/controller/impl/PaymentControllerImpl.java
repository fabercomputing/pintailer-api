package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fw.bean.PaymentGatewayRequestBean;
import com.fw.bean.PaymentGatewayResponseBean;
import com.fw.controller.PaymentController;
import com.fw.domain.Payment;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IPaymentService;
import com.fw.utils.LocalUtils;

@Controller
@RequestMapping(value = "/mLogistics")
public class PaymentControllerImpl implements PaymentController {
	private final static Logger logger = Logger
			.getLogger(PaymentControllerImpl.class);
	@Autowired
	IPaymentService paymentService;

	@Override
	@RequestMapping(value = "/private/payment/getPaymentInfo", method = { POST })
	public void savePaymentInfo(@RequestBody Payment pay) throws APIExceptions {
		try {
			paymentService.getPaymentInfo(pay);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/getAllPayments", method = { GET })
	public ResponseEntity<List<Payment>> getAllPayments() throws APIExceptions {
		try {
			return new ResponseEntity<List<Payment>>(
					paymentService.getAllPayments(), HttpStatus.OK);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}

	}

	@Override
	@RequestMapping(value = "/private/payment/deletePayments/{paymentId}", method = { DELETE })
	public void deletePayment(@RequestBody Payment paymentLog)
			throws APIExceptions {
		try {
			paymentService.deletePayments(paymentLog);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}

	}

	@Override
	@RequestMapping(value = "/private/payment/updatePaymentInfo", method = { POST })
	public void modifyPaymentByPaymentId(@RequestBody Payment log)
			throws APIExceptions {
		try {
			paymentService.updatePayments(log);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/getPaymentInfoById/{paymentId}", method = { GET })
	public ResponseEntity<Payment> getPaymentById(
			@PathVariable("paymentId") Long log) throws APIExceptions {
		try {
			return new ResponseEntity<Payment>(
					paymentService.getPaymentInfoById(log), HttpStatus.OK);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/getPaymentByUserId/{userId}", method = { GET })
	public ResponseEntity<List<Payment>> getPaymentByUserId(
			@PathVariable("userId") Long paymentUserlog) throws APIExceptions {
		try {
			return new ResponseEntity<List<Payment>>(
					paymentService.getPaymentInfoByUserId(paymentUserlog),
					HttpStatus.OK);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/paymentGatewayResponse", method = { POST }, consumes = { "application/x-www-form-urlencoded" })
	public ResponseEntity<PaymentGatewayResponseBean> paymentGatewayResponse(
			PaymentGatewayResponseBean pgrBean) throws APIExceptions {
		try {
			logger.error("CALLED FROM GATEWAY WEBHOOK  >>>>>"
					+ pgrBean.toString());
			return new ResponseEntity<PaymentGatewayResponseBean>(pgrBean,
					HttpStatus.OK);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/createNewPaymentRequest", method = { POST })
	public ResponseEntity<String> createNewPaymentRequest(
			@RequestBody PaymentGatewayRequestBean pgRequestBean)
			throws APIExceptions {
		try {
			JSONObject jsonObject = paymentService
					.createNewPaymentRequest(pgRequestBean);
			if (null != jsonObject)
				return new ResponseEntity<String>(jsonObject.toString(),
						HttpStatus.OK);
			else
				return new ResponseEntity<String>("", HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	@RequestMapping(value = "/private/payment/submitPaymentDetails", method = { PATCH })
	public ResponseEntity<String> getPaymentDetailsByTransactionId(
			@RequestBody PaymentGatewayRequestBean pgRequestBean)
			throws APIExceptions, JsonParseException, JsonMappingException,
			IOException {
		try {
			String transactionId = pgRequestBean.getTransactionId();
			JSONObject jsonObject = paymentService
					.getPaymentDetailsByTransactionId(transactionId);
			if (null != jsonObject)
				return new ResponseEntity<String>(jsonObject.toString(),
						HttpStatus.OK);
			else
				return new ResponseEntity<String>("", HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}
}
