package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 
 * @author Sumit Srivastava
 *
 */
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.ILinkedTicketController;
import com.fw.domain.LinkedTicket;
import com.fw.exceptions.APIExceptions;
import com.fw.services.ILinkedTicketService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class LinkedTicketControllerImpl implements ILinkedTicketController {

	private static Logger log = Logger
			.getLogger(LinkedTicketControllerImpl.class);

	@Autowired
	ILinkedTicketService linkedTicketService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/linkedTicket/addLinkedTicket", method = { POST })
	public ResponseEntity<?> addLinkedTicket(@RequestBody LinkedTicket linkedTicket)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<LinkedTicket>(
				linkedTicketService.persistLinkedTicket(linkedTicket), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/linkedTicket/updateLinkedTicket", method = { PATCH })
	public ResponseEntity<?> updateLinkedTicket(@RequestBody LinkedTicket linkedTicket)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Integer>(
				linkedTicketService.updateLinkedTicket(linkedTicket), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/linkedTicket/getAllLinkedTickets", method = { GET })
	public ResponseEntity<?> getAllLinkedTickets(
			@RequestParam("releaseId") int releaseId,
			@RequestParam("environmentId") int environmentId,
			@RequestParam("testCaseIds") String testCaseIds)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<LinkedTicket>>(
				linkedTicketService.getAllLinkedTickets(releaseId,
						environmentId, testCaseIds), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/linkedTicket/deleteLinkedTicket/{linkedTicketId}", method = { DELETE })
	public ResponseEntity<?> deleteLinkedTicket(
			@PathVariable("linkedTicketId") int linkedTicketId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Integer>(
				linkedTicketService.deleteLinkedTicket(linkedTicketId),
				HttpStatus.OK);

	}
}
