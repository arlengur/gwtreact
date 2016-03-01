/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.pm;

import java.util.List;

import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class PMTaskConfiguration extends PMConfiguration {

	private MResultConfiguration configuration;

    private Source systemComponenet;

	public PMTaskConfiguration() {
		super();
	}

	public PMTaskConfiguration(final PMTaskConfiguration pmTaskConfiguration) {
		super(pmTaskConfiguration);
		this.configuration = pmTaskConfiguration.getConfiguration();
        this.systemComponenet = pmTaskConfiguration.getSystemComponenet();
	}

	public PMTaskConfiguration(final Source systemComponent, final Source source,
                               final MResultConfiguration configuration,
                               final List<MPolicy> policies) {
		super(source, policies);
		this.configuration = configuration;
        this.systemComponenet = systemComponent;
	}

	/**
	 * @return the configuration
	 */
	public MResultConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(final MResultConfiguration configuration) {
		this.configuration = configuration;
	}

    public Source getSystemComponenet() {
        return systemComponenet;
    }

    public void setSystemComponenet(final Source systemComponenet) {
        this.systemComponenet = systemComponenet;
    }
}
