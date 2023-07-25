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
import com.fw.controller.IAssetUserController;
import com.fw.domain.AssetUser;
import com.fw.exceptions.APIExceptions;
import com.fw.services.IAssetUserService;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class AssetUserControllerImpl implements IAssetUserController {

	private static Logger log = Logger.getLogger(AssetUserControllerImpl.class);

	@Autowired
	IAssetUserService assertInfoService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/AssetUser/addAssetUser", method = { POST })
	public ResponseEntity<?> addAssetUser(@RequestBody AssetUser state)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<AssetUser>(
				assertInfoService.addAssetUser(state), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetUser/getAssetUserList", method = { GET })
	public ResponseEntity<?> getAssetUser() throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<AssetUser>>(
				assertInfoService.getAssetUser(), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetUser/updateAssetUserById", method = { PATCH })
	public ResponseEntity<?> updateAssetUserById(@RequestBody AssetUser bidForm)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.updateAssetUserById(bidForm);
		return new ResponseEntity<AssetUser>(bidForm, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetUser/getAssetUserDetails/{AssetUserId}/", method = { GET })
	public ResponseEntity<?> getAssetUserById(
			@PathVariable("AssetUserId") String blockUsedId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<List<AssetUser>>(
				assertInfoService.getAssetUserById(blockUsedId), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/AssetUser/deleteAssetUser/{AssetUserId}", method = { DELETE })
	public ResponseEntity<?> removeUser(@RequestBody AssetUser blockUsedId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		assertInfoService.deleteAssetUserById(blockUsedId);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
