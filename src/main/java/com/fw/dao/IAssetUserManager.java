package com.fw.dao;

import java.util.List;

import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;

public interface IAssetUserManager {

	AssetUser persistAssetUser(AssetUser logEntity) throws APIExceptions;

	void updateAssetUserById(AssetUser logEntity) throws APIExceptions;

	void deleteAssetUserById(AssetUser id) throws APIExceptions;

	List<AssetUser> getAllAssetUserRowMapper() throws APIExceptions;

	List<AssetUser> getAssetUserById(String Id) throws APIExceptions;

	AssetUser importAssetUser(AssetUser logEntity) throws APIExceptions;

}
