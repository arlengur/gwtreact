/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.domain.MAlertUpdate;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author sviyazov.a
 * 
 */
public class ReportXlsxWriter extends XlsxExportDataWriter<AlertReportWrapper> {

	private CellStyle criticalCellStyle;
	private CellStyle majorCellStyle;
	private CellStyle minorCellStyle;
	private CellStyle noticeCellStyle;
	private CellStyle warningCellStyle;
	private CellStyle indeterminateCellStyle;
	private CellStyle durationCellStyle;
	private Map<MAlertType.PerceivedSeverity, String> severities;

	private final String ALERT_TYPE_FIELD = "alert.alertType.displayName";

	private final String ALERT_SEVERITY_FIELD = "perceivedSeverity";

	private final PropertyUtilsBean propertyUtilsBean;

	private final String TIME_OVER_24H_FORMAT = "[h]:mm:ss";

	private Map<String, String> selectedColumns;

	public ReportXlsxWriter(final OutputStream outputStream,
			final AlertReportWrapper alertReportWrapper, final String timeZone) {
		super(outputStream, alertReportWrapper.dateFormat,
				alertReportWrapper.locale, alertReportWrapper, timeZone);
		propertyUtilsBean = new PropertyUtilsBean();
		initReportCellStyles();
		createReport();
	}

	private CellStyle configureCellStyleWithThinBorder(
			final CellStyle originalCellStyle) {
		final CellStyle style = workbook.createCellStyle();
		style.cloneStyleFrom(originalCellStyle);
		setAllBorders(style, CellStyle.BORDER_THIN);
		return style;
	}

    private int getColumnWidth(final int columnIndex) {
        switch(columnIndex) {
            case 1:
                return 35 * 256;
            case 2:
                return 30 * 256;
            default:
                return 23 * 256;
        }
    }

	@Override
	protected void createExportDataHeader() {
		super.createExportDataHeader();
		final int severitiesRowStart = currentRowIndex;
		Row row = sheet.createRow(currentRowIndex);
		Cell cell = row.createCell(0);
		cell.setCellStyle(arial10BoldStyle);
		cell.setCellValue(dataWrapper.labels.severity);

		for (final MAlertType.PerceivedSeverity severity : severities.keySet()) {
			row = sheet.getRow(currentRowIndex);
			if (row == null) {
				row = sheet.createRow(currentRowIndex);
			}
			cell = row.createCell(1);
			cell.setCellStyle(getCellStyle(severity));
			cell.setCellValue(severities.get(severity));
			currentRowIndex++;
		}

		sheet.addMergedRegion(new CellRangeAddress(severitiesRowStart,
				currentRowIndex - 1, 0, 0));
		createHeaderColumn();

		sheet.getPrintSetup().setLandscape(true);
	}

	private void createHeaderColumn() {
		selectedColumns = new LinkedHashMap<String, String>();
		final String[] hiddenColumns = dataWrapper.template.getHiddenColumns();
		final Map<String, String> displayColumn = dataWrapper.labels.columnDisplayNames;
		for (final String columnPath : displayColumn.keySet()) {
			Boolean match = false;
			for (final String hidden : hiddenColumns) {
				if (columnPath.equals(hidden)) {
					match = true;
					break;
				}
			}
			// alert type must be always in report to be colored
			if (!match || ALERT_TYPE_FIELD.equals(columnPath)) {
				selectedColumns.put(columnPath, displayColumn.get(columnPath));
			}
		}

		int numberColumn = 0;
		final Row row = sheet.createRow(currentRowIndex++);
		for (final String keyColumn : selectedColumns.keySet()) {
			final Cell cell = row.createCell(numberColumn);
			cell.setCellStyle(arial10BoldCentreStyle);
			cell.setCellValue(selectedColumns.get(keyColumn));
			sheet.setColumnWidth(numberColumn, getColumnWidth(numberColumn));
			numberColumn++;
		}

        final Cell cell = row.createCell(numberColumn);
        cell.setCellStyle(arial10BoldCentreStyle);
        cell.setCellValue(dataWrapper.labels.comments);
        sheet.setColumnWidth(numberColumn, getColumnWidth(numberColumn) * 2);
	}

	private void createReport() {
		severities = dataWrapper.labels.severities;
		initFormatsToWriteContent();
	}

	private double formatDuration(final long duration) {
		final double result = (double) duration
				/ (double) TimeConstants.MILLISECONDS_PER_DAY;
		return result;
	}

	private CellStyle getCellStyle(final MAlertType.PerceivedSeverity severity) {
		CellStyle cellStyle;
		switch (severity) {
			case CRITICAL :
				cellStyle = criticalCellStyle;
				break;
			case MAJOR :
				cellStyle = majorCellStyle;
				break;
			case MINOR :
				cellStyle = minorCellStyle;
				break;
			case NOTICE :
				cellStyle = noticeCellStyle;
				break;
			case WARNING :
				cellStyle = warningCellStyle;
				break;
			case INDETERMINATE :
				cellStyle = indeterminateCellStyle;
				break;
			default :
				cellStyle = defaultFormatStyle;
				break;
		}
		return cellStyle;
	}

	private void initFormatsToWriteContent() {
		defaultFormatStyle = configureCellStyleWithThinBorder(defaultFormatStyle);
		criticalCellStyle = configureCellStyleWithThinBorder(criticalCellStyle);
		majorCellStyle = configureCellStyleWithThinBorder(majorCellStyle);
		warningCellStyle = configureCellStyleWithThinBorder(warningCellStyle);
		minorCellStyle = configureCellStyleWithThinBorder(minorCellStyle);
		noticeCellStyle = configureCellStyleWithThinBorder(noticeCellStyle);
		indeterminateCellStyle = configureCellStyleWithThinBorder(indeterminateCellStyle);
	}

