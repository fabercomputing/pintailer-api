package com.fw.dao;

import java.util.List;

import com.fw.domain.Notifications;
import com.fw.exceptions.APIExceptions;

public interface INotificationsManager {

	Notifications persistNotifications(Notifications logEntity)
			throws APIExceptions;

	List<Notifications> getAllNotifications() throws APIExceptions;

	Notifications getNotificationsById(long notificationsId)
			throws APIExceptions;

	Notifications updateNotificationsById(Notifications logEntity)
			throws APIExceptions;
}
