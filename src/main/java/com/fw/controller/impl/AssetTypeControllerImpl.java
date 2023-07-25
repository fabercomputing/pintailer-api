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
import com.fw.controller.IAssetTypeController;
import com.fw.domain.AssetType;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetTypeService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class AssetTypeControllerImpl implements IAssetTypeController {

	private static Logger log = Logger.getLogger(AssetTypeControllerImpl.class);

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	IAssetTypeService assertInfoService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/AssetType/addAssetType", method = { POST })
	public ResponseEntity<?> addAssetType(@RequestBody AssetType state)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.addAssetType(state);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetType/getAssetTypeList", method = { GET })
	public ResponseEntity<?> getAssetType() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<AssetType>>(
				assertInfoService.getAssetType(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetType/updateAssetTypeById", method = { PATCH })
	public ResponseEntity<?> updateAssetTypeById(@RequestBody AssetType bidForm)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.updateAssetTypeById(bidForm);
		return new ResponseEntity<AssetType>(bidForm, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetType/getAssetTypeDetails/{AssetTypeId}", method = { GET })
	public ResponseEntity<?> getAssetTypeById(
			@PathVariable("AssetTypeId") long blockUsedId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<AssetType>(
				assertInfoService.getAssetTypeById(blockUsedId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetType/deleteAssetType/{AssetTypeId}", method = { DELETE })
	public ResponseEntity<?> removeUser(@RequestBody AssetType blockUsedId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.deleteAssetTypeById(blockUsedId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