	private void initReportCellStyles() {

		final CreationHelper createHelper = workbook.getCreationHelper();
		durationCellStyle = workbook.createCellStyle();
		durationCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		durationCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		setAllBorders(durationCellStyle, CellStyle.BORDER_THIN);
		durationCellStyle.setDataFormat(createHelper.createDataFormat()
				.getFormat(TIME_OVER_24H_FORMAT));

		criticalCellStyle = workbook.createCellStyle();
		criticalCellStyle.cloneStyleFrom(defaultFormatStyle);
		criticalCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		criticalCellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
		criticalCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

		majorCellStyle = workbook.createCellStyle();
		majorCellStyle.cloneStyleFrom(criticalCellStyle);
		majorCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		majorCellStyle.setFillBackgroundColor(IndexedColors.ORANGE.getIndex());

		warningCellStyle = workbook.createCellStyle();
		warningCellStyle.cloneStyleFrom(criticalCellStyle);
		warningCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
		warningCellStyle.setFillBackgroundColor(IndexedColors.GOLD.getIndex());

		minorCellStyle = workbook.createCellStyle();
		minorCellStyle.cloneStyleFrom(criticalCellStyle);
		minorCellStyle.setFillForegroundColor(IndexedColors.DARK_YELLOW
				.getIndex());
		minorCellStyle.setFillBackgroundColor(IndexedColors.DARK_YELLOW
				.getIndex());

		noticeCellStyle = workbook.createCellStyle();
		noticeCellStyle.cloneStyleFrom(criticalCellStyle);
		noticeCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT
				.getIndex());
		noticeCellStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT
				.getIndex());

		final Font arial10WhiteFont = workbook.createFont();
		arial10WhiteFont.setFontHeightInPoints((short) 10);
		arial10WhiteFont.setFontName("Arial");
		arial10WhiteFont.setColor(IndexedColors.WHITE.getIndex());

		indeterminateCellStyle = workbook.createCellStyle();
		indeterminateCellStyle.cloneStyleFrom(criticalCellStyle);
		indeterminateCellStyle
				.setFillForegroundColor(IndexedColors.GREY_80_PERCENT
						.getIndex());
		indeterminateCellStyle
				.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT
						.getIndex());
		indeterminateCellStyle.setWrapText(false);
		indeterminateCellStyle.setFont(arial10WhiteFont);
	}

	/**
	 * Insert records into the open writer
	 * 
	 * @param alertReports
	 * @param comments
	 */
	public void insertRecords(final List<MAlertReport> alertReports, final Map<Long, List<MAlertUpdate>> comments)	throws Exception {

		if (sheet == null || currentRowIndex >= ROWS_PER_SHEET) {
			createNewSheetWithFreezeHeader();
		}

		for (final MAlertReport alertReport : alertReports) {
			final Row row = sheet.createRow(currentRowIndex++);
			int columnIndex = 0;
			for (final String pathColumn : selectedColumns.keySet()) {
				CellStyle cellStyle = null;
				final Cell cell = row.createCell(columnIndex++);
				final Object value = propertyUtilsBean.getNestedProperty(
						alertReport, pathColumn);
				if (value == null) {
					cell.setCellStyle(defaultFormatStyle);
					cell.setCellType(Cell.CELL_TYPE_BLANK);
					continue;
				}
				String toWrite = "";
				if (value instanceof String) {
					toWrite = (String) value;
					if (ALERT_TYPE_FIELD.equals(pathColumn)) {
						cellStyle = getCellStyle((MAlertType.PerceivedSeverity) propertyUtilsBean
								.getProperty(alertReport, ALERT_SEVERITY_FIELD));
					}
				}
				if (value instanceof Long) {
					final double duration = formatDuration((long) value);
					cell.setCellValue(duration);
					cell.setCellStyle(durationCellStyle);
					continue;
				}
				if (value instanceof Double) {
					cell.setCellValue((double)value);
					cell.setCellStyle(defaultFormatStyle);
					continue;
				}
				if (value instanceof Date) {
					DateTime dateValue = new DateTime(value).withZone(DateTimeZone.forID(timeZone));
					cell.setCellValue(dateFormatter.print(dateValue));
					cell.setCellStyle(dateCellFormatStyle);
					continue;
				}
				if (value instanceof Boolean) {
					toWrite = value.toString();
				}
				if (value instanceof MAlertType.PerceivedSeverity) {
					final MAlertType.PerceivedSeverity severity = (MAlertType.PerceivedSeverity) value;
					toWrite = severities.get(severity);
				}

				cell.setCellValue(toWrite);
				cell.setCellStyle(cellStyle == null	? defaultFormatStyle : cellStyle);
			}

            writeReportComments(row.createCell(columnIndex++), alertReport, comments);
		}
	}

    private void writeReportComments(Cell cell, MAlertReport alertReport, Map<Long,
            List<MAlertUpdate>> allComments) {
        final List<MAlertUpdate> comments = allComments.get(alertReport.getId());
        String cellValue = "";

        if (comments != null) {
            for (MAlertUpdate comment : comments) {
				cellValue += dateFormatter.print(new DateTime(comment.getDateTime())) + " (" + comment.getUser()
                        + ") : " + comment.getComment() + "\n";
            }
        }
        cell.setCellValue(cellValue);
        cell.setCellStyle(defaultFormatStyle);
    }
}
