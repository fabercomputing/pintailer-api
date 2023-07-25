package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;

public interface IAssetUserController {

	ResponseEntity<?> addAssetUser(AssetUser state) throws APIExceptions;

	ResponseEntity<?> getAssetUser() throws APIExceptions;

	ResponseEntity<?> updateAssetUserById(AssetUser bidForm)
			throws APIExceptions;

	ResponseEntity<?> getAssetUserById(String blockUsedId) throws APIExceptions;

	ResponseEntity<?> removeUser(AssetUser blockUsedId) throws APIExceptions;
}
