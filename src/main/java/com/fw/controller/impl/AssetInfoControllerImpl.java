package com.fw.controller.impl;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fw.config.AuthorizeUser;
import com.fw.controller.IAssetInfoController;
import com.fw.domain.AssetInfo;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetInfoService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class AssetInfoControllerImpl implements IAssetInfoController {

	private static Logger log = Logger.getLogger(AssetInfoControllerImpl.class);

	@Autowired
	IAssetInfoService assertInfoService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/AssetInfo/addAssetInfo", method = { POST })
	public ResponseEntity<?> addAssetInfo(@RequestBody AssetInfo state)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.addAssetInfo(state);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetInfo/getAssetInfoList", method = { GET })
	public ResponseEntity<?> getAssetInfo() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<AssetInfo>>(
				assertInfoService.getAssetInfo(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetInfo/updateAssetInfoById", method = { PATCH })
	public ResponseEntity<?> updateAssetInfoById(@RequestBody AssetInfo bidForm)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.updateAssetInfoById(bidForm);
		return new ResponseEntity<AssetInfo>(bidForm, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetInfo/getAssetInfoDetails/{AssetInfoId}", method = { GET })
	public ResponseEntity<?> getAssetInfoById(
			@PathVariable("AssetInfoId") int assetInfoId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<AssetInfo>(
				assertInfoService.getAssetInfoById(assetInfoId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetInfo/deleteAssetInfo/{AssetInfoId}", method = { DELETE })
	public ResponseEntity<?> removeUser(@RequestBody AssetInfo blockUsedId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.deleteAssetInfoById(blockUsedId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
