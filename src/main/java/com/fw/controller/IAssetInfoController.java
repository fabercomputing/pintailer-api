package com.fw.controller;

import org.springframework.http.ResponseEntity;

import com.fw.domain.AssetInfo;
import com.fw.exceptions.APIExceptions;

public interface IAssetInfoController {

	ResponseEntity<?> addAssetInfo(AssetInfo assetInfo) throws APIExceptions;

	ResponseEntity<?> getAssetInfo() throws APIExceptions;

	ResponseEntity<?> updateAssetInfoById(AssetInfo assetInfo)
			throws APIExceptions;

	ResponseEntity<?> getAssetInfoById(int assetInfoId) throws APIExceptions;

	ResponseEntity<?> removeUser(AssetInfo assetInfoId) throws APIExceptions;
}
