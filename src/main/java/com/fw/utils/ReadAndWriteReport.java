package com.fw.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.csvreader.CsvWriter;

public class ReadAndWriteReport {

	private Logger log = Logger.getLogger(ReadAndWriteReport.class);

	public String writeXLSFile(final String[] header,
			final List<Map<String, String>> data, String filePath,
			String fileNamePrefix, boolean isClientReport) throws IOException {
		if (data.isEmpty()) {
			log.error("No data is provided to create the report.");
			return null;
		}

		// name of excel file
		String excelFileName = fileNamePrefix + System.currentTimeMillis()
				+ ".xls";
		String fileFullPath = filePath + File.separator + excelFileName;

		String sheetName = "Sheet1";// name of sheet

		try (HSSFWorkbook wb = new HSSFWorkbook()) {
			HSSFSheet sheet = wb.createSheet(sheetName);

			// Adding header to the file
			int rowIndex = 1;
			int headerCellIndex = 1;
			HSSFRow headerRow = sheet.createRow(rowIndex);
			// iterating header columns
			HSSFCellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
			headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
			headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
			for (int index = 0; index < header.length; index++) {
				HSSFCell cell = headerRow.createCell(headerCellIndex);
				cell.setCellValue(header[index]);

				// setting header style
				headerCellStyle.setFillForegroundColor(
						IndexedColors.GREY_25_PERCENT.getIndex());
				headerCellStyle
						.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				cell.setCellStyle(headerCellStyle);
				headerCellIndex++;
			}
			rowIndex++;

			// Adding rows to the report
			HSSFCellStyle cellStyle = wb.createCellStyle();
			for (int index = 0; index < data.size(); index++) {
				HSSFRow row = sheet.createRow(rowIndex);
				Map<String, String> rowData = data.get(index);

				int dataCellIndex = 1;

				// iterating data columns
				for (int i = 0; i < header.length; i++) {
					// adding value to cell
					HSSFCell cell = row.createCell(dataCellIndex);

					// if (header[i].equals("Section")
					// || header[i].equals("Status")
					// || header[i].equals("Linked Bugs")) {
					cell.setCellType(CellType.STRING);
					cell.setCellValue(rowData.get(header[i]));
					// } else {
					// cell.setCellType(CellType.NUMERIC);
					// cell.setCellValue(Integer.parseInt(rowData
					// .get(header[i])));
					// }

					// Set cell style
//					cellStyle = wb.createCellStyle();
					if (isClientReport) {
						cellStyle.setBorderBottom(BorderStyle.THIN);
						cellStyle.setBorderLeft(BorderStyle.THIN);
						cellStyle.setBorderRight(BorderStyle.THIN);
						cellStyle.setBorderTop(BorderStyle.THIN);

						if (index == (data.size() - 1)) {
							cellStyle.setBorderBottom(BorderStyle.MEDIUM);
						}
						if (header[i].equals("Section")) {
							cellStyle.setBorderLeft(BorderStyle.MEDIUM);
							cellStyle.setFillForegroundColor(
									IndexedColors.GREY_25_PERCENT.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
						} else if (header[i].equals("Failed")) {
							cellStyle.setFillForegroundColor(
									IndexedColors.RED.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
						} else if (header[i].equals("Pending")) {
							cellStyle.setFillForegroundColor(
									IndexedColors.LIGHT_ORANGE.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
						} else if (header[i].equals("Status")) {
							if (rowData.get(header[i]).equals("Completed")) {
								cellStyle.setFillForegroundColor(
										IndexedColors.GREEN.getIndex());
							} else if (rowData.get(header[i])
									.equals("Pending")) {
								cellStyle.setFillForegroundColor(
										IndexedColors.LIGHT_ORANGE.getIndex());
							} else if (rowData.get(header[i])
									.equals("In Process")) {
								cellStyle.setFillForegroundColor(
										IndexedColors.LIGHT_BLUE.getIndex());
							} else {
								cellStyle.setFillForegroundColor(
										IndexedColors.RED1.getIndex());
							}
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
						}
						if (header[i].equals("Linked Bugs")) {
							cellStyle.setBorderRight(BorderStyle.MEDIUM);
						}
						cell.setCellStyle(cellStyle);
					}
					// Setting width of column
					// if (System.getProperty("os.name").contains("Windows")) {
					// sheet.autoSizeColumn(dataCellIndex);
					// } else {
					// sheet.setColumnWidth(dataCellIndex, 20);
					// }
					int length = 30;
					if (rowData.get(header[i]) == null
							|| rowData.get(header[i]).length() < 2) {
						length = header[i].length();
					} else if (rowData.get(header[i]).length() > 100) {
						length = 40;
					}
					sheet.setColumnWidth(dataCellIndex, 256 * length);

					dataCellIndex++;
				}
				rowIndex++;
			}
			if (isClientReport) {
				sheet.setDisplayGridlines(false);
			}

			File file = new File(fileFullPath);
			FileOutputStream fileOut = new FileOutputStream(file);
			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			return excelFileName;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Some error occured while creating XLS file with error : "
					+ e.getStackTrace());
		}
		return null;
	}

	public String writeXLSXFile(final String[] header,
			final List<Map<String, String>> data, String filePath,
			String fileNamePrefix, boolean isClientReport) throws IOException {
		if (data.isEmpty()) {
			log.error("No data is provided to create the report.");
			return null;
		}

		// name of excel file
		String excelFileName = fileNamePrefix + System.currentTimeMillis()
				+ ".xlsx";
		log.info("excelFileName : " + excelFileName);
		String fileFullPath = filePath + File.separator + excelFileName;
		log.info("fileFullPath : " + fileFullPath);

		String sheetName = "Sheet1";// name of sheet
		try (XSSFWorkbook wb = new XSSFWorkbook()) {
			XSSFSheet sheet = wb.createSheet(sheetName);
			// Adding header to the file
			int rowIndex = 1;
			int headerCellIndex = 1;
			XSSFRow headerRow = sheet.createRow(rowIndex);
			// iterating header columns
			XSSFCellStyle headerCellStyle = wb.createCellStyle();
			headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
			headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
			headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
			for (int index = 0; index < header.length; index++) {
				XSSFCell cell = headerRow.createCell(headerCellIndex);
				cell.setCellValue(header[index]);
				// setting header style
				headerCellStyle.setFillForegroundColor(
						IndexedColors.GREY_25_PERCENT.getIndex());
				headerCellStyle
						.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				cell.setCellStyle(headerCellStyle);
				headerCellIndex++;
			}
			rowIndex++;
			// Adding rows to the report
			for (int index = 0; index < data.size(); index++) {
				XSSFRow row = sheet.createRow(rowIndex);
				Map<String, String> rowData = data.get(index);
				int dataCellIndex = 1;
				// iterating data columns
				for (int i = 0; i < header.length; i++) {
					// adding value to cell
					XSSFCell cell = row.createCell(dataCellIndex);
					// if (header[i].equals("Section")
					// || header[i].equals("Status")
					// || header[i].equals("Linked Bugs")) {
					cell.setCellType(CellType.STRING);
					cell.setCellValue(rowData.get(header[i]));

					// } else {
					// cell.setCellType(CellType.NUMERIC);
					// cell.setCellValue(Integer.parseInt(rowData
					// .get(header[i])));
					// }
					// Set cell style
					if (isClientReport) {
						XSSFCellStyle cellStyle = wb.createCellStyle();
						cellStyle.setBorderBottom(BorderStyle.THIN);
						cellStyle.setBorderLeft(BorderStyle.THIN);
						cellStyle.setBorderRight(BorderStyle.THIN);
						cellStyle.setBorderTop(BorderStyle.THIN);
						if (index == (data.size() - 1)) {
							cellStyle.setBorderBottom(BorderStyle.MEDIUM);
						}

						switch (header[i].toUpperCase()) {
						case "SECTION":
							cellStyle.setBorderLeft(BorderStyle.MEDIUM);
							cellStyle.setFillForegroundColor(
									IndexedColors.GREY_25_PERCENT.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
							break;
						case "FAILED":
							cellStyle.setFillForegroundColor(
									IndexedColors.RED.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
							break;
						case "PENDING":
							cellStyle.setFillForegroundColor(
									IndexedColors.LIGHT_ORANGE.getIndex());
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
							break;
						case "STATUS":
							switch (rowData.get(header[i]).toUpperCase()) {
							case "COMPLETED":
								cellStyle.setFillForegroundColor(
										IndexedColors.GREEN.getIndex());
								break;
							case "PENDING":
								cellStyle.setFillForegroundColor(
										IndexedColors.LIGHT_ORANGE.getIndex());
								break;
							case "IN PROCESS":
								cellStyle.setFillForegroundColor(
										IndexedColors.LIGHT_BLUE.getIndex());
								break;
							default:
								cellStyle.setFillForegroundColor(
										IndexedColors.RED1.getIndex());
								break;
							}
							cellStyle.setFillPattern(
									FillPatternType.SOLID_FOREGROUND);
							break;
						default:
							break;
						}
						if (header[i].equals("Linked Bugs")) {
							cellStyle.setBorderRight(BorderStyle.MEDIUM);
						}
						cell.setCellStyle(cellStyle);
					}

					// Setting width of column
					// if (System.getProperty("os.name").contains("Windows")) {
					// sheet.autoSizeColumn(dataCellIndex);
					// } else {
					int length = 15;
					if (rowData.get(header[i]) == null
							|| rowData.get(header[i]).length() < 2) {
						length = header[i].length();
					} else if (rowData.get(header[i]).length() > 100) {
						length = 40;
					}
					sheet.setColumnWidth(dataCellIndex, 256 * length);

					// }
					dataCellIndex++;
				}
				rowIndex++;
			}
			if (isClientReport) {
				sheet.setDisplayGridlines(false);
			}
			File file = new File(fileFullPath);
			FileOutputStream fileOut = new FileOutputStream(file);
			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			return excelFileName;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Some error occured while creating XLSX file with error : "
					+ e.getLocalizedMessage());
			log.info(e.getMessage());
		}
		return null;
	}

	public boolean writeCSVTemplate(final String[] header,
			final List<Map<String, String>> data, String filePath,
			String fileName) {

		if (data.isEmpty()) {
			log.error(
					"No data is provided to create the manual execution template.");
			return false;
		}

		// name of excel file
		String fileFullPath = filePath + File.separator + fileName;

		// before we open the file check to see if it already exists
		File file = new File(fileFullPath);
		if (file.exists()) {
			file.delete();
		}

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(file, true),
					',');

			// writing header to the file
			for (int i = 0; i < header.length; i++) {
				csvOutput.write(header[i]);
			}
			csvOutput.endRecord();

			// Writing data
			for (int index = 0; index < data.size(); index++) {
				Map<String, String> value = data.get(index);
				for (int i = 0; i < header.length; i++) {
					csvOutput.write(value.get(header[i]));
				}
				csvOutput.endRecord();
			}

			csvOutput.close();
		} catch (Exception e) {
			log.error("Some error occured while creating XLS file with error : "
					+ e.getStackTrace());
			return false;
		}
		return true;
	}
}