package com.fw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.domain.Notifications;
import com.fw.exceptions.APIExceptions;

public interface INotificationsController {

	ResponseEntity<?> persistNotifications(Notifications unit, String gcaptha)
			throws APIExceptions;

	ResponseEntity<List<Notifications>> getAllNotifications()
			throws APIExceptions;

	ResponseEntity<Notifications> getNotificationsById(long notificationsId)
			throws APIExceptions;

	ResponseEntity<Notifications> updateNotificationsById(
			Notifications logEntity) throws APIExceptions;
}
