package com.fw.services;

import java.util.List;

import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;

public interface IReleaseService {

	Release persistRelease(Release logEntity) throws APIExceptions;

	Release updateRelease(Release logEntity) throws APIExceptions;

	List<Release> getAllReleases(int clientProjectId, String condition)
			throws APIExceptions;

	int deleteRelease(int releaseId) throws APIExceptions;

	Release getReleaseByProjectAndRelease(int clientProjectId, int releaseId)
			throws APIExceptions;
}
