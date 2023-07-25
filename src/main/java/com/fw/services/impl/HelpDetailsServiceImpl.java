package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IHelpDetailsManager;
import com.fw.domain.HelpDetails;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IHelpDetailsService;

@Service
public class HelpDetailsServiceImpl implements IHelpDetailsService {

	@Autowired
	IHelpDetailsManager helpDetailsManager;

	@Override
	@Transactional
	public HelpDetails persistHelpDetailsInfo(HelpDetails logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			return helpDetailsManager.persistHelpDetailsInfo(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateHelpDetailsById(HelpDetails logEntity)
			throws APIExceptions {
		if (logEntity != null) {
			helpDetailsManager.updateHelpDetailsById(logEntity);
		}
	}

	@Override
	@Transactional
	public void deleteHelpDetailsById(HelpDetails helpDetails)
			throws APIExceptions {
		helpDetailsManager.deleteHelpDetailsById(helpDetails);
	}

	@Override
	public List<HelpDetails> getAllHelpDetailsRowMapper() throws APIExceptions {
		return helpDetailsManager.getAllHelpDetailsRowMapper();
	}

	@Override
	public HelpDetails getHelpDetailsForTopicById(int Id) throws APIExceptions {
		return helpDetailsManager.getHelpDetailsForTopicById(Id);
	}
}
