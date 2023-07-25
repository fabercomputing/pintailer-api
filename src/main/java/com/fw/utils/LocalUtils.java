package com.fw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.fw.pintailer.constants.PintailerConstants;

@Component
public class LocalUtils implements ApplicationListener<ApplicationReadyEvent> {

	private Logger log = Logger.getLogger(LocalUtils.class);

	String[] allLocalFileNames = { "error_english.properties",
			"error_hindi.properties" };
	private static Map<String, Map<String, String>> allPropFiles;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		try {
			readAllLocalFiles();
		} catch (IOException e) {
			String message = "Error occured : " + e.getMessage();
			log.error(message);
		}
		return;
	}

	private Map<String, String> loadLocalFile(String fileName)
			throws IOException

	{
		Map<String, String> propertyMap = new HashMap<String, String>();
		Properties engPropFile = new Properties();

		InputStream stream = LocalUtils.class.getClassLoader()
				.getResourceAsStream(fileName);
		InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
		engPropFile.load(isr);

		Enumeration<?> keys = engPropFile.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			propertyMap.put(key, engPropFile.getProperty(key));
		}
		return propertyMap;

	}

	private Map<String, Map<String, String>> readAllLocalFiles()
			throws IOException {
		allPropFiles = new HashMap<String, Map<String, String>>();

		for (String currentLocalFile : allLocalFileNames) {

			String key = "";
			if (currentLocalFile.equalsIgnoreCase("error_english.properties")) {
				key = PintailerConstants.LOCALE_EN;
			} else if (currentLocalFile
					.equalsIgnoreCase("error_hindi.properties")) {
				key = PintailerConstants.LOCALE_HI;
			} else {
				key = PintailerConstants.LOCALE_EN;
			}
			Map<String, String> localFile = loadLocalFile(currentLocalFile);
			allPropFiles.put(key, localFile);
		}

		return allPropFiles;
	}

	public static String getStringLocale(String locale, String key) {
		String returnString = null;
		switch (locale) {
		case PintailerConstants.LOCALE_EN:
			returnString = allPropFiles.get(PintailerConstants.LOCALE_EN).get(
					key);
			break;
		case PintailerConstants.LOCALE_HI:
			returnString = allPropFiles.get(PintailerConstants.LOCALE_HI).get(
					key);
			break;
		default:
			returnString = allPropFiles.get(PintailerConstants.LOCALE_EN).get(
					key);
		}

		return returnString;
	}

}
