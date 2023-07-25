package com.fw.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fw.dao.IAssetUserManager;
import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetUserService;

@Service
public class AssetUserServiceImpl implements IAssetUserService {

	// private Logger log = Logger.getLogger(AssetUserServiceImpl.class);

	@Autowired
	IAssetUserManager AssetUserManager;

	@Override
	@Transactional
	public AssetUser addAssetUser(AssetUser logEntity) throws APIExceptions {
		if (logEntity != null) {
			return AssetUserManager.persistAssetUser(logEntity);
		} else
			return null;
	}

	@Override
	@Transactional
	public void updateAssetUserById(AssetUser logEntity) throws APIExceptions {
		if (logEntity != null) {
			AssetUserManager.updateAssetUserById(logEntity);
		}

	}

	@Override
	@Transactional
	public void deleteAssetUserById(AssetUser id) throws APIExceptions {
		AssetUserManager.deleteAssetUserById(id);
	}

	@Override
	public List<AssetUser> getAssetUser() throws APIExceptions {
		return AssetUserManager.getAllAssetUserRowMapper();
	}

	@Override
	public List<AssetUser> getAssetUserById(String userId) throws APIExceptions {
		return AssetUserManager.getAssetUserById(userId);
	}

}
