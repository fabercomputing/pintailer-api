package com.fw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.domain.HelpDetails;
import com.fw.exceptions.APIExceptions;

public interface IHelpDetailsController {
	ResponseEntity<HelpDetails> persistHelpDetailsInfo(HelpDetails logEntity)
			throws APIExceptions;

	ResponseEntity<HelpDetails> updateHelpDetailsById(HelpDetails logEntity)
			throws APIExceptions;

	void deleteHelpDetailsById(HelpDetails id) throws APIExceptions;

	ResponseEntity<List<HelpDetails>> getAllHelpDetailsRowMapper()
			throws APIExceptions;

	ResponseEntity<HelpDetails> getHelpDetailsForTopicById(int Id)
			throws APIExceptions;
}
