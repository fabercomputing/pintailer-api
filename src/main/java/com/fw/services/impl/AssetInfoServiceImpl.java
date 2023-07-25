package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IAssetInfoManager;
import com.fw.domain.AssetInfo;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetInfoService;

@Service
public class AssetInfoServiceImpl implements IAssetInfoService {

	// private Logger log = Logger.getLogger(AssetInfoServiceImpl.class);

	@Autowired
	IAssetInfoManager assetInfoManager;

	@Override
	@Transactional
	public AssetInfo addAssetInfo(AssetInfo logEntity) throws APIExceptions {
		if (logEntity != null) {
			return assetInfoManager.persistAssetInfo(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateAssetInfoById(AssetInfo logEntity) throws APIExceptions {
		if (logEntity != null) {
			assetInfoManager.updateAssetInfoById(logEntity);
		}
	}

	@Override
	@Transactional
	public void deleteAssetInfoById(AssetInfo id) throws APIExceptions {
		assetInfoManager.deleteAssetInfoById(id);
	}

	@Override
	public List<AssetInfo> getAssetInfo() throws APIExceptions {
		return assetInfoManager.getAllAssetInfoRowMapper();
	}

	@Override
	public AssetInfo getAssetInfoById(int assetId) throws APIExceptions {
		return assetInfoManager.getAssetInfoById(assetId);
	}

}
