package com.tecomgroup.qos.tools.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.rrd4j.core.RrdDb;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.DataBatchProcessor;
import com.tecomgroup.qos.util.RrdBatchRetrievingStrategy;
import com.tecomgroup.qos.util.RrdToCsvBatchProcessingStrategy;
import com.tecomgroup.qos.util.RrdUtil;

/**
 * @author smyshlyaev.s
 */
@Component
public class RrdToCsv implements QoSTool {

	@Value("${rrd.to.csv.input.path}")
	private String inputPath;

	@Value("${rrd.to.csv.interval.start}")
	private String start;

	@Value("${rrd.to.csv.interval.end}")
	private String end;

	@Value("${rrd.to.csv.output.path}")
	private String outputPath;

	@Value("${rrd.to.csv.export.timezone}")
	private String exportTimezone;

	@Value("${rrd.to.csv.batch.size}")
	private Integer batchSize;

	@Value("${rrd.to.csv.start.end.labels}")
	private Boolean useStartEndLabels;

	private static Logger LOGGER = Logger.getLogger(RrdToCsv.class);

	private File createNewFile(final String filePath) {
		final String extension = FilenameUtils.getExtension(filePath);
		final String fileNameWithoutExtension = FilenameUtils
				.removeExtension(filePath);
		int i = 1;
		File file = new File(filePath);
		while (file.exists()) {
			final String fileName = fileNameWithoutExtension + "(" + i + ")."
					+ extension;
			file = new File(fileName);
			i++;
		}
		return file;
	}

	@Override
	public void execute() {
		try {
			final TimeZone timezone = TimeZone.getTimeZone(exportTimezone);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd.MM.yyyy HH-mm", new Locale("ru"));
			dateFormat.setTimeZone(timezone);

			final RrdDb rrd = new RrdDb(inputPath);
			final String datasourceName = RrdUtil.getDatasourceName(rrd, 0);
			final Long samplingRate = RrdUtil.getSamplingRate(rrd);

			final Long startTime = start.isEmpty()
					? RrdUtil.findDatasourceStartTime(rrd, datasourceName,
							batchSize) : Long.parseLong(start);
			final Long endTime = end.isEmpty() ? RrdUtil.getArcEndTime(rrd,
					samplingRate) : Long.parseLong(end);

			final String formattedStartDateTime = dateFormat.format(startTime
					* TimeConstants.MILLISECONDS_PER_SECOND);
			final String formattedEndDateTime = dateFormat.format(new Date(
					endTime * TimeConstants.MILLISECONDS_PER_SECOND));

			if (outputPath.isEmpty()) {
				outputPath = RrdUtil.createExportResultsFileName(
						formattedStartDateTime, formattedEndDateTime,
						timezone.getDisplayName(false, TimeZone.SHORT), false)
						+ ".csv";
			}
			final File outputFile = createNewFile(outputPath);

			try (PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(outputFile)))) {
				final RrdBatchRetrievingStrategy retrievingStrategy = new RrdBatchRetrievingStrategy(
						rrd);
				final SimpleDateFormat resultsDateFormat = new SimpleDateFormat(
						"dd.MM.yyyy HH:mm:ss", new Locale("ru"));
				resultsDateFormat.setTimeZone(timezone);
				final RrdToCsvBatchProcessingStrategy processingStrategy = new RrdToCsvBatchProcessingStrategy(
						writer, resultsDateFormat);

				final DataBatchProcessor<List<Map.Entry<Long, Double>>> batchProcessor = new DataBatchProcessor<>(
						"Rrd to csv export", retrievingStrategy,
						processingStrategy);
				LOGGER.info("Starting export\n" + "Input file: " + inputPath
						+ "\n" + "Output file: " + outputFile.getAbsolutePath()
						+ "\n" + "Start timestamp: " + startTime + ", date is "
						+ formattedStartDateTime + "\n" + "End timestamp: "
						+ endTime + ", date is " + formattedEndDateTime + "\n"
						+ "Total of " + (endTime - startTime)
						+ " points will be exported\n");

				if (useStartEndLabels) {
					writer.write("Start of data\n");
				}
				batchProcessor.process(startTime, endTime, batchSize);
				if (useStartEndLabels) {
					writer.write("End of data\n");
				}
				writer.flush();

				LOGGER.info("Export finished.");
			} catch (final IOException e) {
				LOGGER.error(
						"Error writing csv results to "
								+ outputFile.getAbsolutePath(), e);
			}

		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public String getDescription() {
		return "Export RRD file to CSV\n"
				+ "Avaliable parameters:\n"
				+ "Mandatory:\n"
				+ "rrd.to.csv.input.path - path to input file\n"
				+ "Optional:\n"
				+ "rrd.to.csv.interval.start - export start Unix timestamp. "
				+ "Defaults to start of the file. "
				+ "Use http://www.epochconverter.com/ to convert human date to timestamp\n"
				+ "rrd.to.csv.interval.end - export end Unix timestamp. "
				+ "Defaults to end of the file. "
				+ "Use http://www.epochconverter.com/ to convert human date to timestamp\n"
				+ "rrd.to.csv.output.path - path to output file. If a file with such name exists, new one with digit suffix will be created. "
				+ "If not specified, a new file with generated name will be created in the current directory.\n"
				+ "rrd.to.csv.export.timezone - timezone in java format. Defaults \"Europe/Moscow\".\n"
				+ "rrd.to.csv.batch.size - size of result interval in seconds that should be proccessed as one chunk (performance tuning).\n"
				+ "rrd.to.csv.start.end.labels - indicated that 'start of data' and 'end of data' labels should be printed.\n"
				+ "Example usage:"
				+ "RrdToCsv rrd.to.csv.input.path=/opt/qos/3.0/rrdHome/NTV_Vostok.MpegTSStatisticsIPTVControlModule.710/dataBitrate.56413.rrd rrd.to.csv.interval.start=1413367200 rrd.to.csv.export.timezone=Asia/Krasnoyarsk";

	}
}
