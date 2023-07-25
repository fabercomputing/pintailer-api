package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.INotificationsManager;
import com.fw.domain.Notifications;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.INotificationsService;
import com.fw.utils.ApplicationCommonUtil;

@Service
public class NotificationsServiceImpl implements INotificationsService {

	@Autowired
	INotificationsManager notificationManager;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Override
	@Transactional
	public Notifications persistNotifications(Notifications logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return notificationManager.persistNotifications(logEntity);
		} else
			throw new APIExceptions(
					"Information is not provided to the support. "
							+ "Please try again later or send email to ["
							+ PintailerConstants.SUPPORT_EMAIL + "].");
	}

	@Override
	@Transactional
	public Notifications updateNotificationsById(Notifications logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return notificationManager.updateNotificationsById(logEntity);
		}
		return null;
	}

	@Override
	public List<Notifications> getAllNotifications() throws APIExceptions {
		return notificationManager.getAllNotifications();
	}

	@Override
	public Notifications getNotificationsById(long notificationsId)
			throws APIExceptions {
		return notificationManager.getNotificationsById(notificationsId);
	}
}
