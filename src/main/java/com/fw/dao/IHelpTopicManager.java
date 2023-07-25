package com.fw.dao;

import java.util.List;

import com.fw.domain.HelpTopics;
import com.fw.exceptions.APIExceptions;

public interface IHelpTopicManager {

	HelpTopics persistHelpTopicsInfo(HelpTopics logEntity) throws APIExceptions;

	void updateHelpTopicsById(HelpTopics logEntity) throws APIExceptions;

	void deleteHelpTopicsById(HelpTopics id) throws APIExceptions;

	List<HelpTopics> getAllHelpTopicsRowMapper() throws APIExceptions;

	HelpTopics getHelpTopicsById(int Id) throws APIExceptions;

	List<String> getHelpTopicsHierarchy(int helpTopicsId) throws APIExceptions;

	List<Long> getAllChildHelpTopics(int parentHelpTopicId)
			throws APIExceptions;
}
