package com.fw.services;

import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;

public interface IEmailService {
	String sendEmail(String supportType, String visitorName,
			String visitorEmail, String visitorCompanyName,
			String visitorContactNo, String remarks) throws APIExceptions;

	int updateEmailControllerById(Email emailController) throws APIExceptions;

	Boolean isEmailActive(String emailType) throws APIExceptions;

	Boolean toggleEmailActive(Boolean isActive) throws APIExceptions;
}
