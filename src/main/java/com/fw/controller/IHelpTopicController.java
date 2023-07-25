package com.fw.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;

public interface IHelpTopicController {

	ResponseEntity<HelpTopics> persistHelpTopicsInfo(HelpTopics logEntity)
			throws APIExceptions;

	ResponseEntity<HelpTopics> updateHelpTopicsById(HelpTopics logEntity) throws APIExceptions;

	void deleteHelpTopicsById(HelpTopics id) throws APIExceptions;

	ResponseEntity<List<HelpTopics>> getAllHelpTopicsRowMapper()
			throws APIExceptions;

	ResponseEntity<HelpTopics> getHelpTopicsById(int Id) throws APIExceptions;

	ResponseEntity<List<HelpTopics>> getHelpTopicsHierarchy()
			throws APIExceptions;
}
