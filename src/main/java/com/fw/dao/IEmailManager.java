package com.fw.dao;

import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;

public interface IEmailManager {
	Email persistEmailController(Email emailController) throws APIExceptions;

	int updateEmailControllerById(Email emailController) throws APIExceptions;

	void deleteEmailControllerById(int emailControllerId) throws APIExceptions;

	Email getEmailControllerById(int emailControllerId, String isActive)
			throws APIExceptions;

	Boolean isEmailActive(String emailType) throws APIExceptions;
}
