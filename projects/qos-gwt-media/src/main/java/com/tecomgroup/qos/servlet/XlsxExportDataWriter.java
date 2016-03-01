/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.servlet;

import java.io.OutputStream;
import java.util.*;

import com.tecomgroup.qos.CommonExportDataWrapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author sviyazov.a
 * 
 */
public abstract class XlsxExportDataWriter<T extends CommonExportDataWrapper> implements AutoCloseable {

	private final static int TITLE_COLUMN_WIDTH = 6;

	protected final static int ROWS_PER_SHEET = 1000000;

	private final OutputStream outputStream;
    protected final T dataWrapper;

    protected CellStyle defaultFormatStyle;

	protected CellStyle headerDefaultFormatStyle;

	protected CellStyle arial10BoldCentreStyle;

	protected CellStyle arial10BoldStyle;

	protected CellStyle arial12BoldCentreStyle;

	protected Workbook workbook;

	protected Sheet sheet;

	protected CellStyle dateCellFormatStyle;

	protected CellStyle headerDateCellFormatStyle;

	protected int currentRowIndex = 0;

	protected int currentSheetIndex = 0;

	private CellStyle arial10Style;

	protected DateTimeFormatter dateFormatter;

	protected String timeZone;

    public XlsxExportDataWriter(final OutputStream outputStream, final String dateFormat,
                                final String locale, final T dataWrapper, final String timeZone) {
		workbook = new SXSSFWorkbook(10000);
		this.outputStream = outputStream;
        this.dataWrapper = dataWrapper;

		initFormats(dateFormat, locale, timeZone);
	}

	@Override
	public void close() throws Exception {
		workbook.write(outputStream);
		outputStream.close();
	}

	/**
	 * Creates common header with title, agents, creation/ending time and
	 * timezone labels. {@link #sheet} should be initialized before calls to
	 * this method.
	 * 
	 * @param title
	 * @param headerCommonInfo
	 *            - linked map with following order of elements: <br>
	 *            agentLabel=agentDisplayNames <br>
	 *            startTimeLabel=startTime <br>
	 *            endTimeLabel=endTime <br>
	 *            timezoneLabel=timezone
	 * 
	 * @throws Exception
	 */
	protected void createCommonHeader(final String title,
			final Map<String, Object> headerCommonInfo) {

		final Row headerRow = sheet.createRow(currentRowIndex);
		final Cell headerCell = headerRow.createCell(0, Cell.CELL_TYPE_STRING);
		headerCell.setCellStyle(arial12BoldCentreStyle);
		headerCell.setCellValue(title);

		sheet.addMergedRegion(new CellRangeAddress(currentRowIndex,
				currentRowIndex + 1, 0, TITLE_COLUMN_WIDTH - 1));
		currentRowIndex += 2;

		for (final Map.Entry<String, Object> headerInfoEntry : headerCommonInfo
				.entrySet()) {
			final Row row = sheet.createRow(currentRowIndex);
			Cell cell = row.createCell(0);

			cell.setCellValue(headerInfoEntry.getKey());
			cell.setCellStyle(arial10BoldStyle);

			cell = row.createCell(1);
			if (headerInfoEntry.getValue() instanceof Date) {
				cell.setCellValue((Date) headerInfoEntry.getValue());
				cell.setCellStyle(headerDateCellFormatStyle);
			} else {
				cell.setCellValue(headerInfoEntry.getValue().toString());
				cell.setCellStyle(headerDefaultFormatStyle);
			}
			currentRowIndex++;
		}
	}

	protected void createNewSheet() {
		sheet = workbook.createSheet(dataWrapper.labels.reportTitle + " "
                + (++currentSheetIndex));
		currentRowIndex = 0;
	}

    protected void createNewSheetWithFreezeHeader() {
        createNewSheet();
        createExportDataHeader();
        sheet.createFreezePane(0, currentRowIndex);
    }

