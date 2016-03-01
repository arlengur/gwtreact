/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.configuration;

import java.io.File;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;
import com.tecomgroup.qos.json.QoSJsonObjectMapper;
import com.tecomgroup.qos.util.Utils;

/**
 * @author abondin
 * 
 */
@Component
public class JSONConfigurationStorage
		implements
			ConfigurationStorage,
			InitializingBean {

	private final ObjectMapper mapper = new QoSJsonObjectMapper();

	private final static Logger LOGGER = Logger
			.getLogger(JSONConfigurationStorage.class);

	private File configurationResource;

	@Value("#{systemProperties['pm.home']}")
	private final String pmHome = null;

	private PolicyManagerConfiguration configuration;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (configurationResource != null) {
			if (configurationResource.exists()) {
				LOGGER.info("Load configuration from "
						+ configurationResource.getAbsolutePath());
				try {
					configuration = mapper.readValue(configurationResource,
							PolicyManagerConfiguration.class);
				} catch (final Exception ex) {
					LOGGER.error("Cannot load local configuration", ex);
					configuration = new PolicyManagerConfiguration();
				}
			}
		}
	}

	@Override
	public PolicyManagerConfiguration loadLocal() {
		return configuration;
	}

	private void mkdirs(final File configurationFile) {
		if (configurationFile.getParentFile() != null
				&& !configurationFile.getParentFile().exists()) {
			configurationFile.getParentFile().mkdirs();
		}

	}

	/**
	 * @param configurationResource
	 *            the configurationResource to set
	 */
	@Value("${pm.configuration}")
	public void setConfigurationResource(final File configurationResource) {
		this.configurationResource = Utils.getAbsoluteFile(pmHome,
				configurationResource);
	}

	@Override
	public void updateConfiguration(
			final PolicyManagerConfiguration configuration) {
		this.configuration = configuration;
		if (configurationResource != null) {
			LOGGER.info("Save local configuration to "
					+ configurationResource.getAbsolutePath());
			if (!configurationResource.exists()) {
				mkdirs(configurationResource);
			}
			try {
				mapper.writerWithDefaultPrettyPrinter().writeValue(
						configurationResource, configuration);
			} catch (final Exception ex) {
				LOGGER.error("Cannot save configuration to local file", ex);
			}
		}
	}
}
