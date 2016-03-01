/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * @author abondin
 * 
 */
@Entity(name = "MAccumulatedFallCondition")
@SuppressWarnings("serial")
public class MAccumulatedThresholdFallCondition
		extends
			MContinuousThresholdFallCondition {

	@Embedded
	@Column(nullable = false)
	private AccumulatedLevel criticalLevel;

	public MAccumulatedThresholdFallCondition() {
		super();
	}

	public MAccumulatedThresholdFallCondition(
			final MAccumulatedThresholdFallCondition condition) {
		super(condition);
		this.criticalLevel = condition.getCriticalLevel();
	}

	@Override
	public MAccumulatedThresholdFallCondition copy() {
		return new MAccumulatedThresholdFallCondition(this);
	}

	/**
	 * @return the criticalLevel
	 */
	@Override
	public AccumulatedLevel getCriticalLevel() {
		return criticalLevel;
	}

	/**
	 * @param criticalLevel
	 *            the criticalLevel to set
	 */
	public void setCriticalLevel(final AccumulatedLevel criticalLevel) {
		this.criticalLevel = criticalLevel;
	}

	@Override
	public boolean updateSimpleFields(final MPolicyCondition condition) {
		boolean isUpdated = super.updateSimpleFields(condition);

		if (condition instanceof MAccumulatedThresholdFallCondition) {
			final MAccumulatedThresholdFallCondition accumulatedThresholdFallCondition = (MAccumulatedThresholdFallCondition) condition;

			final AccumulatedLevel newCriticalLevel = accumulatedThresholdFallCondition
					.getCriticalLevel();
			if (criticalLevel != null) {
				isUpdated |= criticalLevel.updateSimpleFields(newCriticalLevel);
			} else if (newCriticalLevel != null) {
				criticalLevel = newCriticalLevel;
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}
