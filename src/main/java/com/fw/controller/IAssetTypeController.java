package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;

public interface IAssetTypeController {

	ResponseEntity<?> addAssetType(AssetType state) throws APIExceptions;

	ResponseEntity<?> getAssetType() throws APIExceptions;

	ResponseEntity<?> updateAssetTypeById(AssetType bidForm)
			throws APIExceptions;

	ResponseEntity<?> getAssetTypeById(long blockUsedId) throws APIExceptions;

	ResponseEntity<?> removeUser(AssetType blockUsedId) throws APIExceptions;
}
