package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IEmailController;
import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IEmailService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class EmailControllerImpl implements IEmailController {

	private static Logger log = Logger.getLogger(EmailControllerImpl.class);

	@Autowired
	IEmailService emailService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@RequestMapping(value = "/public/email/sendEmail", method = { POST })
	public ResponseEntity<String> sendEmail(
			@RequestParam("supportType") String supportType,
			@RequestParam("visitorName") String visitorName,
			@RequestParam("visitorEmail") String visitorEmail,
			@RequestParam("visitorCompanyName") String visitorCompanyName,
			@RequestParam("visitorContactNo") String visitorContactNo,
			@RequestParam("remarks") String remarks,
			@RequestParam String gcaptha) throws APIExceptions {
		if (ApplicationCommonUtil.isCaptchaValid(PintailerConstants.GCAPTCHA,
				gcaptha)) {
			return new ResponseEntity<String>(emailService.sendEmail(
					supportType, visitorName, visitorEmail, visitorCompanyName,
					visitorContactNo, remarks), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(
					"You are not authorized to contact the support.",
					HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	@RequestMapping(value = "/public/email/updateEmailInfo", method = { POST })
	public ResponseEntity<?> updateEmailControllerById(Email emailController)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Integer>(
				emailService.updateEmailControllerById(emailController),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/public/email/isEmailActive", method = { GET })
	public ResponseEntity<?> isEmailActive(String emailType)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Boolean>(
				emailService.isEmailActive(emailType), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/public/email/toggleEmailActive", method = { GET })
	public ResponseEntity<?> toggleEmailActive(
			@RequestParam("isActive") Boolean isActive) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Boolean>(
				emailService.toggleEmailActive(isActive), HttpStatus.OK);
	}
}
