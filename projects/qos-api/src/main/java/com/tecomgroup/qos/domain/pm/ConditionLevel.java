/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.MAbstractEntity;

@SuppressWarnings("serial")
@Embeddable
public class ConditionLevel
		implements
			Serializable,
			UpdatableEntity<ConditionLevel> {
	public static final String THRESHOLD_CRITICAL_LEVEL = "thresholdCriticalLevel";
	public static final String THRESHOLD_WARNING_LEVEL = "thresholdWarningLevel";

	@JsonIgnore
	@Transient
	private Double raiseLevelDouble;

	@JsonIgnore
	@Transient
	private Double ceaseLevelDouble;

	protected String raiseLevel;

	protected Long raiseDuration;

	protected String ceaseLevel;

	protected Long ceaseDuration;

	public ConditionLevel() {
		super();
	}

	public ConditionLevel(final String raiseLevel, final Long raiseDuration,
			final String ceauseLevel, final Long ceaseDuration) {
		this();
		this.raiseDuration = raiseDuration;
		this.raiseLevel = raiseLevel;
		this.ceaseDuration = ceaseDuration;
		this.ceaseLevel = ceauseLevel;
	}

	public ConditionLevel copy() {
		return new ConditionLevel(getRaiseLevel(), getRaiseDuration(),
				getCeaseLevel(), getCeaseDuration());
	}

	public Long getCeaseDuration() {
		return ceaseDuration;
	}

	public String getCeaseLevel() {
		return ceaseLevel;
	}

	public Double getCeaseLevelDouble() {
		return ceaseLevelDouble;
	}

	public Long getRaiseDuration() {
		return raiseDuration;
	}

	public String getRaiseLevel() {
		return raiseLevel;
	}

	public Double getRaiseLevelDouble() {
		return raiseLevelDouble;
	}

	public void setCeaseDuration(final Long ceaseDuration) {
		this.ceaseDuration = ceaseDuration;
	}

	public void setCeaseLevel(final String ceaseLevel) {
		this.ceaseLevel = ceaseLevel;
	}

	public void setCeaseLevelDouble(final Double ceaseLevelDouble) {
		this.ceaseLevelDouble = ceaseLevelDouble;
	}

	public void setRaiseDuration(final Long raiseDuration) {
		this.raiseDuration = raiseDuration;
	}

	public void setRaiseLevel(final String raiseLevel) {
		this.raiseLevel = raiseLevel;
	}

	public void setRaiseLevelDouble(final Double raiseLevelDouble) {
		this.raiseLevelDouble = raiseLevelDouble;
	}

	@Override
	public String toString() {
		return "{raiseLevel = " + raiseLevel + ", ceaseLevel = " + ceaseLevel
				+ "}";
	}

	@Override
	public boolean updateSimpleFields(final ConditionLevel conditionLevel) {
		boolean isUpdated = false;

		if (conditionLevel != null) {
			if (!MAbstractEntity.equals(raiseLevel,
					conditionLevel.getRaiseLevel())) {
				raiseLevel = conditionLevel.getRaiseLevel();
				isUpdated = true;
			}
			if (!MAbstractEntity.equals(raiseDuration,
					conditionLevel.getRaiseDuration())) {
				raiseDuration = conditionLevel.getRaiseDuration();
				isUpdated = true;
			}
			if (!MAbstractEntity.equals(ceaseLevel,
					conditionLevel.getCeaseLevel())) {
				ceaseLevel = conditionLevel.getCeaseLevel();
				isUpdated = true;
			}
			if (!MAbstractEntity.equals(ceaseDuration,
					conditionLevel.getCeaseDuration())) {
				ceaseDuration = conditionLevel.getCeaseDuration();
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}