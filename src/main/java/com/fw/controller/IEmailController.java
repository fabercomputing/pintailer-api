package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;

public interface IEmailController {

	ResponseEntity<String> sendEmail(String message, String subject,
			String attachmentPath, String recipientsTO, String recipientsCC,
			String userName, String gcaptha) throws APIExceptions;

	ResponseEntity<?> updateEmailControllerById(Email emailController)
			throws APIExceptions;

	ResponseEntity<?> isEmailActive(String emailType) throws APIExceptions;

	ResponseEntity<?> toggleEmailActive(Boolean isActive) throws APIExceptions;

}
