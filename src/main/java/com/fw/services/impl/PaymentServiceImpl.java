package com.fw.services.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fw.bean.PaymentGatewayRequestBean;
import com.fw.bean.PaymentResponseBean;
import com.fw.bean.PaymentResponseTransactionBean;
import com.fw.dao.IPaymentManager;
import com.fw.domain.Payment;
import com.fw.enums.PaymentDirection;
import com.fw.enums.PaymentReceiveMethod;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IPaymentService;
import com.fw.utils.InstamojoPaymentGateway;
import com.fw.utils.LocalUtils;

@Service
public class PaymentServiceImpl implements IPaymentService {

	public final static Logger log = Logger.getLogger(PaymentServiceImpl.class);

	@Autowired
	private IPaymentManager paymentManager;

	@Override
	public List<Payment> getAllPayments() throws APIExceptions {
		return paymentManager.getAllPaymentRowMapper();
	}

	@Override
	@Transactional
	public void deletePayments(Payment payment) throws APIExceptions {
		paymentManager.deletePayment(payment);
	}

	@Override
	@Transactional
	public void updatePayments(Payment payment) throws DataAccessException,
			APIExceptions {
		paymentManager.modifyPaymentsByPaymentId(payment);
	}

	@Override
	public void getPaymentInfo(Payment paymentEntity) throws APIExceptions {
		paymentManager.persist(paymentEntity);

	}

	@Override
	public Payment getPaymentInfoById(Long paymentEntity) throws APIExceptions {
		return paymentManager.getPaymentById(paymentEntity);
	}

	@Override
	public List<Payment> getPaymentInfoByUserId(Long paymentEntity)
			throws APIExceptions {
		return paymentManager.getPaymentByUserId(paymentEntity);
	}

	Payment paymentinfo = new Payment();

	@Override
	@Transactional
	public JSONObject createNewPaymentRequest(
			PaymentGatewayRequestBean pgRequestBean) throws APIExceptions {
		JSONObject jsonObject = null;
		try {
			jsonObject = InstamojoPaymentGateway.paymentOrder(pgRequestBean,
					null);
			if (jsonObject != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				PaymentResponseBean paymentResponse = objectMapper.readValue(
						jsonObject.toString(), PaymentResponseBean.class);
				paymentinfo.setPaymentGatewayRequestId(paymentResponse
						.getOrder().getId());
				paymentinfo.setPaymentGatewayPaymentId(paymentResponse
						.getOrder().getTransaction_id());
				paymentinfo.setAmountRecieved(paymentResponse.getOrder()
						.getAmount());
				paymentinfo.setAmountRequested(pgRequestBean
						.getAmountRequested());
				paymentinfo.setCurrency(paymentResponse.getOrder()
						.getCurrency());
				paymentinfo.setPackageId(pgRequestBean.getPackageId());
				paymentinfo
						.setReceiveMethod(PaymentReceiveMethod.PAYMENT_GATEWAY);
				paymentinfo.setUserId(pgRequestBean.getUserId());
				paymentinfo.setPaymentDirections(PaymentDirection.CREDIT);
				paymentinfo.setPaymentStatus(paymentResponse.getOrder()
						.getStatus());
				paymentinfo.setPaymentTime(paymentResponse.getOrder()
						.getCreated_at());
				paymentinfo.setCreatedBy(pgRequestBean.getUserId());
				paymentinfo.setModifiedBy(pgRequestBean.getUserId());

				String getPaymentGatewayResponse = paymentResponse.toString();
				paymentinfo
						.setPaymentGatewayResponse(getPaymentGatewayResponse);

				paymentManager.persistPaymentRequest(paymentinfo);
				return jsonObject;
			} else {
				throw new APIExceptions(LocalUtils.getStringLocale(
						"fw_test_mgmt_locale", "PaymentFailure"));
			}
		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}

	@Override
	public JSONObject getPaymentDetailsByTransactionId(String transactionId)
			throws JsonParseException, JsonMappingException, IOException,
			APIExceptions {
		try {
			JSONObject getTranasactionJsonObject = null;
			getTranasactionJsonObject = InstamojoPaymentGateway
					.getDetailsByTransactionId(transactionId);
			if (getTranasactionJsonObject != null) {
				paymentinfo = paymentManager
						.getPaymentDetailsByTransactionId(transactionId);
				ObjectMapper objectMapper = new ObjectMapper();
				PaymentResponseTransactionBean paymentTransactionResponse = objectMapper
						.readValue(getTranasactionJsonObject.toString(),
								PaymentResponseTransactionBean.class);
				if (paymentTransactionResponse.getStatus().equals("completed")) {
					paymentinfo.setAmountRecieved(paymentTransactionResponse
							.getAmount());
					paymentinfo.setPaymentStatus(paymentTransactionResponse
							.getStatus());
					paymentinfo.setPaymentTime(paymentTransactionResponse
							.getCreated_at());
					String getPaymentGatewayResponse = getTranasactionJsonObject
							.toString();
					paymentinfo
							.setPaymentGatewayResponse(getPaymentGatewayResponse);
					paymentManager.updatePaymentResponse(paymentinfo,
							transactionId);

					String bookedLP = paymentTransactionResponse.getPhone();
					log.info("====bookedLP===== :" + bookedLP);
					bookedLP = bookedLP.replaceAll("[-+.^:,]", "");
					log.info("====bookedLP After===== :" + bookedLP);

					return getTranasactionJsonObject;
				} else {
					throw new APIExceptions(LocalUtils.getStringLocale(
							"fw_test_mgmt_locale", "PaymentFailure"));
				}
			} else {
				throw new APIExceptions(LocalUtils.getStringLocale(
						"fw_test_mgmt_locale", "PaymentFailure"));
			}

		} catch (Exception e) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "PaymentFailure"));
		}
	}
}
