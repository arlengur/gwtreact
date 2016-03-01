/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.BuildInfo;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.message.ApplicationMessage;
import com.tecomgroup.qos.util.ExposedPropertyPlaceholderConfigurer;
import com.tecomgroup.qos.util.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author abondin
 * 
 */
@Service("systemInformationService")
public class DefaultSystemInformationService
		implements
			SystemInformationService,
			InitializingBean {
	public static final String GIT_DATE_FORMAT = "dd.MM.yyyy '@' HH:mm:ss z";
	/**
	 * Maven artifactiId проекта, который будет использован для получения
	 * информации о билде
	 */
	public static final String BUILD_INFO_PROJECT = "qos-api";

	public static final String BUILD_INFO_FILE_SUFFIX = "-git.properties";

	public static final String BUILD_VERSION = "build.version";

	private BuildInfo buildInfo;

	@Autowired
	private ExposedPropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

	@Value("${probe.video.export.path}")
	String probeVideoExportPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		final Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream(
				"/META-INF/" + BUILD_INFO_PROJECT + BUILD_INFO_FILE_SUFFIX));
		final String branch = properties.getProperty("git.branch");
		final String commitId = properties.getProperty("git.commit.id");
		final String buildUserName = properties
				.getProperty("git.build.user.name");
		final String buildUserEmail = properties
				.getProperty("git.build.user.email");
		final Date buildDateTime = getDateTimeProperty(properties,
				"git.build.time");
		final String commitUserName = properties
				.getProperty("git.commit.user.name");
		final String commitUserEmail = properties
				.getProperty("git.commit.user.email");
		final String commitMessageShort = properties
				.getProperty("git.commit.message.short");
		final String commitMessageFull = properties
				.getProperty("git.commit.message.full");
		final Date commitDateTime = getDateTimeProperty(properties,
				"git.commit.time");

		final String buildVersion = properties.getProperty(BUILD_VERSION);

		final String applicationVersion = Utils
				.getApplicationVersion(properties);

		buildInfo = new BuildInfo(branch, commitId, buildUserName,
				buildUserEmail, buildDateTime, commitUserName, commitUserEmail,
				commitMessageShort, commitMessageFull, commitDateTime,
				applicationVersion, buildVersion);

	}

	/**
	 * Get string composed of offset in hours and minutes, and TimeZoneId
	 * 
	 * @param timeZoneId
	 * @param offsetInMilliseconds
	 * @return label TimeZone
	 */
	private String createTimeZoneLabel(final String timeZoneId,
			final int offsetInMilliseconds) {
		String offset = String
				.format("%02d:%02d",
						Math.abs(offsetInMilliseconds
								/ TimeConstants.MILLISECONDS_PER_HOUR),
						Math.abs((offsetInMilliseconds / TimeConstants.MILLISECONDS_PER_MINUTE)
								% TimeConstants.SECONDS_PER_MINUTE));
		offset = (offsetInMilliseconds >= 0 ? "+" : "-") + offset;
		return "(UTC" + offset + ") " + timeZoneId;
	}

	@Override
	public BuildInfo getBuildInfo() {
		return buildInfo;
	}

	@Override
	public Map<String, Object> getClientProperties() {
		Map<String, Object> properties=propertyPlaceholderConfigurer.getProperties("client.");
		properties.put(ApplicationMessage.PROBE_VIDEO_EXPORT_PATH, probeVideoExportPath);
		return properties;
	}

	private Date getDateTimeProperty(final Properties properties,
			final String propertyName) throws ParseException {
		final SimpleDateFormat dateTimeParser = new SimpleDateFormat(
				GIT_DATE_FORMAT);

		final String propertyValue = properties.getProperty(propertyName);
		final Date date = dateTimeParser.parse(propertyValue);

		return date;
	}

	@Override
	public List<TimeZoneWrapper> getTimeZoneList() {
		final List<TimeZoneWrapper> timeZoneResult = new ArrayList<TimeZoneWrapper>();
		final long currentTimeMillis = DateTime.now().getMillis();
		for (String timeZoneId : DateTimeZone.getAvailableIDs()){
			if (!isRedundantTimeZone(timeZoneId)) {
				int offsetInMilliseconds = DateTimeZone.forID(timeZoneId).getOffset(currentTimeMillis);
				final String timeZoneLabel = createTimeZoneLabel(timeZoneId,
						offsetInMilliseconds);
				timeZoneResult.add(new TimeZoneWrapper(timeZoneId, timeZoneLabel,
						offsetInMilliseconds));
			}
		}
		Collections.sort(timeZoneResult);
		return timeZoneResult;
	}

	private boolean isRedundantTimeZone(final String timeZoneId){
		return (timeZoneId.length() <= 3) ||
				timeZoneId.toUpperCase().startsWith("ETC") ||
				timeZoneId.toUpperCase().startsWith("GMT");
	}
}
