package com.fw.services;

import java.util.List;

import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;

public interface IHelpTopicService {

	HelpTopics persistHelpTopicsInfo(HelpTopics logEntity) throws APIExceptions;

	void updateHelpTopicsById(HelpTopics logEntity) throws APIExceptions;

	void deleteHelpTopicsById(HelpTopics id) throws APIExceptions;

	List<HelpTopics> getAllHelpTopicsRowMapper() throws APIExceptions;

	HelpTopics getHelpTopicsById(int Id) throws APIExceptions;

	List<HelpTopics> getHelpTopicsHierarchy() throws APIExceptions;
}
