/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.servlet;

import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.util.RrdUtil;
import com.tecomgroup.qos.util.SimpleUtils;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sviyazov.a
 * 
 */
public class ResultXlsxWriter
		extends
			XlsxExportDataWriter<ExportResultsWrapper> {

	private final CellStyle numberFormat;

	private final String noDataLabel;

	private final int columnsSize;

	// as we use SXSSFWorkbook, we cannot correctly use autosize for all row
	// range
	private final int[] columnsMinWidth;

	private DateTime lastUpdatedDateTime = null;

	private final Map<String, Boolean> parametersEndOfData = new HashMap<String, Boolean>();

	private final Map<String, Boolean> parametersStartOfData = new HashMap<String, Boolean>();

	public ResultXlsxWriter(final OutputStream outputStream,
			final ExportResultsWrapper exportResultsWrapper) {
		super(outputStream, exportResultsWrapper.dateFormat,
				exportResultsWrapper.locale, exportResultsWrapper, exportResultsWrapper.timeZone);
		numberFormat = workbook.createCellStyle();
		final DataFormat format = workbook.createDataFormat();
		numberFormat.setDataFormat(format.getFormat(SimpleUtils.NUMBER_FORMAT));
		numberFormat.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		numberFormat.setAlignment(CellStyle.ALIGN_LEFT);
		setAllBorders(numberFormat, CellStyle.BORDER_THIN);

		this.noDataLabel = exportResultsWrapper.labels.noData;

		final Font arial10Font = workbook.createFont();
		arial10Font.setFontHeightInPoints((short) 10);
		arial10Font.setFontName("Arial");

		defaultFormatStyle = workbook.createCellStyle();
		defaultFormatStyle.setFont(arial10Font);
		defaultFormatStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		defaultFormatStyle.setWrapText(true);
		setAllBorders(defaultFormatStyle, CellStyle.BORDER_THIN);

		columnsSize = exportResultsWrapper.labels.parameterDisplayNames.size();
		columnsMinWidth = new int[columnsSize];

	}

	private void adjustColumnWidth(final int startColumnIndex,
			final int endColumnIndex) {
        if (sheet != null) {
            for (int i = startColumnIndex; i < endColumnIndex; ++i) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < columnsMinWidth[i]) {
                    sheet.setColumnWidth(i, columnsMinWidth[i]);
                }
            }
        }
	}

	@Override
	protected void createExportDataHeader() {
		super.createExportDataHeader();

		final Row headerRow = sheet.createRow(currentRowIndex);
		for (int i = 0; i < columnsSize; ++i) {
			final Cell cell = headerRow.createCell(i);
			cell.setCellValue(dataWrapper.labels.parameterDisplayNames.get(i));
			cell.setCellStyle(arial10BoldCentreStyle);
			sheet.autoSizeColumn(i);
			columnsMinWidth[i] = sheet.getColumnWidth(i);
		}
		currentRowIndex++;
	}

	private String getBooleanOrLabelString(final String storageKey,
			final Double value) {
		String result = noDataLabel;
		if (Double.isInfinite(value)) {
			if (value.equals(RrdUtil.START_OF_DATA)) {
				result = dataWrapper.labels.startOfDataLabel;
			} else {
				result = dataWrapper.labels.endOfDataLabel;
				parametersEndOfData.put(storageKey, true);
			}
		} else {
			final Boolean booleanValue = SimpleUtils.doubleAsBoolean(value);
			if (booleanValue != null) {
				result = booleanValue
						? dataWrapper.labels.trueMessage
						: dataWrapper.labels.falseMessage;
			}
		}
		return result;
	}

	/**
	 * Writes results to .xls file. Can be used to write results chunk by chunk.
	 * 
	 * @param results
	 *            - list of parameters results received from
	 *            {@link com.tecomgroup.qos.service.ResultService}
	 * @param sortedStorageKeys
	 *            - storage keys that are sorted for right matching column's
	 *            headers and data
	 * @throws Exception
	 */
	public void writeResultData(final List<Map<String, Object>> results,
			final List<String> sortedStorageKeys) {

		for (int resultIndex = 0; resultIndex < results.size(); resultIndex++) {
			if (sheet == null || currentRowIndex >= ROWS_PER_SHEET) {
				createNewSheetWithFreezeHeader();
			}
			final Map<String, Object> resultMap = results.get(resultIndex);

			final DateTime currentDateValue = new DateTime(resultMap
					.get(SimpleUtils.DATE_PARAMETER_NAME)).withZone(DateTimeZone.forID(timeZone));
			if (lastUpdatedDateTime == null
				|| currentDateValue.isAfter(lastUpdatedDateTime)) {
				lastUpdatedDateTime = currentDateValue;

				int columnIndex = 0;
				final Row row = sheet.createRow(currentRowIndex++);
				final Cell dateCell = row.createCell(columnIndex++);
				dateCell.setCellValue(dateFormatter.print(currentDateValue));
				dateCell.setCellStyle(dateCellFormatStyle);

				for (final String storageKey : sortedStorageKeys) {
					final Cell valueCell = row.createCell(columnIndex++);
					Double value = (Double) resultMap.get(storageKey);
					if (value == null) {
						if (parametersStartOfData.containsKey(storageKey)
								&& !parametersEndOfData.containsKey(storageKey)) {
							// write end of data
							value = RrdUtil.END_OF_DATA;
							parametersEndOfData.put(storageKey, true);
						}
					} else if (value.equals(RrdUtil.END_OF_DATA)) {
						// don't write end of data if it was already written
						if (parametersEndOfData.containsKey(storageKey)) {
							value = null;
						}
					}
					if (value != null) {
						if (dataWrapper.booleanResults || Double.isNaN(value)
								|| Double.isInfinite(value)) {
							valueCell.setCellValue(getBooleanOrLabelString(
									storageKey, value));
							valueCell.setCellStyle(defaultFormatStyle);
						} else {
							valueCell.setCellValue(value);
							valueCell.setCellStyle(numberFormat);
						}
						parametersStartOfData.put(storageKey, true);
					}
				}
			}
		}
		adjustColumnWidth(0, columnsSize);
	}
}
