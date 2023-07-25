package com.fw.controller.impl;

/**
 * 
 * @author Sumit Srivastava
 *
 */

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fw.config.AuthorizeUser;
import com.fw.controller.ITestCaseController;
import com.fw.db.ClientDatabaseContextHolder;
import com.fw.domain.TestCase;
import com.fw.domain.TestCaseClientBean;
import com.fw.domain.TestCaseVersion;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.exceptions.ImportDuplicateDataException;
import com.fw.services.IModulesService;
import com.fw.services.ITestCaseService;
import com.fw.services.ITestStepService;
import com.fw.utils.ApplicationCommonUtil;
import com.fw.utils.LocalUtils;

@Controller
@RequestMapping(value = "/fwTestManagement")
public class TestCaseControllerImpl implements ITestCaseController {

	private Logger log = Logger.getLogger(TestCaseControllerImpl.class);

	@Autowired
	ITestCaseService testCaseService;

	@Autowired
	ITestStepService testStepService;

	@Autowired
	AuthorizeUser authorizeUser;

	@Autowired
	ApplicationCommonUtil applicationCommonUtil;

	@Autowired
	IModulesService modulesService;

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCase/addTestCase", method = { POST })
	public ResponseEntity<?> addTestCase(@RequestBody TestCase testCase)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForModuleId(testCase.getModuleId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<TestCase>(
				testCaseService.addTestCase(testCase), HttpStatus.OK);
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCase/importTestCaseOld", method = {
			POST }, consumes = { "multipart/form-data" })
	public ResponseEntity<?> importTestCase(
			@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile uploadfile,
			@RequestParam("clientProjectId") int clientProjectId)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		try {
			String filename = uploadfile.getOriginalFilename();
			filename = filename.substring(filename.lastIndexOf(".") + 1);
			if (filename.toLowerCase().equals("csv")) {
				List<TestCase> testCaseList = null;
				testCaseList = testCaseService.importTestCase(uploadfile,
						clientProjectId);
				return new ResponseEntity<List<?>>(testCaseList, HttpStatus.OK);
			}
			if (filename.toLowerCase().equals("feature")) {
				testStepService.importTestStepFeature(uploadfile,
						clientProjectId);
				return new ResponseEntity<List<?>>(HttpStatus.OK);
			}
			throw new Exception(LocalUtils
					.getStringLocale("fw_test_mgmt_locale", "ValidImportFile"));
		} catch (APIExceptions exception) {
			throw new APIExceptions(exception.getMessage());
		} catch (NullPointerException exception) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"InvalidHeader");
			log.error(message);
			throw new APIExceptions(message);
		}
	}

	@Override
	@ResponseBody
	@RequestMapping(value = "/private/testCase/importTestCase", method = {
			POST }, consumes = { "multipart/form-data" })
	public ResponseEntity<?> importTestCaseNew(
			@RequestPart("file") @Valid @NotNull @NotBlank MultipartFile uploadfile,
			@RequestParam("clientProjectId") int clientProjectId)
			throws APIExceptions, ImportDuplicateDataException,
			NullPointerException, EmptyDataException, Exception {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		try {
			String filename = uploadfile.getOriginalFilename();
			filename = filename.substring(filename.lastIndexOf(".") + 1);
			if (filename.toLowerCase().equals("csv")) {
				List<TestCase> testCaseList = null;
				testCaseList = testCaseService.importTestCaseNew(uploadfile,
						clientProjectId);
				return new ResponseEntity<List<?>>(testCaseList, HttpStatus.OK);
			}
			if (filename.toLowerCase().equals("feature")) {
				testStepService.importTestStepFeature(uploadfile,
						clientProjectId);
				return new ResponseEntity<List<?>>(HttpStatus.OK);
			} else if (filename.toLowerCase().equals("java")) {
				testStepService.importTestStepFromJava(uploadfile, clientProjectId);
				return new ResponseEntity<List<?>>(HttpStatus.OK);
			}
			throw new Exception(LocalUtils
					.getStringLocale("fw_test_mgmt_locale", "ValidImportFile"));
		} catch (APIExceptions exception) {
			throw new APIExceptions(exception.getMessage());
		} catch (NullPointerException exception) {
			String message = LocalUtils.getStringLocale("fw_test_mgmt_locale",
					"InvalidHeader");
			log.error(message);
			throw new APIExceptions(message);
		}
	}

	@Override
	@RequestMapping(value = "/private/testCase/getTestCasesClientList", method = {
			GET })
	public ResponseEntity<?> getTestCasesClientBeans(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("applicable") String applicable,
			@RequestParam("searchTxt") String searchTxt,
			@RequestParam("sortByColumn") String sortByColumn,
			@RequestParam("ascOrDesc") String ascOrDesc,
			@RequestParam("limit") int limit,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("isDeleted") String isDeleted) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseClientBean>>(
				testCaseService.getTestCaseList(clientProjectId, 0, null, null,
						applicable, null, searchTxt, sortByColumn, ascOrDesc,
						limit, pageNumber, startDate, endDate, isDeleted, true),
				HttpStatus.OK);

		// testCaseService.getTestCaseList(clientProjectId, applicable,
		// null, searchTxt, sortByColumn, ascOrDesc, limit,
		// pageNumber)
	}

	@Override
	@RequestMapping(value = "/private/testCase/updateTestCaseById", method = {
			PATCH })
	public ResponseEntity<?> updateTestCaseById(@RequestBody TestCase testCase,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("isTestCaseDataUpdated") boolean isTestCaseDataUpdated)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForModuleId(testCase.getModuleId());
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseService.updateTestCaseById(testCase, releaseId,
				isTestCaseDataUpdated);
		return new ResponseEntity<TestCase>(testCase, HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCase/deleteTestCase/{testCaseId}", method = {
			DELETE })
	public ResponseEntity<?> removeTestCase(
			@PathVariable("testCaseId") int testCaseId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		testCaseService.deleteTestCaseById(testCaseId);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	@Override
	@RequestMapping(value = "/private/testCase/getTestCaseByModule", method = {
			GET })
	public ResponseEntity<?> getTestCaseByModule(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("releaseId") int releaseId,
			@RequestParam("moduleId") long moduleId,
			@RequestParam("applicable") String applicable,
			@RequestParam("isDeleted") String isDeleted) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForModuleId(moduleId);
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			updateDataSource();
			return new ResponseEntity<List<TestCaseClientBean>>(
					testCaseService.getTestCaseByModuleId(clientProjectId,
							releaseId, moduleId, applicable, isDeleted),
					HttpStatus.OK);
		} catch (APIExceptions e) {
			log.error(e);
			throw new APIExceptions(LocalUtils.getStringLocale(
					"fw_test_mgmt_locale", "TestCaseByModule"));
		}
	}

	@Override
	@RequestMapping(value = "/private/testCase/getTags", method = { GET })
	public ResponseEntity<?> getTags(
			@RequestParam("clientProjectId") int clientProjectId)
			throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Set<String>>(
				testCaseService.getProjectSpecificTagList(clientProjectId),
				HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCase/getTestCaseListByIds", method = {
			GET })
	public ResponseEntity<?> getTestCaseListByIds(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("applicable") String applicable,
			@RequestParam("testCaseIds") String testCaseIds,
			@RequestParam("searchTxt") String searchTxt,
			@RequestParam("sortByColumn") String sortByColumn,
			@RequestParam("ascOrDesc") String ascOrDesc,
			@RequestParam("limit") int limit,
			@RequestParam("pageNumber") int pageNumber,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("isDeleted") String isDeleted) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseClientBean>>(
				testCaseService.getTestCaseList(clientProjectId, 0, null, null,
						applicable, testCaseIds, searchTxt, sortByColumn,
						ascOrDesc, limit, pageNumber, startDate, endDate,
						isDeleted, false),
				HttpStatus.OK);

		// testCaseService.getTestCaseList(clientProjectId, applicable,
		// testCaseIds, searchTxt, sortByColumn, ascOrDesc, limit,
		// pageNumber)
	}

	@Override
	@RequestMapping(value = "/private/testCase/getTestCasesCount", method = {
			GET })
	public ResponseEntity<?> getTestCasesCount(
			@RequestParam("clientProjectId") int clientProjectId,
			@RequestParam("applicable") String applicable,
			@RequestParam("searchTxt") String searchTxt) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
			authorizeUser.authorizeUserForProjectId(clientProjectId);
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<Integer>(testCaseService.getTestCasesCount(
				clientProjectId, applicable, searchTxt), HttpStatus.OK);
	}

	@Override
	@RequestMapping(value = "/private/testCase/getTestCaseVersion", method = {
			GET })
	public ResponseEntity<?> getTestCaseVersion(
			@RequestParam("testCaseId") int testCaseId) throws APIExceptions {
		try {
			authorizeUser.authorizeUserForTokenString();
		} catch (APIExceptions e) {
			log.info(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.UNAUTHORIZED);
		}
		updateDataSource();
		return new ResponseEntity<List<TestCaseVersion>>(
				testCaseService.getTestCaseVersion(testCaseId), HttpStatus.OK);
	}

	private void updateDataSource() throws APIExceptions {
		ClientDatabaseContextHolder.set(applicationCommonUtil.getDefaultOrg());
	}
}
