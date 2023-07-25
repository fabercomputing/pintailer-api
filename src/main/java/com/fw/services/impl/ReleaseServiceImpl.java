package com.fw.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IReleaseManager;
import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IReleaseService;

@Service
public class ReleaseServiceImpl implements IReleaseService {
	private Logger log = Logger.getLogger(ReleaseServiceImpl.class);

	@Autowired
	IReleaseManager releaseManager;

	@Override
	@Transactional
	public Release persistRelease(Release logEntity) throws APIExceptions {
		if (!(null == logEntity || null == logEntity.getReleaseNumber() || logEntity
				.getReleaseNumber().trim().equals(""))) {
			return releaseManager.persistRelease(logEntity);
		} else {
			log.error("Invalid release number is given");
			throw new APIExceptions("Invalid release number is given");
		}
	}

	@Override
	@Transactional
	public Release updateRelease(Release logEntity) throws APIExceptions {
		if (!(null == logEntity || null == logEntity.getReleaseNumber() || logEntity
				.getReleaseNumber().trim().equals(""))) {
			if (releaseManager.updateRelease(logEntity) > 0) {
				return logEntity;
			} else {
				log.error("Some error occured while updating release no with value ["
						+ logEntity.getReleaseNumber() + "].");
				return null;
			}
		} else {
			log.error("Invalid release number is given");
			return null;
		}
	}

	@Override
	public List<Release> getAllReleases(int clientProjectId, String condition)
			throws APIExceptions {
		return releaseManager.getAllReleases(clientProjectId, condition);
	}

	@Override
	@Transactional
	public int deleteRelease(int releaseId) throws APIExceptions {
		return releaseManager.deleteRelease(releaseId);
	}

	@Override
	public Release getReleaseByProjectAndRelease(int clientProjectId,
			int releaseId) throws APIExceptions {
		return releaseManager.getReleaseByProjectAndRelease(clientProjectId,
				releaseId);
	}
}
