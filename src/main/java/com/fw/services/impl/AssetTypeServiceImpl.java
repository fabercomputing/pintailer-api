package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IAssetTypeManager;
import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetTypeService;

@Service
public class AssetTypeServiceImpl implements IAssetTypeService {

	// private Logger log = Logger.getLogger(AssetTypeServiceImpl.class);

	@Autowired
	IAssetTypeManager AssetTypeManager;

	@Override
	@Transactional
	public AssetType addAssetType(AssetType logEntity) throws APIExceptions {
		if (logEntity != null) {
			return AssetTypeManager.persistAssetType(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateAssetTypeById(AssetType logEntity) throws APIExceptions {
		if (logEntity != null) {
			AssetTypeManager.updateAssetTypeById(logEntity);
		}
	}

	@Override
	@Transactional
	public void deleteAssetTypeById(AssetType id) throws APIExceptions {
		AssetTypeManager.deleteAssetTypeById(id);
	}

	@Override
	public List<AssetType> getAssetType() throws APIExceptions {
		return AssetTypeManager.getAllAssetTypeRowMapper();
	}

	@Override
	public AssetType getAssetTypeById(long assetTypeId) throws APIExceptions {
		return AssetTypeManager.getAssetTypeById(assetTypeId);
	}

}
