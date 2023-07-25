package com.fw.dao;

import java.util.List;

import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;

public interface IReleaseManager {
	Release persistRelease(Release release) throws APIExceptions;

	int updateRelease(Release release) throws APIExceptions;

	int deleteRelease(int releaseId) throws APIExceptions;

	List<Release> getAllReleases(int clientProjectId, String condition)
			throws APIExceptions;

	Release getReleaseByProjectAndRelease(int clientProjectId, int releaseId)
			throws APIExceptions;
}
