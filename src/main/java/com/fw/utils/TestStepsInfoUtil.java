package com.fw.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.fw.domain.TestCase;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;

public class TestStepsInfoUtil {

	private static Logger log = Logger.getLogger(TestStepsInfoUtil.class);

	public Map<String, Map<String, ArrayList<String>>> getTestStepFeature(
			int clientProjectId, MultipartFile uploadfile)
			throws APIExceptions {
		try {
			TestStepsInfoUtil testStepsInfoUtil = new TestStepsInfoUtil();
			return testStepsInfoUtil.readFile(clientProjectId, uploadfile);
		} catch (Exception e) {
			String message = "Error occured while reading the feature : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
	}

	private Map<String, Map<String, ArrayList<String>>> readFile(
			int clientProjectId, MultipartFile uploadfile) throws Exception {
		// This will store the current line
		String readLine = null;
		String scenarioTag = null;

		// contains all the test steps in a feature. It includes the duplicate
		// steps as well to replicate the exact scenario structure on the UI
		ArrayList<String> testStepsWithSequence = new ArrayList<String>();

		// structure : Map<UniqueNameOfScenarioInFeature,
		// ListOfOrderedTestStepsInTheScenario>
		Map<String, ArrayList<String>> scenarioSteps = new LinkedHashMap<String, ArrayList<String>>();

		int featureLineCount = 0;
		String featureDescription = null;
		boolean isReadingFeatureTextFirstTime = true;

		// For background
		String backgroundDescription = null;
		boolean isReadingBackgroundTextFirstTime = true;

		int stepLineNumber = 0;

		String scenarioName = null;
		String stepKeyword = null;
		String stepDefinition = null;
		String updatedStepDefinition = null;

		boolean isReadingScenarioTextFirstTime = true;
		String scenarioDescription = null;
		boolean isFeature = false;
		boolean isBackgroundScenario = false;
		boolean isScenarioOutline = false;
		boolean isExample = false;
		ArrayList<String> stepSequence = new ArrayList<String>();
		ArrayList<String> steps = new ArrayList<String>();
		ArrayList<String> example = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadfile.getInputStream()))) {
			while ((readLine = br.readLine()) != null) {
				featureLineCount++;
				readLine = (readLine.replace("\r", "").replace("\n", "")
						.replace("\t", "")).trim();

				/*
				 * All the lines with ~ character are considered as ignored
				 * lines and will not be processed in system
				 */
				if (readLine.contains("~")) {
					continue;
				}

				/*
				 * All the comments started with # are converted into normal
				 * text in the feature to avoid loosing any step as it is
				 * commented (derived from commissioning tool)
				 */
				while (readLine.startsWith(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
					readLine = readLine.substring(1);
					readLine = (readLine.replace("\r", "").replace("\n", "")
							.replace("\t", "")).trim();
				}

				if (readLine.startsWith("TC")) {
					continue;
				}

				/* empty lines are ignored in the feature */
				if (readLine.equals("")) {
					continue;
				}

				/*
				 * Read the feature description of the feature file. Added on
				 * 16th Nov, 2018 to give user functionality to edit and
				 * download the feature from the tool
				 */
				if (readLine.startsWith("Feature:")) {
					if (isReadingFeatureTextFirstTime) {
						featureDescription = readLine
								.substring(readLine.indexOf(":") + 1).trim();
						isReadingFeatureTextFirstTime = false;
						isFeature = true;
					}
					continue;
				} else if (null != featureDescription
						&& !(readLine.startsWith("@")
								|| readLine.startsWith("Scenario:")
								|| readLine.startsWith("Scenario Outline:")
								|| readLine.startsWith("Background:"))) {
					/*
					 * If feature description is spread in more than one line in
					 * the feature file, then we have to add all the text
					 */
					featureDescription += " " + readLine
							.substring(readLine.indexOf(":") + 1).trim();

					continue;
				} else if (null != featureDescription) {
					/*
					 * as soon as we reach to other tag or scenario, it means
					 * the ending of the feature description. So adding it in
					 * object and continuing the loop.
					 */
					scenarioSteps.put(
							getTestScenarioString(featureDescription,
									scenarioTag, isFeature,
									isBackgroundScenario, isScenarioOutline),
							null);
					isFeature = false;
					scenarioTag = null;
					featureDescription = null;
				}

				/*
				 * Read the background scenario description in the feature file.
				 * Added on 28th Nov, 2018 to give user functionality to edit
				 * and download the feature from the tool
				 */
				if (readLine.startsWith("Background:")) {
					if (isReadingBackgroundTextFirstTime) {
						backgroundDescription = readLine
								.substring(readLine.indexOf(":") + 1).trim();
						isReadingBackgroundTextFirstTime = false;
						isBackgroundScenario = true;
					}
					continue;
				} else if (null != backgroundDescription
						&& !(readLine.startsWith("@")
								|| readLine.startsWith("Scenario:")
								|| readLine.startsWith("Scenario Outline:")
								|| isStep(readLine))) {
					/*
					 * If background description is spread in more than one line
					 * in the feature file, then we have to add all the text
					 */
					backgroundDescription += " " + readLine;

					continue;
				} else if (null != backgroundDescription) {
					/*
					 * as soon as we reach to other tag or scenario, it means
					 * the ending of the background description. So adding it in
					 * object and continuing the loop. Also, tag is not allowed
					 * for background scenario. So adding empty value
					 */
					scenarioTag = "";
					scenarioName = getTestScenarioString(backgroundDescription,
							scenarioTag, isFeature, isBackgroundScenario,
							isScenarioOutline);
					isBackgroundScenario = false;
					scenarioTag = null;
					backgroundDescription = null;
				}

				/*
				 * Reading the data rows in the feature which will added to the
				 * parent step. Steps will be store in the DB with all the
				 * available data rows and hashcode will be generated for the
				 * entire string
				 */
				if (!isExample && readLine.startsWith("|")) {
					/*
					 * in case the current scenario is a scenario outline, there
					 * are possibilities of steps with data lines. below flag
					 * and if true code is added to check that
					 */
					if (!isScenarioOutline) {
						if (null == updatedStepDefinition) {
							updatedStepDefinition = stepDefinition.concat("\n")
									.concat(readLine);
						} else {
							updatedStepDefinition += "".concat("\n")
									.concat(readLine);
						}
						/* Adding the data line with the parent step */
						testStepsWithSequence.set(
								testStepsWithSequence.size() - 1,
								getTestStepWithSequenceString(clientProjectId,
										stepLineNumber, updatedStepDefinition,
										stepKeyword));

						scenarioSteps.put(scenarioName, testStepsWithSequence);
					} else {
						steps.set(steps.size() - 1, steps.get(steps.size() - 1)
								.concat("\n").concat(readLine));
					}
					continue;
				}

				/*
				 * both scenario are scenario outline are considered as
				 * beginning of new scenario, hence the test step list is re
				 * initialized
				 */
				if (readLine.startsWith("Scenario:")
						|| readLine.startsWith("Scenario Outline:")) {

					if (isReadingScenarioTextFirstTime) {
						scenarioDescription = readLine
								.substring(readLine.indexOf(":") + 1).trim();
						isReadingScenarioTextFirstTime = false;
						testStepsWithSequence = new ArrayList<String>();
					}

					if (readLine.startsWith("Scenario Outline:")) {
						isScenarioOutline = true;
					}
					continue;
				} else if (null != scenarioDescription
						&& !(readLine.startsWith("@") || isStep(readLine)
								|| isExample)) {
					/*
					 * If background description is spread in more than one line
					 * in the feature file, then we have to add all the text
					 */
					scenarioDescription += " " + readLine;
					continue;
				} else if (null != scenarioDescription) {

					scenarioName = getTestScenarioString(scenarioDescription,
							scenarioTag, isFeature, isBackgroundScenario,
							isScenarioOutline);
					updatedStepDefinition = null;
					scenarioDescription = null;
					isReadingScenarioTextFirstTime = true;
				}

				if (readLine.startsWith("Examples:")) {
					isExample = true;
					continue;
				}

				if (isScenarioOutline && isStep(readLine)) {
					stepSequence.add("" + featureLineCount);
					steps.add(readLine);
					continue;
				} else if (isExample && readLine.startsWith("|")) {
					readLine = readLine.replaceAll(" ", "");
					example.add(readLine);
					continue;
				}

				if (isScenarioOutline && isExample) {
					scenarioSteps.put(scenarioName, readScenarioOutline(
							clientProjectId, stepSequence, steps, example));
					isScenarioOutline = false;
					isExample = false;
				}

				if (isStep(readLine)) {
					stepLineNumber = featureLineCount;
					/* Hold the starting keyword */
					stepKeyword = readLine.substring(0, readLine.indexOf(" "))
							.trim();

					/* Line without keyword */
					stepDefinition = readLine.substring(readLine.indexOf(" "))
							.trim();

					testStepsWithSequence.add(getTestStepWithSequenceString(
							clientProjectId, stepLineNumber, stepDefinition,
							stepKeyword));
					scenarioSteps.put(scenarioName, testStepsWithSequence);
					updatedStepDefinition = null;
				}
				if (readLine.startsWith("@")) {
					scenarioTag = readLine.trim();
				}
			}
		}

		if (isScenarioOutline && isExample) {
			scenarioSteps.put(scenarioName, readScenarioOutline(clientProjectId,
					stepSequence, steps, example));
			isScenarioOutline = false;
			isExample = false;
		}

		Map<String, Map<String, ArrayList<String>>> scenarioSequence = new LinkedHashMap<String, Map<String, ArrayList<String>>>();
		scenarioSequence.put(uploadfile.getOriginalFilename(), scenarioSteps);

		return scenarioSequence;
	}

	public static boolean isStep(String readLine) {
		if (readLine.startsWith("Given") || readLine.startsWith("When")
				|| readLine.startsWith("Then") || readLine.startsWith("And")
				|| readLine.startsWith("But")) {
			return true;
		}
		return false;
	}

	private ArrayList<String> readScenarioOutline(int clientProjectId,
			ArrayList<String> stepSequence, ArrayList<String> steps,
			ArrayList<String> example) throws APIExceptions {
		boolean isHeader = true;
		String[] headers = null;
		String[] values = null;
		ArrayList<String> testStepsWithSequence = new ArrayList<String>();
		String stepKeyword = null;
		String stepDefinition = null;
		/*
		 * Adding the steps based on the data lines. For each data lines all the
		 * steps will be added. So in case the scenario has 5 steps and 3 data
		 * lines under examples, total 10 lines (5*2) will be added in the
		 * scenario
		 */
		for (String data : example) {
			/*
			 * Removing first and last pipe to get correct headers/values array
			 * after split
			 */
			data = data.substring(1);
			data = data.substring(0, data.lastIndexOf("|"));
			if (isHeader) {
				/* replacing pipe with @@ as split is not working for pipe */
				headers = data.replace("|", "@@").split("@@");
				isHeader = false;
				continue;
			} else {
				values = data.replace("|", "@@").split("@@");
			}

			/* Loop on all the steps to replace the header with value */
			for (int i = 0; i < steps.size(); i++) {
				stepKeyword = steps.get(i)
						.substring(0, steps.get(i).indexOf(" ")).trim();

				/* Line without keyword */
				stepDefinition = steps.get(i)
						.substring(steps.get(i).indexOf(" ")).trim();
				boolean firstTime = true;
				/*
				 * This will store the replaced keyword and its example value in
				 * '<keyword>+Symbol+examples value' format to recreate the
				 * feature file on edit feature functionality
				 */
				String examplesKeyValue = "";
				for (int index = 0; index < headers.length; index++) {
					/*
					 * processing only those step rows having the keyword to be
					 * replaced by value
					 */
					if (stepDefinition
							.contains("<".concat(headers[index]).concat(">"))) {
						stepDefinition = stepDefinition.replaceAll(
								"<".concat(headers[index]).concat(">"),
								values[index]);
						String temp = "".concat("<").concat(headers[index])
								.concat(">")
								.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
								.concat(values[index]);
						if (firstTime) {
							examplesKeyValue = temp;
							firstTime = false;
						} else {
							examplesKeyValue = examplesKeyValue.concat(
									PintailerConstants.GENERIC_SEPARATOR)
									.concat(temp);
						}
					}
				}
				/*
				 * Extra information is added here regarding the keyword and
				 * example value. This info will be used for scenario outlines
				 * only
				 */
				testStepsWithSequence.add(getTestStepWithSequenceString(
						clientProjectId, Integer.parseInt(stepSequence.get(i)),
						stepDefinition, stepKeyword).concat(
								PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
								.concat(examplesKeyValue));
			}
		}
		return testStepsWithSequence;
	}

	public ArrayList<Object> temp(MultipartFile uploadfile,
			Map<String, Map<String, ArrayList<String>>> data,
			int clientProjectId, String currentUserName) throws Exception {
		ArrayList<Object> result = new ArrayList<Object>();
		String featureFileName = data.keySet().iterator().next().toString();
		Map<String, ArrayList<String>> scenarioSteps = data
				.get(featureFileName);
		String readLine = null;
		String manualTestCases = null;
		/*
		 * contains all the test steps in a feature for the given manual test
		 * case as comment.
		 */
		ArrayList<String> automatedTestSteps = new ArrayList<String>();
		/*
		 * structure : Map<UniqueNameOfScenarioInFeature,
		 * ListOfOrderedTestStepsInTheScenario>
		 */
		Map<TestCase, ArrayList<String>> manualVsAutomatedSteps = new LinkedHashMap<TestCase, ArrayList<String>>();

		List<String> moduleNameList = new ArrayList<String>();
		int featureLineNumber = 0;
		boolean firstTimeFlg = true;
		List<String> tags = new ArrayList<String>();
		tags.add("P1");
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadfile.getInputStream()))) {
			while ((readLine = br.readLine()) != null) {
				featureLineNumber++;
				/*
				 * ignore line if it contains ~. No additional condition has to
				 * be check
				 */
				if (readLine.contains("~")) {
					continue;
				} else if (readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").trim().toLowerCase().startsWith("gpup")) {
					continue;
				}

				readLine = (readLine.replace("\r", "").replace("\n", "")
						.replace("\t", "")).trim();

				/*
				 * gives module and sub module name by reading the feature tag
				 */
				if (firstTimeFlg && readLine.startsWith("@")) {
					String[] split = readLine
							.substring(readLine.indexOf("@") + 1).split("_");
					int count = 0;
					for (String name : split) {
						if (count >= PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT) {
							/*
							 * Only 3 level hierarchy is allowed in the modules.
							 * So adding all the below levels in the 3rd level
							 * itself
							 */
							moduleNameList.set(moduleNameList.size() - 1,
									moduleNameList
											.get(moduleNameList.size() - 1)
											.concat("_").concat(name.trim()));
							continue;
						}
						moduleNameList.add(name.trim());
						count++;
					}

					if (count < PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT) {
						for (int i = count; i < PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT; i++) {
							moduleNameList.add("");
						}
					}
					firstTimeFlg = false;
					continue;
				}

				/*
				 * Identify the comments which are started with keyword TC. This
				 * comments will be considered as manual test cases
				 */
				if (readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").trim().startsWith(
								PintailerConstants.AUTOMATIC_TEST_CASE_FROM_FEATURE_FILE_IDENTIFIER)) {
					while (readLine.startsWith(
							PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
						// Instead of replace all methods, this loop is added to
						// remove only initial # and not the # added later in
						// the string
						readLine = readLine.substring(1);
						readLine = (readLine.replace("\r", "").replace("\n", "")
								.replace("\t", "")).trim();
					}

					if (null != manualTestCases && null != automatedTestSteps
							&& !automatedTestSteps.isEmpty()) {
						manualVsAutomatedSteps.put(setManualTestCaseObj("-", "",
								moduleNameList, manualTestCases, "-", tags, "-",
								"-", true, null, null, null, null, true,
								currentUserName), automatedTestSteps);
					}

					manualTestCases = readLine;
					automatedTestSteps = new ArrayList<String>();
					continue;
				}

				if (null == manualTestCases) {
					continue;
				}

//				return (stepLineNumber + "")
//						.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
//						.concat(GenerateUniqueHash.getTestStepHash(clientProjectId,
//								stepDefWithoutKeyword))
//						.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
//						.concat(stepKeyword)
//						.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
//						.concat(stepDefWithoutKeyword);

				for (Entry<String, ArrayList<String>> valuesEntry : scenarioSteps
						.entrySet()) {
					String scenarioName = valuesEntry.getKey();
					ArrayList<String> scenarioStepList = valuesEntry.getValue();
					if (null == scenarioStepList) {
						continue;
					}
					boolean isFound = false;
					int index = 0;
					for (String step : scenarioStepList) {
						final String[] stepInfoArr = step.split(
								PintailerConstants.READ_FILE_OBJECT_SEPARATOR);
						int testStepSequence = Integer.parseInt(stepInfoArr[0]);
						if (testStepSequence == featureLineNumber) {
							/*
							 * Adding values in the list of automated test steps
							 * as <HashCode of steps><separator><step with
							 * keyword in beginning><separator><featuer file
							 * name><separator><scenario name><separator> where
							 * separator is a constant in PintailerConstants
							 */
							automatedTestSteps
									.add(getTestStepString(clientProjectId,
											stepInfoArr[3], stepInfoArr[2],
											featureFileName, scenarioName));
							isFound = true;
							break;
						}
						index++;
					}

					// if step is the last step of the scenario
					if (isFound && index == (scenarioStepList.size() - 1)) {
						manualVsAutomatedSteps.put(setManualTestCaseObj("-", "",
								moduleNameList, manualTestCases, "-", tags, "-",
								"-", true, null, null, null, null, true,
								currentUserName), automatedTestSteps);

						manualTestCases = null;
						automatedTestSteps = null;
						break;
					}

					// End of scenario means no more step will add to the test
					// case
				}
			}
			if (null != manualTestCases && null != automatedTestSteps
					&& !automatedTestSteps.isEmpty()) {
				manualVsAutomatedSteps.put(
						setManualTestCaseObj("-", "", moduleNameList,
								manualTestCases, "-", tags, "-", "-", true,
								null, null, null, null, true, currentUserName),
						automatedTestSteps);
			}
			result.add(manualVsAutomatedSteps);
		} catch (Exception e) {
			String message = "Error: Issue while reading the feature file for automatic mapping : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} finally {
			automatedTestSteps = null;
			manualVsAutomatedSteps = null;
			moduleNameList = null;
			tags = null;
		}
		return result;
	}

	/**
	 * For commissioning tool only - Line with ~ will be ignored completely - in
	 * comments : - If statement starts with Gpup, ignore the line - If
	 * statement starts with TC, it will consider as manual test cases (gpup
	 * keyword does not matters) - Lines with given, when, then keyword will be
	 * inserted in database but will be disabled and will not be displayed on ui
	 * untill its enable
	 */
	public ArrayList<Object> readAndMapFeatureFile(MultipartFile uploadfile,
			int clientProjectId, String currentUserName) throws Exception {
		String featureName = uploadfile.getOriginalFilename();
		String scenarioName = null;
		/* This will store the current line */
		String readLine = null;

		ArrayList<Object> result = new ArrayList<Object>();

		/*
		 * contains all the test steps in a feature for the given manual test
		 * case as comment.
		 */
		ArrayList<String> automatedTestSteps = new ArrayList<String>();

		/*
		 * structure : Map<UniqueNameOfScenarioInFeature,
		 * ListOfOrderedTestStepsInTheScenario>
		 */
		Map<TestCase, ArrayList<String>> manualVsAutomatedSteps = null;

		/*
		 * boolean flag to read the module and submodule name given in the
		 * feature tag
		 */
		boolean firstTimeFlg = true;

		String manualTestCases = null;

		List<String> moduleNameList = new ArrayList<String>();

		/* For commissioning tool, the default tag is set to P1 */
		List<String> tags = new ArrayList<String>();
		tags.add("P1");
		String scenarioDescription = null;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(uploadfile.getInputStream()))) {
			while ((readLine = br.readLine()) != null) {
				/*
				 * ignore line if it contains ~. No additional condition has to
				 * be check
				 */
				if (readLine.contains("~")) {
					continue;
				} else if (readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").trim().toLowerCase().startsWith("gpup")) {
					continue;
				}

				readLine = (readLine.replace("\r", "").replace("\n", "")
						.replace("\t", "")).trim();

				// if (readLine.startsWith("Scenario:")) {
				// manualTestCases = null;
				// scenarioName = readLine
				// .substring(readLine.indexOf(":") + 1).trim();
				// continue;
				// }

				if (readLine.startsWith("Scenario:")
						|| readLine.startsWith("Scenario Outline:")) {
					scenarioDescription = readLine
							.substring(readLine.indexOf(":") + 1).trim();
					if (null == manualVsAutomatedSteps) {
						manualVsAutomatedSteps = new LinkedHashMap<TestCase, ArrayList<String>>();
					}
					// Adding info before next test case is getting read
					if (null != manualTestCases
							&& !automatedTestSteps.isEmpty()) {
						manualVsAutomatedSteps.put(setManualTestCaseObj("-", "",
								moduleNameList, manualTestCases, "-", tags, "-",
								"-", true, null, null, null, null, true,
								currentUserName), automatedTestSteps);
					}
					manualTestCases = null;
					continue;
				} else if (null != scenarioDescription && !(readLine
						.startsWith("@") || isStep(readLine)
						|| readLine.startsWith(
								PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL))) {
					/*
					 * If background description is spread in more than one line
					 * in the feature file, then we have to add all the text
					 */
					scenarioDescription += " " + readLine;
					continue;
				} else if (null != scenarioDescription) {
					scenarioName = scenarioDescription.trim();
					scenarioDescription = null;
				}

				/*
				 * gives module and sub module name by reading the feature tag
				 */
				if (firstTimeFlg && readLine.startsWith("@")) {

					String[] split = readLine
							.substring(readLine.indexOf("@") + 1).split("_");

					int count = 0;
					for (String name : split) {
						if (count >= PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT) {
							/*
							 * Only 3 level hierarchy is allowed in the modules.
							 * So adding all the below levels in the 3rd level
							 * itself
							 */
							moduleNameList.set(moduleNameList.size() - 1,
									moduleNameList
											.get(moduleNameList.size() - 1)
											.concat("_").concat(name.trim()));
							continue;
						}
						moduleNameList.add(name.trim());
						count++;
					}

					if (count < PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT) {
						for (int i = count; i < PintailerConstants.MODULE_HIERARCHY_LEVEL_COUNT; i++) {
							moduleNameList.add("");
						}
					}

					firstTimeFlg = false;
					continue;
				}

//				if (readLine.startsWith(
//						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
//					String tmp = readLine.replaceAll(
//							PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
//							"").trim();
//					/*
//					 * Ignore a comment line if it starts with gpup or does not
//					 * starts with given, when, then, and, but keyword and TC
//					 * and |
//					 */
//					if (tmp.toLowerCase().startsWith("gpup") || !(isStep(tmp)
//							|| tmp.startsWith(
//									PintailerConstants.AUTOMATIC_TEST_CASE_FROM_FEATURE_FILE_IDENTIFIER)
//							|| tmp.startsWith("|"))) {
//						continue;
//					}
//				}

				/*
				 * Identify the comments which are started with keyword TC. This
				 * comments will be considered as manual test cases
				 */
				if (readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").trim()

						.startsWith(
								PintailerConstants.AUTOMATIC_TEST_CASE_FROM_FEATURE_FILE_IDENTIFIER)) {
					while (readLine.startsWith(
							PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
						// Instead of replace all methods, this loop is added to
						// remove only initial # and not the # added later in
						// the string
						readLine = readLine.substring(1);
						readLine = (readLine.replace("\r", "").replace("\n", "")
								.replace("\t", "")).trim();
					}

					manualTestCases = readLine;
					automatedTestSteps = new ArrayList<String>();
					continue;
				}

				if (manualTestCases == null) {
					continue;
				}

				if (!readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").replace("\r", "").replace("\n", "")
						.replace("\t", "").trim().startsWith("|")
						&& (isStep(readLine) || readLine.startsWith(
								PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL))) {
					/* Hold the starting keyword */
					String keyWord = "";

					if (readLine.startsWith(
							PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
						/*
						 * all the starting hash symbols used as a comment are
						 * removed to display on UI. After that all the white
						 * spaces are removed so that the readLine starts with
						 * any of the given, when, then etc keyword. Keyword is
						 * not included in hash code generation
						 */
						while (readLine.startsWith(
								PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
							// keyWord = keyWord
							// .concat(PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL);
							readLine = readLine.replace(
									PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
									"").replace("\r", "").replace("\n", "")
									.replace("\t", "").trim();
						}
						/*
						 * if scenario contain some comments in the middle of
						 * the steps than ignore.
						 */
						if (!isStep(readLine)) {
							continue;
						}
						/*
						 * Adding given, when, then, and or but after removing #
						 * in the keyword to display on UI. Previously # was
						 * added with the keyword itself
						 */
						keyWord = readLine.substring(0, readLine.indexOf(" "))
								.trim();
					} else {
						keyWord = readLine.substring(0, readLine.indexOf(" "))
								.trim();
					}
					/*
					 * All the white spaces are removed after removing the
					 * initial given, when, then etc. keyword
					 */
					readLine = (readLine.trim().substring(readLine.indexOf(" "))
							.replace("\r", "").replace("\n", "")
							.replace("\t", "")).trim();

					/*
					 * Adding values in the list of automated test steps as
					 * <HashCode of steps><separator><step with keyword in
					 * beginning><separator><featuer file
					 * name><separator><scenario name><separator> where
					 * separator is a constant in PintailerConstants
					 */
					automatedTestSteps.add(getTestStepString(clientProjectId,
							readLine, keyWord, featureName, scenarioName));

//					if (null == manualVsAutomatedSteps) {
//						manualVsAutomatedSteps = new LinkedHashMap<TestCase, ArrayList<String>>();
//					}
//					manualVsAutomatedSteps.put(setManualTestCaseObj("-", "",
//							moduleNameList, manualTestCases, "-", tags, "-",
//							"-", true, null, null, null, null, true,
//							currentUserName), automatedTestSteps);

				} else if (readLine.replaceAll(
						PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL,
						"").replace("\r", "").replace("\n", "")
						.replace("\t", "").trim().startsWith("|")) {
					/*
					 * getting the last step in which the data line will be
					 * added. Hashcode is generated for entire steps including
					 * data lines
					 */
					String temp = automatedTestSteps
							.get(automatedTestSteps.size() - 1)
							.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[2]
									.trim();

					/* Retrieving the keyword of the line */
					// String keyword = temp.substring(0, temp.indexOf(" "))
					// .trim();
					String keyword = automatedTestSteps
							.get(automatedTestSteps.size() - 1)
							.split(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[1]
									.trim();

					/*
					 * adding the data line to the previous line. Data line if
					 * comment, getting added after removing initial # and than
					 * extra white spaces
					 */
					temp = temp.trim().concat("\n")
							.concat(readLine.substring(readLine.indexOf("|")));

					/*
					 * step GenerateUniqueHash.hash code and text updated in the
					 * original map
					 */
					automatedTestSteps.set(automatedTestSteps.size() - 1,
							getTestStepString(clientProjectId, temp, keyword,
									featureName, scenarioName));
				}
			}
			if (null == manualVsAutomatedSteps) {
				manualVsAutomatedSteps = new LinkedHashMap<TestCase, ArrayList<String>>();
			}
			// Adding last scenario info
			if (null != manualTestCases && !automatedTestSteps.isEmpty()) {
				manualVsAutomatedSteps.put(
						setManualTestCaseObj("-", "", moduleNameList,
								manualTestCases, "-", tags, "-", "-", true,
								null, null, null, null, true, currentUserName),
						automatedTestSteps);
			}
			if (null == manualVsAutomatedSteps
					|| manualVsAutomatedSteps.isEmpty()) {
				return null;
			}

			result.add(manualVsAutomatedSteps);

			Set<TestCase> keySet = manualVsAutomatedSteps.keySet();
			log.debug("Module : " + moduleNameList.get(0));
			log.debug("Sub Module : " + moduleNameList.get(1));
			log.debug("");
			for (TestCase key : keySet) {
				log.debug("Manual Test Case Summary : " + key.getTestSummary());
				log.debug("Automation Test Case Summary : ");
				ArrayList<String> arrayList = manualVsAutomatedSteps.get(key);
				for (String step : arrayList) {
					log.debug(step.split(
							PintailerConstants.READ_FILE_OBJECT_SEPARATOR)[1]);
				}
				log.debug("");
			}

			return result;
		} catch (Exception e) {
			String message = "Error: Issue while reading the feature file for automatic mapping : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} finally {
			automatedTestSteps = null;
			manualVsAutomatedSteps = null;
			moduleNameList = null;
			tags = null;
		}
	}

	/**
	 * This method is used for the portal like automated TestNG projects, where
	 * the user import the data in CSV format with test case method name in
	 * TestNG java file details in the CSV column.
	 * 
	 * These test cases name will be considered as automated test step.
	 * 
	 * @param fileName
	 * 
	 * @throws APIExceptions
	 */
	public ArrayList<Object> convertCSVObjectForAutomationMapping(
			final int clientProjectId, List<TestCase> testCaseBeanList,
			final String currentUserName) throws APIExceptions {
		ArrayList<Object> result = new ArrayList<Object>();

		Map<TestCase, ArrayList<String>> manualVsAutomatedSteps = new LinkedHashMap<TestCase, ArrayList<String>>();

		for (TestCase testCase : testCaseBeanList) {
			if (!testCase.isAutomaticMappingAvailable()) {
				continue;
			}
			ArrayList<String> automatedTestSteps = new ArrayList<String>();

			String temp = testCase.getAutomatedTestCaseNoFromFile();

			if (null == manualVsAutomatedSteps.get(testCase)) {
				if (temp.contains(",")) {
					String[] split = temp.split(",");
					for (String step : split) {
						step = step.trim();
						automatedTestSteps
								.add(getTestStepString(clientProjectId, step,
										"", testCase.getFeatureName(),
										testCase.getScenarioName()));
					}
				} else {
					automatedTestSteps.add(getTestStepString(clientProjectId,
							temp, "", testCase.getFeatureName(),
							testCase.getScenarioName()));
				}
				manualVsAutomatedSteps.put(testCase, automatedTestSteps);
			} else {
				if (temp.contains(",")) {
					String[] split = temp.split(",");
					for (String step : split) {
						manualVsAutomatedSteps.get(testCase)
								.add(getTestStepString(clientProjectId, step,
										"", testCase.getFeatureName(),
										testCase.getScenarioName()));
					}
				} else {
					manualVsAutomatedSteps.get(testCase)
							.add(getTestStepString(clientProjectId, temp, "",
									testCase.getFeatureName(),
									testCase.getScenarioName()));
				}
			}
		}

		result.add(manualVsAutomatedSteps);
		return result;
	}

	private TestCase setManualTestCaseObj(String testCaseNumber,
			String testData, List<String> moduleNameList,
			String manualTestCaseSummary, String preCondition,
			List<String> tags, String executionSteps, String expectedResults,
			boolean automatable, String remarks, String fileName,
			String automatedTestCaseNoFromFile, String manualReason,
			boolean applicable, String username) throws APIExceptions {
		TestCase testCase = new TestCase();
		testCase.setTestCaseNo(testCaseNumber);
		testCase.setTestData(testData);

		testCase.setModuleNameHierarchy(moduleNameList);
		testCase.setTestSummary(manualTestCaseSummary);
		testCase.setPreCondition(preCondition);
		testCase.setTags(tags);
		testCase.setExecutionSteps(executionSteps);
		testCase.setExpectedResult(expectedResults);
		testCase.setAutomatable(automatable);
		testCase.setRemarks(remarks);
		testCase.setFileName(fileName);
		testCase.setAutomatedTestCaseNoFromFile(automatedTestCaseNoFromFile);
		testCase.setManualReason(manualReason);
		testCase.setApplicable(applicable);
		testCase.setCreatedBy(username);
		testCase.setModifiedBy(username);

		return testCase;
	}

	private String getTestStepString(final int clientProjectId,
			final String stepDefWithoutKeyword, final String stepKeyword,
			final String featureName, final String scenarioName)
			throws APIExceptions {
		return GenerateUniqueHash
				.getTestStepHash(clientProjectId, stepDefWithoutKeyword)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(stepKeyword)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(stepDefWithoutKeyword)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(featureName)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(scenarioName);
	}

	private String getTestScenarioString(final String scenarioDescription,
			String scenarioTag, final boolean isFeature,
			final boolean isBackgroundScenario, final boolean isScenarioOutline)
			throws APIExceptions {
		if (null == scenarioTag) {
			scenarioTag = "";
		}
		return scenarioDescription
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(scenarioTag == null ? "" : scenarioTag)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(String.valueOf(isFeature))
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(String.valueOf(isBackgroundScenario))
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(String.valueOf(isScenarioOutline));
	}

	private String getTestStepWithSequenceString(final int clientProjectId,
			int stepLineNumber, final String stepDefWithoutKeyword,
			final String stepKeyword) throws APIExceptions {
		return (stepLineNumber + "")
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(GenerateUniqueHash.getTestStepHash(clientProjectId,
						stepDefWithoutKeyword))
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(stepKeyword)
				.concat(PintailerConstants.READ_FILE_OBJECT_SEPARATOR)
				.concat(stepDefWithoutKeyword);
	}
}
