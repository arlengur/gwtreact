/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kunilov.p
 * 
 */
@Entity
public class MPolicyConditionLevels extends MPolicyCondition {

	private static final long serialVersionUID = 5527590553079055387L;

	@Embedded
	protected ConditionLevel warningLevel;

	@Embedded
	@Column(nullable = false)
	protected ConditionLevel criticalLevel;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	protected ThresholdType thresholdType;

	public MPolicyConditionLevels() {
		super();
	}

	public MPolicyConditionLevels(final MPolicyConditionLevels conditionLevels) {
		this();
		this.warningLevel = conditionLevels.getWarningLevel();
		this.criticalLevel = conditionLevels.getCriticalLevel();
		this.thresholdType = conditionLevels.getThresholdType();
	}

	@Override
	public MPolicyConditionLevels copy() {
		return new MPolicyConditionLevels(this);
	}

	@Transient
	@JsonIgnore
	public List<ConditionLevel> getConditionLevels() {
		final List<ConditionLevel> conditions = new ArrayList<ConditionLevel>(2);
		if (criticalLevel != null) {
			conditions.add(criticalLevel);
		}
		if (warningLevel != null) {
			conditions.add(warningLevel);
		}
		return conditions;
	}

	@Transient
	@JsonIgnore
	public Map<PerceivedSeverity, ConditionLevel> getConditionLevelsMap() {
		final Map<PerceivedSeverity, ConditionLevel> conditions = new HashMap<PerceivedSeverity, ConditionLevel>(
				2);
		if (criticalLevel != null) {
			conditions.put(PerceivedSeverity.CRITICAL, criticalLevel);
		}
		if (warningLevel != null) {
			conditions.put(PerceivedSeverity.WARNING, warningLevel);
		}
		return conditions;
	}

	/**
	 * @return the criticalLevel
	 */
	public ConditionLevel getCriticalLevel() {
		return criticalLevel;
	}

	/**
	 * @return the thresholdType
	 */
	public ThresholdType getThresholdType() {
		return thresholdType;
	}

	/**
	 * @return the warningLevel
	 */
	public ConditionLevel getWarningLevel() {
		return warningLevel;
	}

	/**
	 * @param criticalLevel
	 *            the criticalLevel to set
	 */
	public void setCriticalLevel(final ConditionLevel criticalLevel) {
		this.criticalLevel = criticalLevel;
	}

	/**
	 * @param thresholdType
	 *            the thresholdType to set
	 */
	public void setThresholdType(final ThresholdType thresholdType) {
		this.thresholdType = thresholdType;
	}

	/**
	 * @param warningLevel
	 *            the warningLevel to set
	 */
	public void setWarningLevel(final ConditionLevel warningLevel) {
		this.warningLevel = warningLevel;
	}

	@Override
	public boolean updateSimpleFields(final MPolicyCondition condition) {
		boolean isUpdated = false;

		if (condition instanceof MPolicyConditionLevels) {
			final MPolicyConditionLevels continuousThresholdFallCondition = (MPolicyConditionLevels) condition;

			final ThresholdType newThresholdType = continuousThresholdFallCondition
					.getThresholdType();
			if (!equals(thresholdType, newThresholdType)) {
				thresholdType = newThresholdType;
				isUpdated = true;
			}

			final ConditionLevel newWarningLevel = continuousThresholdFallCondition
					.getWarningLevel();
			if (warningLevel != null && newWarningLevel != null) {
				isUpdated |= warningLevel.updateSimpleFields(newWarningLevel);
			} else if (warningLevel != null ^ newWarningLevel != null) {
				warningLevel = newWarningLevel;
				isUpdated = true;
			}

			final ConditionLevel newCriticalLevel = continuousThresholdFallCondition
					.getCriticalLevel();
			if (criticalLevel != null && newCriticalLevel != null) {
				isUpdated |= criticalLevel.updateSimpleFields(newCriticalLevel);
			} else if (criticalLevel != null ^ newCriticalLevel != null) {
				criticalLevel = newCriticalLevel;
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}
