package com.fw.services;

import java.util.List;
import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;

public interface IAssetTypeService {

	AssetType addAssetType(AssetType logEntity) throws APIExceptions;

	void updateAssetTypeById(AssetType logEntity) throws APIExceptions;

	void deleteAssetTypeById(AssetType id) throws APIExceptions;

	List<AssetType> getAssetType() throws APIExceptions;

	AssetType getAssetTypeById(long assetTypeId) throws APIExceptions;

}
