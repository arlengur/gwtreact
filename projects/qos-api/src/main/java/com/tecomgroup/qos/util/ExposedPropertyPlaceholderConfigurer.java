/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Constants;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.jmx.export.naming.SelfNaming;

import javax.management.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Exposes the content of PropertyPlaceholderConfigurer via JMX
 */
public class ExposedPropertyPlaceholderConfigurer
		extends
			PropertyPlaceholderConfigurer implements SelfNaming, DynamicMBean {
	private int systemPropertiesMode = PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK;
	private final Properties properties = new Properties();

	private String name;

	private static final Constants constants = new Constants(
			PropertyPlaceholderConfigurer.class);

	@Override
	public Object getAttribute(final String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		return properties.get(attribute);
	}

	@Override
	public AttributeList getAttributes(final String[] attributes) {
		final AttributeList attributeList = new AttributeList();
		for (final Entry<Object, Object> property : properties.entrySet()) {
			attributeList.add(new Attribute((String) property.getKey(),
					property.getValue()));
		}
		return attributeList;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		final MBeanAttributeInfo attributes[] = new MBeanAttributeInfo[properties
				.size()];
		int i = 0;
		for (final Object propertyName : properties.keySet()) {
			final MBeanAttributeInfo attribute = new MBeanAttributeInfo(
					(String) propertyName, Object.class.toString(), "", true,
					false, false);
			attributes[i] = attribute;
			i++;
		}
		return new MBeanInfo(
				ExposedPropertyPlaceholderConfigurer.class.getName(), "",
				attributes, new MBeanConstructorInfo[0],
				new MBeanOperationInfo[0], new MBeanNotificationInfo[0]);
	}

	@Override
	public ObjectName getObjectName() throws MalformedObjectNameException {
		return new ObjectName(SimpleUtils.getJMXObjectName(null, name == null
				? "Properties"
				: name));
	}

	/**
	 * Возвращает свойства, название которых начинается с укзанного префикса
	 * 
	 * @param prefix
	 * @return
	 */
	public Map<String, Object> getProperties(final String prefix) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		for (final Attribute attribute : getAttributes(null).asList()) {
			final String name = attribute.getName();
			if (name.startsWith(prefix)) {
				properties.put(name, attribute.getValue());
			}
		}
		return properties;
	}

	@Override
	public Object invoke(final String actionName, final Object[] params,
			final String[] signature) throws MBeanException,
			ReflectionException {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Properties mergeProperties() throws IOException {
		final Properties properties = super.mergeProperties();
		this.properties.putAll(properties);
		for (final Object propertyName : this.properties.keySet()) {
			String value = resolvePlaceholder((String) propertyName,
					properties, systemPropertiesMode);
			value = super.parseStringValue(value, properties,
					new HashSet<String>());
			this.properties.put(propertyName, value);
		}
		return properties;
	}

	public Resource[] getLocations() {
		try {
			Field field = PropertiesLoaderSupport.class.getDeclaredField("locations");
			field.setAccessible(true);
			Object value = field.get(this);
			field.setAccessible(false);

			if (value == null) {
				return null;
			} else if ( Resource[].class.isAssignableFrom(value.getClass())) {
				return ( Resource[]) value;
			}
			throw new RuntimeException("Can't read locations from placeholder");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Resource getResource(String name) {
		Resource result = null;
		Resource[] locations = getLocations();
		for(Resource resource: locations) {
			if(resource.getFilename().equals(name) && resource.exists()){
				result = resource;
			}
		}
		return result;
	}

	@Override
	public void setAttribute(final Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
	}

	@Override
	public AttributeList setAttributes(final AttributeList attributes) {
		return null;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	public void setPropertyPlaceholderName(final String name) {
		this.name = name;
	}

	@Override
	public void setSystemPropertiesModeName(final String constantName)
			throws IllegalArgumentException {
		super.setSystemPropertiesModeName(constantName);
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}
}
