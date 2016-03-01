/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Embeddable;

import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class AccumulatedLevel extends ConditionLevel {

	private Long analysisInterval;

	public AccumulatedLevel() {
		super();
	}

	public AccumulatedLevel(final String raiseLevel, final Long raiseDuration,
			final String ceauseLevel, final Long ceaseDuration,
			final Long analysisInterval) {
		super(raiseLevel, raiseDuration, ceauseLevel, ceaseDuration);
		this.analysisInterval = analysisInterval;
	}

	@Override
	public ConditionLevel copy() {
		return new AccumulatedLevel(getRaiseLevel(), getRaiseDuration(),
				getCeaseLevel(), getCeaseDuration(), getAnalysisInterval());
	}

	public Long getAnalysisInterval() {
		return analysisInterval;
	}

	public void setAnalysisInterval(final Long analysisInterval) {
		this.analysisInterval = analysisInterval;
	}

	@Override
	public String toString() {
		return "{raiseLeve = " + raiseLevel + ", ceaseLevel = " + ceaseLevel
				+ ", analysisInterval = " + analysisInterval + "}";
	}

	@Override
	public boolean updateSimpleFields(final ConditionLevel conditionLevel) {
		boolean isUpdated = super.updateSimpleFields(conditionLevel);

		if (conditionLevel instanceof AccumulatedLevel) {
			final AccumulatedLevel accumulatedLevel = (AccumulatedLevel) conditionLevel;

			if (!MAbstractEntity.equals(analysisInterval,
					accumulatedLevel.getAnalysisInterval())) {
				analysisInterval = accumulatedLevel.getAnalysisInterval();
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}
