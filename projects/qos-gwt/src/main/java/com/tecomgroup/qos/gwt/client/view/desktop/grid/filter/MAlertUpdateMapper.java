/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid.filter;

import java.util.logging.Logger;

import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.Source;

/**
 * @author ivlev.e
 * 
 */
public class MAlertUpdateMapper implements EnumMapper {

	public static Logger LOGGER = Logger.getLogger(MAlertUpdateMapper.class
			.getName());

	private static volatile MAlertUpdateMapper instance;

	public static MAlertUpdateMapper getInstance() {
		if (instance == null) {
			synchronized (MAlertUpdateMapper.class) {
				if (instance == null) {
					instance = new MAlertUpdateMapper();
				}
			}
		}
		return instance;
	}

	private MAlertUpdateMapper() {
	}

	@Override
	public Object tryConvertToEnum(final String unqualifiedFiledName,
			final String value) {
		Object instance = null;
		if ("updateType".equals(unqualifiedFiledName)) {
			instance = UpdateType.valueOf(value);
		} else if ("alert.source.type".equals(unqualifiedFiledName)) {
			instance = Source.Type.valueOf(value);
		} else {
			instance = value;
			LOGGER.severe(this.getClass().getName()
					+ " can not map enum value of " + unqualifiedFiledName);
		}
		return instance;
	}

}
