/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import com.tecomgroup.qos.CopyingEntity;
import com.tecomgroup.qos.PropertiesContainer;
import com.tecomgroup.qos.UpdatableEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MStream extends MAbstractEntity
		implements
			PropertiesContainer,
			UpdatableEntity<MStream>,
			CopyingEntity<MStream> {

	public static final String PROGRAM_NAME = "programName";

	public static final String RECORDED_FILE_PREFIX = "recordedFilePrefix";

	public static final String PROGRAM_NUMBER = "program_number";

	public static final String PROGRAM_KEY = "programKey";

	public static final String PROGRAM_KEY_DISPLAY_NAME = "Program Key";

	private static final String PLATFORM_PROPERTY = "platform";

	private static final String DESKTOP_PLATFORM = "desktop";
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	private static void copyStream(final MStream source, final MStream target,
			final boolean copyProperties) {
		if (source != null && target != null) {
			target.setKey(source.getKey());
			target.setDisplayName(source.getDisplayName());
			target.setSource(source.getSource());
			target.setDisabled(source.isDisabled());
			target.setTaskDisplayName(source.getTaskDisplayName());
			if (copyProperties) {
				target.setProperties(new ArrayList<MProperty>(source
						.getProperties()));
			}
		}
	}

	@Transient
	@JsonIgnore
	protected String displayName;

	@Transient
	@JsonIgnore
	protected Source source;

	@Transient
	@JsonIgnore
	protected boolean disabled;

	@Transient
	@JsonIgnore
	protected String taskDisplayName;

	@OneToMany(cascade = CascadeType.ALL)
	protected List<MProperty> properties;

	@Column(name = "STREAM_KEY", nullable = false)
	protected String key;

	public MStream() {
		super();
	}

	public MStream(final MStream stream) {
		copyStream(stream, this, true);
	}

	@Override
	public void addProperty(final MProperty property) {
		properties.add(property);
	}

	@Transient
	private void copyProperties(final List<MProperty> taskProperties) {
		if (taskProperties != null && !taskProperties.isEmpty()) {
			for (final MProperty property : taskProperties) {
				final String propertyName = property.getName();
				if (PROGRAM_NAME.equals(propertyName)) {
					displayName = property.getValue();
				} else if (RECORDED_FILE_PREFIX.equals(propertyName)) {
					final MProperty recordedFilePrefix = new MProperty(property);
					properties.add(recordedFilePrefix);
				} else if (PROGRAM_NUMBER.equals(propertyName)) {
					final MProperty programKey = new MProperty(property);
					programKey.setName(PROGRAM_KEY);
					programKey.setDisplayName(PROGRAM_KEY_DISPLAY_NAME);
					properties.add(programKey);
				}
			}
		}
	}

	@Transient
	@Override
	public void copyTo(final MStream stream) {
		copyStream(this, stream, true);
	}

	@Transient
	public void createTaskRelatedFields(final MAgentTask task) {
		source = Source.getTaskSource(task.getKey());
		copyProperties(task.getProperties());
		disabled = task.isDisabled();
		taskDisplayName = task.getDisplayName();
	}

	@Transient
	public boolean forDesktop() {
		boolean result = false;
		final MProperty platformProperty = getProperty(PLATFORM_PROPERTY);
		if (platformProperty == null
				|| DESKTOP_PLATFORM.equals(platformProperty.getValue())) {
			result = true;
		}
		return result;
	}

	@Transient
	public Source getComplexStreamSource() {
		return source == null ? null : Source.getStreamSource(source.getKey(),
				key);
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the properties
	 */
	@Override
	public List<MProperty> getProperties() {
		return properties;
	}

	@Override
	public MProperty getProperty(final String propertyName) {
		MProperty result = null;
		for (final MProperty property : properties) {
			if (property.getName().equals(propertyName)) {
				result = property;
				break;
			}
		}
		return result;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @return the taskDisplayName
	 */
	public String getTaskDisplayName() {
		return taskDisplayName;
	}

	@Transient
	protected String parseTemplateUrl(final String templateUrl,
			final List<MProperty> taskProperties) {
		String url = templateUrl;
		if (templateUrl != null && !templateUrl.isEmpty()) {
			if (taskProperties != null && !taskProperties.isEmpty()) {
				for (final MProperty property : taskProperties) {
					String propertyValue = property.getValue();
					if (propertyValue == null) {
						propertyValue = "";
					}
					url = url.replace("${properties." + property.getName()
							+ "}", propertyValue);
				}
			}

			if (url.contains("${")) {
				// remove missed property from URL
				String currentUrl = url;
				while (currentUrl.contains("${")) {
					final String missedProperty = currentUrl.substring(
							currentUrl.indexOf("${"),
							currentUrl.indexOf("}") + 1);
					currentUrl = currentUrl.replace(missedProperty, "");
				}
				url = currentUrl;
			}
		}
		return url;
	}

	@Override
	public void removeProperty(final MProperty property) {
		properties.remove(property);
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(final List<MProperty> properties) {
		this.properties = properties;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final Source source) {
		this.source = source;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @param taskDisplayName
	 *            the displayName to set
	 */
	public void setTaskDisplayName(final String taskDisplayName) {
		this.taskDisplayName = taskDisplayName;
	}

	@Override
	public String toString() {
		return "{ key = " + key + ", source = " + source + ", displayName = "
				+ displayName + ", properties = "
				+ (properties == null ? "null" : properties.toString()) + " }";
	}

	@Override
	public boolean updateSimpleFields(final MStream stream) {
		// TODO update simple stream fields
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
