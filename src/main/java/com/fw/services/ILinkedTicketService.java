package com.fw.services;

import java.util.List;

import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;

public interface ILinkedTicketService {

	LinkedTicket persistLinkedTicket(LinkedTicket logEntity)
			throws APIExceptions;

	int updateLinkedTicket(LinkedTicket logEntity) throws APIExceptions;

	List<LinkedTicket> getAllLinkedTickets(int releaseId, int environmentId,
			String testCaseIds) throws APIExceptions;

	int deleteLinkedTicket(int linkedTicketId) throws APIExceptions;
}
