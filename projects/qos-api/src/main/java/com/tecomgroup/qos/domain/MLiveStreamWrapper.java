/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MLiveStreamWrapper extends MStreamWrapper {

	public MLiveStreamWrapper() {
		super();
	}

	public MLiveStreamWrapper(final MLiveStreamWrapper streamWrapper) {
		super(streamWrapper);
	}

	/**
	 * @return the stream
	 */
	@Override
	@Transient
	public MLiveStream getStream() {
		return (MLiveStream) super.getStream();
	}
}