    protected void createExportDataHeader() {
        final Map<String, Object> headerCommonInfo = new LinkedHashMap<>();
        headerCommonInfo.put(dataWrapper.labels.agents,
                getAgentNamesCellContent(dataWrapper.agentDisplayNames));
        headerCommonInfo.put(dataWrapper.labels.startDateTime,
				dateFormatter.print(new DateTime(dataWrapper.startDateTime)));
        headerCommonInfo.put(dataWrapper.labels.endDateTime,
				dateFormatter.print(new DateTime(dataWrapper.endDateTime)));
        headerCommonInfo.put(dataWrapper.labels.timeZone,
                timeZone);

        createCommonHeader(dataWrapper.labels.reportTitle,
                headerCommonInfo);
    }

	protected String getAgentNamesCellContent(
			final Collection<String> agentDisplayNames) {
		String agentNames = "";
		for (final String agentName : agentDisplayNames) {
			if (!agentNames.isEmpty()) {
				agentNames += ", ";
			}
			agentNames += agentName;
		}
		return agentNames;
	}

	private void initFormats(final String dateFormat, final String locale, final String timeZone) {
		final Font arial10Font = workbook.createFont();
		arial10Font.setFontHeightInPoints((short) 10);
		arial10Font.setFontName("Arial");

		arial10Style = workbook.createCellStyle();
		arial10Style.setFont(arial10Font);
		arial10Style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		arial10Style.setWrapText(true);

		defaultFormatStyle = arial10Style;

		headerDefaultFormatStyle = workbook.createCellStyle();
		headerDefaultFormatStyle.cloneStyleFrom(defaultFormatStyle);

		final Font arial10BoldFont = workbook.createFont();
		arial10BoldFont.setFontHeightInPoints((short) 10);
		arial10BoldFont.setFontName("Arial");
		arial10BoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		arial10BoldStyle = workbook.createCellStyle();
		arial10BoldStyle.setFont(arial10BoldFont);
		arial10BoldStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		arial10BoldStyle.setWrapText(true);

		arial10BoldCentreStyle = workbook.createCellStyle();
		arial10BoldCentreStyle.setFont(arial10BoldFont);
		arial10BoldCentreStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		arial10BoldCentreStyle.setAlignment(CellStyle.ALIGN_CENTER);
		arial10BoldCentreStyle.setWrapText(true);
		setAllBorders(arial10BoldCentreStyle, CellStyle.BORDER_MEDIUM);

		final Font arial12BoldFont = workbook.createFont();
		arial12BoldFont.setFontHeightInPoints((short) 12);
		arial12BoldFont.setFontName("Arial");
		arial12BoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		arial12BoldCentreStyle = workbook.createCellStyle();
		arial12BoldCentreStyle.setFont(arial12BoldFont);
		arial12BoldCentreStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		arial12BoldCentreStyle.setAlignment(CellStyle.ALIGN_CENTER);
		arial12BoldCentreStyle.setWrapText(true);

		dateFormatter = DateTimeFormat.forPattern(dateFormat).withLocale(Locale.forLanguageTag(locale));
		this.timeZone = timeZone;
		final CreationHelper createHelper = workbook.getCreationHelper();
		dateCellFormatStyle = workbook.createCellStyle();
		final String excelFormatPattern = DateFormatConverter.convert(
				new Locale(locale), dateFormat);
		dateCellFormatStyle.setDataFormat(createHelper.createDataFormat()
				.getFormat(excelFormatPattern));
		dateCellFormatStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		dateCellFormatStyle.setAlignment(CellStyle.ALIGN_LEFT);
		setAllBorders(dateCellFormatStyle, CellStyle.BORDER_THIN);

		headerDateCellFormatStyle = workbook.createCellStyle();
		headerDateCellFormatStyle.cloneStyleFrom(dateCellFormatStyle);
		setAllBorders(headerDateCellFormatStyle, CellStyle.BORDER_NONE);
	}

	protected void setAllBorders(final CellStyle style, final short borderType) {
		style.setBorderTop(borderType);
		style.setBorderLeft(borderType);
		style.setBorderBottom(borderType);
		style.setBorderRight(borderType);
	}
}