package com.fw.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.csvreader.CsvReader;
import com.fw.bean.ExecutionDataBean;
import com.fw.exceptions.APIExceptions;
import com.fw.exceptions.EmptyDataException;
import com.fw.pintailer.constants.PintailerConstants;

public class GenerateExecutionReports {

	private static Logger log = Logger
			.getLogger(GenerateExecutionReports.class);

	public static ArrayList<ExecutionDataBean> readXML(MultipartFile uploadfile)
			throws APIExceptions {
		ArrayList<ExecutionDataBean> results = new ArrayList<ExecutionDataBean>();
		DateFormat dateFormat = new SimpleDateFormat(
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE_FORMAT);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(uploadfile.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("test-method");
			Element e = (Element) (doc.getElementsByTagName("suite").item(0));

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String isConfig = eElement.getAttribute("is-config");
					if (null != isConfig && isConfig.equalsIgnoreCase("true")) {
						continue;
					}
					ExecutionDataBean executionDataBean = new ExecutionDataBean();
					String description = eElement.getAttribute("description");

					if (description.indexOf("::") == -1) {
						log.error("Description ["
								+ description
								+ "] does not contain the required information. "
								+ "This row will be ignored and rest of the "
								+ "process will be continue.");
						continue;
					}

					String featureName = description.substring(0,
							description.indexOf("::")).trim();
					String scenarioName = featureName.substring(
							featureName.indexOf("_") + 1, featureName.length());

					executionDataBean.setFeatureName(featureName);
					executionDataBean.setScenarioName(scenarioName);
					executionDataBean.setStepDefinition(eElement
							.getAttribute("name"));
					executionDataBean.setStepDescription(description);
					executionDataBean
							.setStatus(eElement.getAttribute("status"));
					String durationValue = eElement.getAttribute("duration-ms");

					// Saving millisec duration in float (Seconds)
					float duration = 0.0f;
					if (!(null == durationValue || durationValue.trim().equals(
							""))) {
						duration = (Integer.parseInt(durationValue.trim()) / 1000);
					}
					executionDataBean.setDuration(duration);

					String dateString = e.getAttribute("started-at")
							.replace("T", " ").replace("Z", "");
					Date date = dateFormat.parse(dateString);
					executionDataBean
							.setStartTime(new Timestamp(date.getTime()));

					executionDataBean.setLineNumber(0);

					results.add(executionDataBean);
				}
			}
		} catch (IOException e) {
			String message = "Error occurred while reading the file : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (java.text.ParseException e) {
			String message = "The file cannot be read due to issue in its data : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (ParserConfigurationException e) {
			String message = "The file cannot be read due to issue : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (SAXException e) {
			String message = "The file cannot be read/parse due to issue in its data : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
		return results;
	}

	public static ArrayList<ExecutionDataBean> readXMLNew(MultipartFile uploadfile)
			throws APIExceptions {
		ArrayList<ExecutionDataBean> results = new ArrayList<ExecutionDataBean>();
		DateFormat dateFormat = new SimpleDateFormat(
				PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE_FORMAT);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(uploadfile.getInputStream());
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("test-method");
			Element e = (Element) (doc.getElementsByTagName("suite").item(0));

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String isConfig = eElement.getAttribute("is-config");
					if (isConfig.equalsIgnoreCase("true")) {
						continue;
					}
					ExecutionDataBean executionDataBean = new ExecutionDataBean();

					String featureName = "";
					String scenarioName = "";

					executionDataBean.setFeatureName(featureName);
					executionDataBean.setScenarioName(scenarioName);
					String description = eElement.getAttribute("description");
					if(!description.equals("")){
						executionDataBean.setScenarioName(description);
					}
					executionDataBean.setStepDefinition(eElement
							.getAttribute("name"));
					executionDataBean.setStepDescription("");
					executionDataBean
							.setStatus(eElement.getAttribute("status"));
					String durationValue = eElement.getAttribute("duration-ms");

					// Saving millisecond duration in float (Seconds)
					float duration = 0.0f;
					if (!durationValue.trim().equals(
							"")) {
						duration = ((float) Integer.parseInt(durationValue.trim()) / 1000);
					}
					executionDataBean.setDuration(duration);

					String dateString = e.getAttribute("started-at")
							.replace("T", " ").replace("Z", "");
					Date date = dateFormat.parse(dateString);
					executionDataBean
							.setStartTime(new Timestamp(date.getTime()));

					executionDataBean.setLineNumber(0);

					results.add(executionDataBean);
				}
			}
		} catch (IOException e) {
			String message = "Error occurred while reading the file : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (java.text.ParseException e) {
			String message = "The file cannot be read due to issue in its data : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (ParserConfigurationException e) {
			String message = "The file cannot be read due to issue : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (SAXException e) {
			String message = "The file cannot be read/parse due to issue in its data : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		}
		return results;
	}

	public static ArrayList<ExecutionDataBean> readJSON(MultipartFile uploadfile)
			throws APIExceptions {
		ArrayList<ExecutionDataBean> results = new ArrayList<ExecutionDataBean>();
		JSONParser parser = new JSONParser();
		JSONArray cucumberJSON;
		File convFile = null;

		try {
			convFile = new File(uploadfile.getOriginalFilename());
			convFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(uploadfile.getBytes());
			fos.close();

			cucumberJSON = (JSONArray) parser.parse(new FileReader(convFile));
			for (Object o : cucumberJSON) {
				JSONObject featureJSON = (JSONObject) o;
				String uri = featureJSON.get("uri").toString();
				String featureName = uri.substring(uri.lastIndexOf("/") + 1)
						.trim();
				JSONArray elements = (JSONArray) featureJSON.get("elements");
				for (Object elementObj : elements) {
					JSONObject element = (JSONObject) elementObj;
					String scenarioName = element.get("name").toString();
					/*
					 * Due to code formatting, if scenario name is shifted to
					 * next line, name will be empty and description will
					 * contain the name/text of the scenario. Updated on 7th
					 * dec 2018.
					 */
					scenarioName += "\n"
							+ element.get("description").toString()
									.replaceAll("\r", "");
					JSONArray steps = (JSONArray) (element.get("steps"));
					JSONArray comments = null;
					String previousComment = null;
					for (Object stepObj : steps) {
						JSONObject step = (JSONObject) stepObj;
						String stepDefinition = step.get("name").toString();
						int lineNumber = Integer.parseInt(step.get("line")
								.toString());
						if (null != step.get("rows")) {
							JSONArray stepDataRows = ((JSONArray) step
									.get("rows"));
							String temp = "";
							for (Object stepDataRowObj : stepDataRows) {
								JSONObject stepDataRow = (JSONObject) stepDataRowObj;
								JSONArray stepDataCells = ((JSONArray) stepDataRow
										.get("cells"));
								for (Object stepDataCellObj : stepDataCells) {
									String stepDataCell = (String) stepDataCellObj;
									temp += "|" + stepDataCell;
								}
								temp += "|" + "\n";
							}
							stepDefinition = stepDefinition.concat("\n")
									.concat(temp.substring(0,
											temp.lastIndexOf("\n")));
						}
						ExecutionDataBean executionDataBean = new ExecutionDataBean();
						executionDataBean.setFeatureName(featureName);
						executionDataBean.setScenarioName(scenarioName);
						executionDataBean.setStepKeyword(step.get("keyword")
								.toString().trim());
						executionDataBean.setStepDefinition(stepDefinition);
						executionDataBean.setLineNumber(lineNumber);
						// temp adding start time as information not provided in
						// json
						executionDataBean.setStartTime(new Timestamp(System
								.currentTimeMillis()));

						JSONObject result = (JSONObject) step.get("result");
						executionDataBean.setStatus(result.get("status")
								.toString());

						// If comments are available, for cases where automatic
						// mapping is done
						if (null != step.get("comments")) {
							comments = (JSONArray) step.get("comments");
						}
						boolean isTestStepCommentInfoAvailable = false;
						if (null != comments) {
							for (Object commentObj : comments) {
								JSONObject comment = (JSONObject) commentObj;
								String temp = comment.get("value").toString();

								// Removing # from the beginning of the line to
								// fetch the text which is actual test step
								// comment
								while (temp
										.startsWith(PintailerConstants.IMPORT_AUTOMATED_FILE_LINE_COMMENT_SYMBOL)) {
									temp = temp.substring(1);
									temp = (temp.replace("\r", "").replace(
											"\n", "").replace("\t", "")).trim();
								}

								if (!temp.contains("~")
										&& temp.startsWith(PintailerConstants.AUTOMATIC_TEST_CASE_FROM_FEATURE_FILE_IDENTIFIER)) {
									executionDataBean.setStepComment(temp);
									isTestStepCommentInfoAvailable = true;
									previousComment = temp;
								}
							}
							if (!isTestStepCommentInfoAvailable
									&& null != previousComment) {
								executionDataBean
										.setStepComment(previousComment);
							}
						}

						// Converting nanoseconds duration value into float
						// seconds
						Object durationValue = result.get("duration");
						float duration = 0.0f;
						if (!(null == durationValue || durationValue.toString()
								.trim().equals(""))) {
							duration = (Long.parseLong(durationValue.toString()
									.trim()) / 1000000000);
						}
						executionDataBean.setDuration(duration);

						results.add(executionDataBean);
					}
				}
			}
		} catch (IOException e) {
			String message = "Error occured while reading the file : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} catch (ParseException e) {
			String message = "The file cannot be read due to issue in its data : "
					+ e.getMessage();
			log.error(message);
			throw new APIExceptions(message);
		} finally {
			if (null != convFile) {
				convFile.delete();
			}
		}
		return results;
	}

	public static ArrayList<ExecutionDataBean> processExecutionCSVFile(
			MultipartFile uploadfile) throws APIExceptions,
			NullPointerException, EmptyDataException, IOException {
		ArrayList<ExecutionDataBean> list = new ArrayList<>();
		// For local file processing
		log.info("Start process manual execution CSV File");
		try {
			CsvReader csvReader = new CsvReader(uploadfile.getInputStream(),
					Charset.defaultCharset());
			csvReader.readHeaders();

			List<String> headersList = Arrays
					.asList(PintailerConstants.IMPORT_EXECUTION_CSV_HEADERS);
			validateCSVExecutionFile(csvReader.getHeaders(), headersList);

			DateFormat dateFormat = new SimpleDateFormat(
					PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE_FORMAT);

			while (csvReader.readRecord()) {
				ExecutionDataBean bean = new ExecutionDataBean();
				bean.setTestCaseId(Integer
						.parseInt(csvReader
								.get(PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_CASE_ID)
								.trim()));

				Date date = dateFormat
						.parse(csvReader
								.get(PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_EXECUTION_DATE)
								.trim());
				bean.setStartTime(new Timestamp(date.getTime()));

				bean.setStatus(csvReader
						.get(PintailerConstants.IMPORT_CSV_COLUMN_TEST_RESULTS));
				bean.setRemarks(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_REMARKS).trim());
				bean.setLinkedDefects(csvReader.get(
						PintailerConstants.IMPORT_CSV_COLUMN_LINKED_DEFECTS)
						.trim());

				String durationValue = csvReader
						.get(PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_DURATION_IN_SECONDS);
				float duration = 0.0f;
				if (!(null == durationValue || durationValue.trim().equals(""))) {
					duration = Float.parseFloat(durationValue.trim());
				}
				bean.setDuration(duration);

				// in case user import the CSV file for automated test cases
				String[] stepDefinitions = csvReader
						.get(PintailerConstants.IMPORT_EXECUTION_CSV_COLUMN_TEST_STEP_DEFINITION)
						.trim().split(",");
				List<String> mappedStepdefinitionsList = new ArrayList<String>();
				if (stepDefinitions.length == 0
						|| (stepDefinitions.length == 1 && stepDefinitions[0]
								.trim().equals(""))) {
					mappedStepdefinitionsList = null;
				} else {
					for (String stepDefinition : stepDefinitions) {
						stepDefinition = stepDefinition.trim();
						// removing keyword from step definition if exists
						if (stepDefinition.equals("")) {
							continue;
						} else if (!stepDefinition.equals("")
								&& (stepDefinition.toLowerCase().startsWith(
										"given")
										|| stepDefinition.toLowerCase()
												.startsWith("when")
										|| stepDefinition.toLowerCase()
												.startsWith("then")
										|| stepDefinition.toLowerCase()
												.startsWith("and") || stepDefinition
										.toLowerCase().startsWith("but"))) {
							mappedStepdefinitionsList.add(stepDefinition
									.substring(stepDefinition.indexOf(" "))
									.trim());
						} else {
							mappedStepdefinitionsList
									.add(stepDefinition.trim());
						}
					}
				}
				bean.setMappedTestStepDefinitions(mappedStepdefinitionsList);

				bean.setLineNumber(0);

				list.add(bean);
			}
		} catch (java.text.ParseException e) {
			String message = "Error reading CSV execution test case file while importing it. "
					+ "Probably the date id not in the desired format."
					+ e.getMessage();
			log.info(message);
			throw new APIExceptions(message);
		} catch (Exception e) {
			String message = "Error reading CSV execution test case file while importing it. "
					+ e.getMessage();
			log.info(message);
			throw new APIExceptions(message);
		}
		return list;

	}

	private static boolean validateCSVExecutionFile(
			String[] actualHeadersInFile, List<String> headersList)
			throws APIExceptions {
		String nonExistColumnHeaders = null;
		boolean firstTime = true;
		for (int i = 0; i < actualHeadersInFile.length; i++) {
			if (actualHeadersInFile[i].trim().equals("")) {
				continue;
			}
			if (!headersList.contains(actualHeadersInFile[i])) {
				if (firstTime) {
					nonExistColumnHeaders = actualHeadersInFile[i];
					firstTime = false;
				} else {
					nonExistColumnHeaders += "," + actualHeadersInFile[i];
				}
			}
		}

		if (!(null == nonExistColumnHeaders
				|| nonExistColumnHeaders.equalsIgnoreCase("") || nonExistColumnHeaders
					.equalsIgnoreCase("null"))) {
			throw new APIExceptions("The imported execution file headers ["
					+ nonExistColumnHeaders
					+ "] are not valid. Please update the file and retry. "
					+ "Expected columns are [" + headersList.toString() + "]");
		}
		return true;
	}
}
