/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.messages;

import java.io.Serializable;

/**
 * @author kunilov.p
 * 
 */
public interface PolicyValidationMessages extends Serializable {

	String cease();

	String criticalCeaseLevelHigherThanWarningCeaseLevel();

	String criticalCeaseLevelLessThanWarningCeaseLevel();

	String criticalRaiseLevelHigherThanCriticalCeaseLevel();

	String criticalRaiseLevelHigherThanWarningRaiseLevel();

	String criticalRaiseLevelLessThanCriticalCeaseLevel();

	String criticalRaiseLevelLessThanWarningRaiseLevel();

	String levelIsNotCompletelyDefined(String level);

	String levelIsNotDefined(String level);

	String levelsAreNotDefined();

	String raise();

	String unableToCreatePolicyWithDeletedSource(String source);

	String unableToCreatePolicyWithDisabledSource(String source);

	String unableToInitConditionLevels();

	String unableToUpdatePolicyWithDeletedSource(String source);

	String unableToUpdatePolicyWithDisabledSource(String source);

	String warningRaiseLevelHigherThanWarningCeaseLevel();

	String warningRaiseLevelLessThanWarningCeaseLevel();

}
