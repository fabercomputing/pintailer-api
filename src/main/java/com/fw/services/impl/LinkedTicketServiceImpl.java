package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.ILinkedTicketManager;
import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ILinkedTicketService;

@Service
public class LinkedTicketServiceImpl implements ILinkedTicketService {
	@Autowired
	ILinkedTicketManager linkedTicketManager;

	@Override
	@Transactional
	public LinkedTicket persistLinkedTicket(LinkedTicket logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return linkedTicketManager.persistLinkedTicket(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public int updateLinkedTicket(LinkedTicket logEntity) throws APIExceptions {
		if (logEntity != null) {
			return linkedTicketManager.updateLinkedTicket(logEntity);
		} else {
			return 0;
		}
	}

	@Override
	public List<LinkedTicket> getAllLinkedTickets(int releaseId,
			int environmentId, String testCaseIds) throws APIExceptions {
		return linkedTicketManager.getAllLinkedTickets(releaseId,
				environmentId, testCaseIds);
	}

	@Override
	public int deleteLinkedTicket(int linkedTicketId) throws APIExceptions {
		return linkedTicketManager.deleteLinkedTicket(linkedTicketId);
	}
}
