/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author ivlev.e
 * 
 */
public class ContinuousThresholdValueProvider
		implements
			ValueProvider<MPolicy, String> {

	public static enum PropertyType {
		PARAMETER, WARN_RAISE_VAL, WARN_CEASE_VAL, WARN_RAISE_DUR, WARN_CEASE_DUR, CRIT_RAISE_VAL, CRIT_CEASE_VAL, CRIT_RAISE_DUR, CRIT_CEASE_DUR
	}

	private final PropertyType type;

	public ContinuousThresholdValueProvider(final PropertyType type) {
		this.type = type;
	}

	@Override
	public String getPath() {
		return type.toString();
	}

	/**
	 * @return the type
	 */
	public PropertyType getType() {
		return type;
	}

	@Override
	public String getValue(final MPolicy object) {
		String result = "";
		if (object.getCondition() instanceof MContinuousThresholdFallCondition) {
			final MContinuousThresholdFallCondition condition = (MContinuousThresholdFallCondition) object
					.getCondition();
			result = provideValue(condition, type);
		}
		return result;
	}
	private String provideValue(
			final MContinuousThresholdFallCondition condition,
			final PropertyType prop) {
		String val = "";
		final ConditionLevel criticalLevel = condition.getCriticalLevel();
		final ConditionLevel warninglLevel = condition.getWarningLevel();
		switch (prop) {
			case PARAMETER :
				val = condition.getParameterIdentifier() == null
						? null
						: condition.getParameterIdentifier().getName();
				break;
			case WARN_RAISE_VAL :
				val = warninglLevel == null ? null : warninglLevel
						.getRaiseLevel();
				break;
			case WARN_CEASE_VAL :
				val = warninglLevel == null ? null : warninglLevel
						.getCeaseLevel();
				break;
			case WARN_RAISE_DUR :
				val = (warninglLevel == null || warninglLevel
						.getRaiseDuration() == null) ? null : warninglLevel
						.getRaiseDuration().toString();
				break;
			case WARN_CEASE_DUR :
				val = (warninglLevel == null || warninglLevel
						.getCeaseDuration() == null) ? null : warninglLevel
						.getCeaseDuration().toString();
				break;
			case CRIT_RAISE_VAL :
				val = criticalLevel == null ? null : criticalLevel
						.getRaiseLevel();
				break;
			case CRIT_CEASE_VAL :
				val = criticalLevel == null ? null : criticalLevel
						.getCeaseLevel();
				break;
			case CRIT_RAISE_DUR :
				val = (criticalLevel == null || criticalLevel
						.getRaiseDuration() == null) ? null : criticalLevel
						.getRaiseDuration().toString();
				break;
			case CRIT_CEASE_DUR :
				val = (criticalLevel == null || criticalLevel
						.getCeaseDuration() == null) ? null : criticalLevel
						.getCeaseDuration().toString();
				break;
			default :
				break;
		}
		return val;
	}

	@Override
	public void setValue(final MPolicy object, final String value) {

	}
}
