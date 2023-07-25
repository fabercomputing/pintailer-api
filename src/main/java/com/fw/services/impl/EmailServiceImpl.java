package com.fw.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.dao.IEmailManager;
import com.fw.domain.Email;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IEmailService;
import com.fw.utils.EmailUtils;
import com.fw.utils.ValueValidations;

@Service
public class EmailServiceImpl implements IEmailService {

	@Autowired
	EmailUtils emailUtils;

	@Autowired
	IEmailManager emailManager;

	@Override
	public String sendEmail(String supportType, String visitorName,
			String visitorEmail, String visitorCompanyName,
			String visitorContactNo, String remarks) throws APIExceptions {
		return emailUtils.emailSender(supportType, visitorName, visitorEmail,
				visitorCompanyName, visitorContactNo, remarks);
	}

	@Override
	public int updateEmailControllerById(Email emailController)
			throws APIExceptions {
		return emailManager.updateEmailControllerById(emailController);
	}

	@Override
	public Boolean isEmailActive(String emailType) throws APIExceptions {
		if (!ValueValidations.isValueValid(emailType)) {
			emailType = PintailerConstants.EMAIL_TYPE_CLIENT_STATUS;
		}
		return emailManager.isEmailActive(emailType);
	}

	@Override
	public Boolean toggleEmailActive(Boolean isActive) throws APIExceptions {
		Email email = new Email();
		email.setEmailControllerId(1);
		email.setEmailType(PintailerConstants.EMAIL_TYPE_CLIENT_STATUS);
		email.setActive(isActive);
		return updateEmailControllerById(email) > 0;
	}

}
