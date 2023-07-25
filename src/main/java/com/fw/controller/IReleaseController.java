package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.Release;
import com.fw.exceptions.APIExceptions;

public interface IReleaseController {

	ResponseEntity<?> addRelease(Release release) throws APIExceptions;

	ResponseEntity<?> updateRelease(Release release) throws APIExceptions;

	ResponseEntity<?> getAllReleases(int clientProjectId, String condition)
			throws APIExceptions;

	ResponseEntity<?> deleteRelease(int releaseId) throws APIExceptions;
}
