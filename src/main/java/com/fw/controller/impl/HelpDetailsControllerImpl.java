package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
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

import com.fw.controller.IHelpDetailsController;
import com.fw.domain.HelpDetails;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IHelpDetailsService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class HelpDetailsControllerImpl implements IHelpDetailsController {

	@Autowired
	IHelpDetailsService helpDetailsService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/HelpDetails/addHelpDetails", method = { POST })
	public ResponseEntity<HelpDetails> persistHelpDetailsInfo(
			@RequestBody HelpDetails logEntity) throws APIExceptions {
		return new ResponseEntity<HelpDetails>(
				helpDetailsService.persistHelpDetailsInfo(logEntity),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpDetails/updateHelpDetailsById", method = { PATCH })
	public ResponseEntity<HelpDetails> updateHelpDetailsById(
			@RequestBody HelpDetails bidForm) throws APIExceptions {
		helpDetailsService.updateHelpDetailsById(bidForm);
		return new ResponseEntity<HelpDetails>(bidForm, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpDetails/deleteHelpDetails", method = { DELETE })
	public void deleteHelpDetailsById(@RequestBody HelpDetails helpDetails)
			throws APIExceptions {
		helpDetailsService.deleteHelpDetailsById(helpDetails);
	}

	@Override
	@RequestMapping(value = "/private/HelpDetails/getHelpDetailsList", method = { GET })
	public ResponseEntity<List<HelpDetails>> getAllHelpDetailsRowMapper()
			throws APIExceptions {
		return new ResponseEntity<List<HelpDetails>>(
				helpDetailsService.getAllHelpDetailsRowMapper(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpDetails/getHelpDetailsForTopicById", method = { GET })
	public ResponseEntity<HelpDetails> getHelpDetailsForTopicById(
			@RequestParam("topicId") int topicId) throws APIExceptions {
		return new ResponseEntity<HelpDetails>(
				helpDetailsService.getHelpDetailsForTopicById(topicId),
				HttpStatus.OK);
	}
}
