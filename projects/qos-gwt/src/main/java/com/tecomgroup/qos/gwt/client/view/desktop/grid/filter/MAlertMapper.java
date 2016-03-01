/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.filter;

import java.util.logging.Logger;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.Source;

/**
 * @author ivlev.e
 * 
 */
public class MAlertMapper implements EnumMapper {

	public static Logger LOGGER = Logger
			.getLogger(MAlertMapper.class.getName());

	private static volatile MAlertMapper instance;

	public static MAlertMapper getInstance() {
		if (instance == null) {
			synchronized (MAlertMapper.class) {
				if (instance == null) {
					instance = new MAlertMapper();
				}
			}
		}
		return instance;
	}

	private MAlertMapper() {
	}

	@Override
	public Object tryConvertToEnum(final String unqualifiedFiledName,
			final String value) {
		Object instance = null;
		if ("perceivedSeverity".equals(unqualifiedFiledName)) {
			instance = PerceivedSeverity.valueOf(value);
		} else if ("alertType.probableCause".equals(unqualifiedFiledName)) {
			instance = ProbableCause.valueOf(value);
		} else if ("specificReason".equals(unqualifiedFiledName)) {
			instance = SpecificReason.valueOf(value);
		} else if ("source.type".equals(unqualifiedFiledName)
				|| "originator.type".equals(unqualifiedFiledName)) {
			instance = Source.Type.valueOf(value);
		} else if ("status".equals(unqualifiedFiledName)) {
			instance = Status.valueOf(value);
		} else {
			instance = value;
			LOGGER.severe(this.getClass().getName()
					+ " can not map enum value of " + unqualifiedFiledName);
		}
		return instance;
	}

}
