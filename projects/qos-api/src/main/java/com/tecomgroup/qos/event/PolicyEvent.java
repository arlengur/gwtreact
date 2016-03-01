/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class PolicyEvent extends AbstractEvent {

	private MPolicy policy;

	private Source systemComponent;

	public PolicyEvent() {
		super();
	}

	/**
     * @param systemComponent
     * @param eventType
     */
	public PolicyEvent(final Source systemComponent, final MPolicy policy,
			final EventType eventType) {
		super(eventType);
		this.systemComponent = systemComponent;
		this.policy = policy;
	}

	/**
	 * @return the policy
	 */
	public MPolicy getPolicy() {
		return policy;
	}

	/**
	 * @return the systemComponent
	 */
	public Source getSystemComponent() {
		return systemComponent;
	}

	/**
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(final MPolicy policy) {
		this.policy = policy;
	}

	/**
     * @param systemComponent
     *            the systemComponent to set
     */
	public void setSystemComponent(final Source systemComponent) {
		this.systemComponent = systemComponent;
	}
}
