package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.controller.INotificationsController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.Notifications;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IEmailService;
import com.fw.services.INotificationsService;
import com.fw.utils.ApplicationCommonUtil;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class NotificationsControllerImpl implements INotificationsController {

	@Autowired
	IEmailService emailService;

	@Autowired
	INotificationsService notificationsService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/public/notifications/addNotifications", method = { POST })
	public ResponseEntity<?> persistNotifications(
			@RequestBody Notifications unit, @RequestParam String gcaptha)
			throws APIExceptions {
		if (ApplicationCommonUtil.isCaptchaValid(PintailerConstants.GCAPTCHA,
				gcaptha)) {
			updateDataSource();
			unit.setReadBy("Pintailer Support");
			return new ResponseEntity<Notifications>(
					notificationsService.persistNotifications(unit),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(
					"You are not authorized to contact the support.",
					HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	@RequestMapping(value = "/private/notifications/updateNotifications", method = { PATCH })
	public ResponseEntity<Notifications> updateNotificationsById(
			@RequestBody Notifications unit) throws APIExceptions {
		updateDataSource();
		return new ResponseEntity<Notifications>(
				notificationsService.updateNotificationsById(unit),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/notifications/getAllNotifications", method = { GET })
	public ResponseEntity<List<Notifications>> getAllNotifications()
			throws APIExceptions {
		return new ResponseEntity<List<Notifications>>(
				notificationsService.getAllNotifications(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/notifications/getNotificationsById", method = { GET })
	public ResponseEntity<Notifications> getNotificationsById(
			@RequestParam("notificationsId") long notificationsId)
			throws APIExceptions {
		return new ResponseEntity<Notifications>(
				notificationsService.getNotificationsById(notificationsId),
				HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(PintailerConstants.COMMON_ORG);
	}
}
