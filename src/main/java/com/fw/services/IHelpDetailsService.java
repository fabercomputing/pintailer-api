package com.fw.services;

import java.util.List;

import com.fw.domain.HelpDetails;
import com.fw.exceptions.APIExceptions;

public interface IHelpDetailsService {
	HelpDetails persistHelpDetailsInfo(HelpDetails logEntity)
			throws APIExceptions;

	void updateHelpDetailsById(HelpDetails logEntity) throws APIExceptions;

	void deleteHelpDetailsById(HelpDetails id) throws APIExceptions;

	List<HelpDetails> getAllHelpDetailsRowMapper() throws APIExceptions;

	HelpDetails getHelpDetailsForTopicById(int Id) throws APIExceptions;
}
