package com.fw.dao;

import java.util.List;

import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;

public interface IAssetTypeManager {

	AssetType persistAssetType(AssetType logEntity) throws APIExceptions;

	void updateAssetTypeById(AssetType logEntity) throws APIExceptions;

	void deleteAssetTypeById(AssetType id) throws APIExceptions;

	List<AssetType> getAllAssetTypeRowMapper() throws APIExceptions;

	AssetType getAssetTypeById(long Id) throws APIExceptions;

	AssetType importAssetType(AssetType logEntity) throws APIExceptions;



}
