/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.jmx.export.naming.SelfNaming;

import javax.management.DynamicMBean;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomPropertyPlaceholderConfigurer
		extends
		ExposedPropertyPlaceholderConfigurer implements SelfNaming, DynamicMBean {
	private final static Logger LOGGER = Logger
			.getLogger(CustomPropertyPlaceholderConfigurer.class);

	private String applicationWorkDir;
	private Resource[] defaultPropertiesLocation;
	private static final String CONFIGURATION_DIRECTORY = "sa" + File.separator + "config";


	public void initCustomProperties() throws Exception {
		try {
			File configDir = new File(applicationWorkDir, CONFIGURATION_DIRECTORY);
			if (!configDir.exists()) {
				configDir.mkdirs();
			}
			Resource[] configs = defaultPropertiesLocation;
			List<Resource> resourcesList=new ArrayList();
			resourcesList.addAll(Arrays.asList(configs));
			for (Resource config : configs) {
				String fileName = config.getFilename();
				File configFile = new File(configDir, fileName);
				if (!configFile.exists()) {
					LOGGER.warn("Creating user configuration from default template :"+configFile.getAbsolutePath());
					configFile.createNewFile();
					FileUtils.copyFile(config.getFile(), configFile);
				}
				FileSystemResource configResource=new FileSystemResource(configFile);
				resourcesList.add(configResource);
			}
			this.setLocations(resourcesList.toArray(new Resource[resourcesList.size()]));
		}catch (Exception e)
		{
			LOGGER.warn("Cant initialize custom properties. Use default values");
			LOGGER.debug("Cant initialize custom properties. Use default values",e);
			this.setLocations(defaultPropertiesLocation);
		}
	}

	public String getApplicationWorkDir() {
		return applicationWorkDir;
	}

	public void setApplicationWorkDir(String applicationWorkDir) {
		this.applicationWorkDir = applicationWorkDir;
	}

	public Resource[] getDefaultPropertiesLocation() {
		return defaultPropertiesLocation;
	}

	public void setDefaultPropertiesLocation(Resource[] defaultPropertiesLocation) {
		this.defaultPropertiesLocation = defaultPropertiesLocation;
	}
}
