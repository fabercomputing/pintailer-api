package com.fw.services;

import java.util.List;
import com.fw.domain.AssetInfo;
import com.fw.exceptions.APIExceptions;

public interface IAssetInfoService {

	AssetInfo addAssetInfo(AssetInfo assetInfo) throws APIExceptions;

	void updateAssetInfoById(AssetInfo assetInfo) throws APIExceptions;

	void deleteAssetInfoById(AssetInfo assetInfoId) throws APIExceptions;

	List<AssetInfo> getAssetInfo() throws APIExceptions;

	AssetInfo getAssetInfoById(int assetId) throws APIExceptions;
}
