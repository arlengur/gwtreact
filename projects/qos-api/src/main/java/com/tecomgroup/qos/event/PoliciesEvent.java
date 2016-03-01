/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import java.util.List;

import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author sviyazov.a
 * 
 */
@SuppressWarnings("serial")
public class PoliciesEvent extends AbstractEvent {

	private Source source;

	private List<MPolicy> policies;

	private Source systemComponent;

	public PoliciesEvent() {
		super();
	}

	public PoliciesEvent(final Source source, final Source systemComponent,
			final List<MPolicy> policies, final EventType eventType) {
		super(eventType);
		this.source = source;
		this.policies = policies;
		this.systemComponent = systemComponent;
	}

	public List<MPolicy> getPolicies() {
		return policies;
	}

	public Source getSource() {
		return source;
	}

	public Source getSystemComponent() {
		return systemComponent;
	}

	public void setPolicies(final List<MPolicy> policies) {
		this.policies = policies;
	}

	public void setSource(final Source source) {
		this.source = source;
	}

	public void setSystemComponent(final Source systemComponent) {
		this.systemComponent = systemComponent;
	}
}
