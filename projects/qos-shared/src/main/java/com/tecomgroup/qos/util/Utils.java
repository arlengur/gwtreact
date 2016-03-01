/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.service.SystemInformationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author kunilov.p
 * 
 */
public class Utils extends SimpleUtils {

	private final static Logger LOGGER = Logger.getLogger(Utils.class);

	private static EmailValidator emailValidator = EmailValidator.getInstance();

	public static String computeMD5Hash(final String str) {
		return DigestUtils.md5Hex(str);
	}

	public static void copyFile(final String sourceFilePath,
			final String destFilePath) throws IOException {
		final File sourceFile = new File(sourceFilePath);
		final File destFile = new File(destFilePath);

		if (!destFile.exists()) {
			destFile.createNewFile();
		} else {
			destFile.delete();
			destFile.createNewFile();
		}

		FileInputStream source = null;
		FileOutputStream destination = null;
		try {
			source = new FileInputStream(sourceFile);
			destination = new FileOutputStream(destFile);

			// previous code: destination.transferFrom(source, 0,
			// source.size());
			// to avoid infinite loops, should be:
			long count = 0;
			final long size = source.getChannel().size();
			while ((count += destination.getChannel().transferFrom(
					source.getChannel(), count, size - count)) < size) {
				;
			}
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static String getUniqueQueueName(final String queue,final String serverName) {
		String uniqueName;
		try {
			uniqueName = queue + " : "
					+ serverName;
		} catch (final Exception e) {
			uniqueName = null;
		}
		return uniqueName;
	}

	public static Criterion createNotDeletedAndDisabledCriterion(
			final boolean onlyActive) {
		final CriterionQuery query = CriterionQueryFactory.getQuery();
		Criterion resultCriterion = query.eq("deleted", false);
		if (onlyActive) {
			resultCriterion = query.and(resultCriterion,
					query.eq("disabled", false));
		}

		return resultCriterion;
	}

	public static Criterion createTimeIntervalIntersectionCriterion(
			final Date startDateTime, final String startDateTimeLabel,
			final Date endDateTime, final String endDateTimeLabel) {
		final CriterionQuery query = CriterionQueryFactory.getQuery();

		Criterion resultCriterion = query.ge(endDateTimeLabel, startDateTime);
		resultCriterion = query.or(resultCriterion,
				query.isNull(endDateTimeLabel));
		resultCriterion = query.and(resultCriterion,
				query.le(startDateTimeLabel, endDateTime));

		return resultCriterion;
	}

	/**
	 * 
	 * @param applicationHome
	 * @param file
	 * @return file as it is if path is absolute or applicationHome is null,
	 *         otherwise absolute path related to applicationHome
	 */
	public static File getAbsoluteFile(final String applicationHome,
			final File file) {
		if (file == null) {
			return null;
		}
		if (applicationHome == null || file.isAbsolute()) {
			return file.getAbsoluteFile();
		}
		final File applicationHomeFile = new File(applicationHome);
		Assert.isTrue(applicationHomeFile.exists(), "Application home "
				+ applicationHome + " is not found");
		Assert.isTrue(applicationHomeFile.isDirectory(), "Application home "
				+ applicationHome + " is not directory");
		return new File(applicationHomeFile, file.getPath()).getAbsoluteFile();
	}

	/**
	 * Gets routing key for service messages for given agent
	 * 
	 * @param broadcastKey
	 * @param agentKey
	 * @return
	 */
	public static String getAgentRoutingKey(final String broadcastKey,
			final String agentKey) {
		return agentKey == null
				? broadcastKey
				: (broadcastKey + "-" + agentKey);
	}

	/**
	 * Returns formatted version of this application (ex: 3.0.10-234fa78).
	 * 
	 * @param properties
	 *            should contain fields: git.commit.id.abbrev and build.version
	 */
	public static String getApplicationVersion(final Properties properties) {
		String applicationVersion = null;
		try {
			applicationVersion = SystemInformationService.class.getPackage()
					.getImplementationVersion();

			if (SimpleUtils.isNotNullAndNotEmpty(applicationVersion)) {
				applicationVersion = applicationVersion.replaceFirst(
						"SNAPSHOT",
						properties.getProperty("build.version")
								+ "-"
								+ properties
										.getProperty("git.commit.id.abbrev"));
			}

		} catch (final Exception ex) {
			LOGGER.warn("Could not read package version.", ex);
		}
		return applicationVersion;
	}

	public static boolean isAnnotationPresent(final Class<?> clazz,
			final Class<? extends Annotation> annotationType) {
		final boolean present = clazz.isAnnotationPresent(annotationType);
		if (!present) {
			final Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				return isAnnotationPresent(superclass, annotationType);
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public static boolean isEmailValid(final String email) {
		return emailValidator.isValid(email);
	}

	public static boolean isUserLoginValid(final String login) {
		return isNotNullAndNotEmpty(login)
				&& Pattern.compile(MUser.LOGIN_VALID_PATTERN).matcher(login)
						.matches()
				&& login.length() <= MUser.LOGIN_MAX_VALID_SIZE;
	}

	public static void moveFile(final String oldFileLocation,
			final String newFileLocation) {
		try {
			final File oldStorageFile = new File(oldFileLocation);
			if (!oldStorageFile.renameTo(new File(newFileLocation))) {
				Utils.copyFile(oldFileLocation, newFileLocation);
				oldStorageFile.delete();
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to move file from "
					+ oldFileLocation + " to " + newFileLocation);
		}
	}

	/**
	 * Parses a List of Lists into a List of Maps. Useful to store key-value
	 * hashes in properties. Key=property pair must be a single string.
	 * 
	 * @param list
	 * @return
	 */
	public static List<Map<String, String>> parseListOfMaps(
			final List<List<String>> list) {
		final List<Map<String, String>> result = new ArrayList<>();
		for (final List<String> rawMap : list) {
			final Map<String, String> map = new HashMap<>();
			for (final String element : rawMap) {
				final String[] tokens = element.split("=");
				if (tokens.length > 2) {
					throw new RuntimeException(
							"Key-value pair is not well formed: " + element);
				} else {
					final String secondToken = tokens.length == 2
							? tokens[1]
							: "";
					map.put(tokens[0], secondToken);
				}
			}
			result.add(map);
		}
		return result;
	}
}
