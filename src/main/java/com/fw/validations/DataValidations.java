package com.fw.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;

import com.fw.exceptions.APIExceptions;
import com.fw.utils.LocalUtils;

@Configuration
public class DataValidations {

	public void phoneNumberValidation(String phoneNumber) throws APIExceptions {

		if (phoneNumber == null || phoneNumber.isEmpty()
				|| "".equals(phoneNumber.trim()))
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "NoNumberSpecified"));

		String regex = "^91([0-9]){10}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber);
		if (!matcher.matches()) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "InValidPhoneNumber"));
		}

	}

	public void passwordValidation(String password) throws APIExceptions {

		if (password == null || password.isEmpty())
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "notAvalidPassword"));

		String regex = "^([a-zA-Z0-9]).{5,20}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		if (!matcher.matches()) {
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "notAvalidPassword"));
		}

	}

	public void paymentGatewayMinimunAmountValidation(Double amount)
			throws APIExceptions {

		if (!(amount >= 9.00))
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "invalidPaymentAmount"));

	}

}
