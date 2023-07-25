package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;

public interface ILinkedTicketController {

	ResponseEntity<?> addLinkedTicket(LinkedTicket linkedTicket)
			throws APIExceptions;

	ResponseEntity<?> updateLinkedTicket(LinkedTicket linkedTicket)
			throws APIExceptions;

	ResponseEntity<?> getAllLinkedTickets(int releaseId, int environmentId,
			String testCaseIds) throws APIExceptions;

	ResponseEntity<?> deleteLinkedTicket(int linkedTicketId)
			throws APIExceptions;
}
