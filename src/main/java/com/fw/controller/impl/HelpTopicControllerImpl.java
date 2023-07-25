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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.controller.IHelpTopicController;
import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IHelpTopicService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class HelpTopicControllerImpl implements IHelpTopicController {

	@Autowired
	IHelpTopicService helpTopicService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/HelpTopics/addHelpTopics", method = { POST })
	public ResponseEntity<HelpTopics> persistHelpTopicsInfo(
			@RequestBody HelpTopics logEntity) throws APIExceptions {
		return new ResponseEntity<HelpTopics>(
				helpTopicService.persistHelpTopicsInfo(logEntity),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpTopics/updateHelpTopicsById", method = { PATCH })
	public ResponseEntity<HelpTopics> updateHelpTopicsById(
			@RequestBody HelpTopics bidForm) throws APIExceptions {
		helpTopicService.updateHelpTopicsById(bidForm);
		return new ResponseEntity<HelpTopics>(bidForm, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpTopics/deleteHelpTopics/{HelpTopicsId}", method = { DELETE })
	public void deleteHelpTopicsById(@RequestBody HelpTopics helpTopicId)
			throws APIExceptions {
		helpTopicService.deleteHelpTopicsById(helpTopicId);
	}

	@Override
	@RequestMapping(value = "/private/HelpTopics/getHelpTopicsList", method = { GET })
	public ResponseEntity<List<HelpTopics>> getAllHelpTopicsRowMapper()
			throws APIExceptions {
		return new ResponseEntity<List<HelpTopics>>(
				helpTopicService.getAllHelpTopicsRowMapper(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpTopics/getHelpTopicsDetailsById/{HelpTopicsId}", method = { GET })
	public ResponseEntity<HelpTopics> getHelpTopicsById(
			@PathVariable("HelpTopicsId") int helpTopicsId)
			throws APIExceptions {
		return new ResponseEntity<HelpTopics>(
				helpTopicService.getHelpTopicsById(helpTopicsId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/HelpTopics/getHelpTopicsHierarchy", method = { GET })
	public ResponseEntity<List<HelpTopics>> getHelpTopicsHierarchy()
			throws APIExceptions {
		return new ResponseEntity<List<HelpTopics>>(
				helpTopicService.getHelpTopicsHierarchy(), HttpStatus.OK);
	}
}
