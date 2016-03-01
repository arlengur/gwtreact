/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.List;

import javax.persistence.MappedSuperclass;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class MStreamTemplate extends MUserAbstractTemplate {

	public enum MediaTemplateType implements TemplateType {
		RECORDED_VIDEO(MRecordedStreamTemplate.class.getName()), LIVE_VIDEO(
				MLiveStreamTemplate.class.getName());

		private final String className;

		private MediaTemplateType(final String className) {
			this.className = className;
		}

		@Override
		public String getTemplateClassName() {
			return className;
		}

	}

	public MStreamTemplate() {
		super();
	}

	public MStreamTemplate(final MStreamTemplate streamTemplate) {
		super(streamTemplate);
	}

	public MStreamTemplate(final String name) {
		super(name);
	}

	/**
	 * 
	 * @return
	 */
	public abstract List<? extends MStreamWrapper> getWrappers();

	/**
	 * 
	 * @param wrappers
	 */
	public abstract void setWrappers(List<? extends MStreamWrapper> wrappers);
}
