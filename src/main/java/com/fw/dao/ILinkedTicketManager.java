package com.fw.dao;

import java.util.List;

import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;

public interface ILinkedTicketManager {
	LinkedTicket persistLinkedTicket(LinkedTicket logEntity)
			throws APIExceptions;

	int updateLinkedTicket(LinkedTicket linkedTicket) throws APIExceptions;

	List<LinkedTicket> getAllLinkedTickets(int releaseId, int environmentId,
			String testCaseIds) throws APIExceptions;

	int deleteLinkedTicket(int linkedTicketId) throws APIExceptions;

	List<LinkedTicket> getLinkedTicketsForExecutionId(long executionId)
			throws APIExceptions;
}
