/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 * 
 */
public class Result {

	public static class ResultIdentifier
			implements
				Comparable<ResultIdentifier> {

		private final Date dateTime;

		private final String keyProperties;

		public ResultIdentifier(final Date dateTime, final String keyProperties) {
			this.dateTime = dateTime;
			this.keyProperties = keyProperties;
		}

		@Override
		public int compareTo(final ResultIdentifier other) {
			if (other == null) {
				return 1;
			}

			int result = 0;
			if (keyProperties == null) {
				if (other.getKeyProperties() != null) {
					result = -1;
				}
			} else {
				if (other.getKeyProperties() == null) {
					result = 1;
				} else {
					result = keyProperties.compareToIgnoreCase(other
							.getKeyProperties());
				}
			}
			if (result == 0) {
				if (dateTime == null) {
					if (other.getDateTime() != null) {
						result = -1;
					}
				} else {
					if (other.getDateTime() == null) {
						result = 1;
					} else {
						result = dateTime.compareTo(other.getDateTime());
					}
				}
			}
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ResultIdentifier other = (ResultIdentifier) obj;
			if (dateTime == null) {
				if (other.dateTime != null) {
					return false;
				}
			} else if (!dateTime.equals(other.dateTime)) {
				return false;
			}
			if (keyProperties == null) {
				if (other.keyProperties != null) {
					return false;
				}
			} else if (!keyProperties.equals(other.keyProperties)) {
				return false;
			}
			return true;
		}

		/**
		 * @return the dateTime
		 */
		public Date getDateTime() {
			return dateTime;
		}

		/**
		 * @return the keyProperties
		 */
		public String getKeyProperties() {
			return keyProperties;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((dateTime == null) ? 0 : dateTime.hashCode());
			result = prime * result
					+ ((keyProperties == null) ? 0 : keyProperties.hashCode());
			return result;
		}
	}

	public static final String RESULT_DATE_FORMAT = "yyyyMMddHHmmssz";

	public static Date parseDateTime(final String dateTime) {
		Date parsedDateTime = null;
		try {
			parsedDateTime = (new SimpleDateFormat(RESULT_DATE_FORMAT))
					.parse(dateTime + "UTC");
		} catch (final ParseException e) {
			throw new ServiceException(e);
		}
		return parsedDateTime;
	}

	// FIXME Should be Date
	private String resultDateTime;

	@JsonIgnore
	private Date convertedResultDateTime;

	@JsonIgnore
	private List<MProperty> modelProperties;

	private Map<String, Double> parameters;

	private Map<String, String> properties;

	public Result() {
		super();
	}

	public Result(final Date resultDateTime,
			final Map<String, Double> parameters) {
		this.convertedResultDateTime = resultDateTime;
		this.parameters = parameters;
	}

	public Result(final Date resultDateTime,
			final Map<String, Double> parameters,
			final Map<String, String> properties) {
		this.convertedResultDateTime = resultDateTime;
		this.parameters = parameters;
		this.properties = properties;
	}

	public Result(final String resultDateTime,
			final Map<String, Double> parameters) {
		this.resultDateTime = resultDateTime;
		this.parameters = parameters;
	}

	public Result(final String resultDateTime,
			final Map<String, Double> parameters,
			final Map<String, String> properties) {
		this.resultDateTime = resultDateTime;
		this.parameters = parameters;
		this.properties = properties;
	}

	public Date getConvertedResultDateTime() {
		if (convertedResultDateTime == null) {
			convertedResultDateTime = parseDateTime(resultDateTime);
		}
		return convertedResultDateTime;
	}

	@JsonIgnore
	public List<MProperty> getModelProperties(
			final MResultConfigurationTemplate resultConfigurationTemplate) {
		if (properties != null && !properties.isEmpty()) {
			if (modelProperties == null) {
				modelProperties = new LinkedList<MProperty>();
			}
			if (modelProperties.isEmpty()) {
				final Map<String, MProperty> templatePropertyConfigurations = ParameterIdentifier
						.getProperties(resultConfigurationTemplate
								.getPropertyConfigurations());
				for (final Map.Entry<String, String> propertyEntry : properties
						.entrySet()) {
					if (templatePropertyConfigurations
							.containsKey(propertyEntry.getKey())) {
						final MProperty templateProperty = templatePropertyConfigurations
								.get(propertyEntry.getKey());
						final MProperty resultProperty = new MProperty(
								templateProperty);
						resultProperty.setValue(propertyEntry.getValue());
						modelProperties.add(resultProperty);
					}
				}
			}
		}
		return modelProperties;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, Double> getParameters() {
		return parameters;
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
	/**
	 * @return the resultDateTime
	 */
	public String getResultDateTime() {
		return resultDateTime;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(final Map<String, Double> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(final Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * @param resultDateTime
	 *            the resultDateTime to set
	 */
	public void setResultDateTime(final String resultDateTime) {
		this.resultDateTime = resultDateTime;
	}

	@Override
	public String toString() {
		return "{resultDateTime = " + resultDateTime + ", parameters = "
				+ parameters + "}";
	}
}
