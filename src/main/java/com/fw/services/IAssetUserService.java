package com.fw.services;

import java.util.List;

import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;

public interface IAssetUserService {

	AssetUser addAssetUser(AssetUser logEntity) throws APIExceptions;

	void updateAssetUserById(AssetUser logEntity) throws APIExceptions;

	void deleteAssetUserById(AssetUser logEntity) throws APIExceptions;

	List<AssetUser> getAssetUser() throws APIExceptions;

	List<AssetUser> getAssetUserById(String userId) throws APIExceptions;

}
