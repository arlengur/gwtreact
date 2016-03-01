/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.messages;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class DefaultPolicyValidationMessages
		implements
			PolicyValidationMessages {

	private volatile static PolicyValidationMessages instance;

	public static PolicyValidationMessages getInstance() {
		if (instance == null) {
			synchronized (DefaultPolicyValidationMessages.class) {
				if (instance == null) {
					instance = new DefaultPolicyValidationMessages();
				}
			}
		}
		return instance;
	}

	@Override
	public String cease() {
		return "Cease";
	}

	@Override
	public String criticalCeaseLevelHigherThanWarningCeaseLevel() {
		return "Critical cease level must be less than or equal to warning cease level";
	}

	@Override
	public String criticalCeaseLevelLessThanWarningCeaseLevel() {
		return "Critical cease level must be greater than or equal to warning cease level";
	}

	@Override
	public String criticalRaiseLevelHigherThanCriticalCeaseLevel() {
		return "Critical raise level must be less than or equal to critical cease level";
	}

	@Override
	public String criticalRaiseLevelHigherThanWarningRaiseLevel() {
		return "Critical raise level must be less than or equal to warning raise level";
	}

	@Override
	public String criticalRaiseLevelLessThanCriticalCeaseLevel() {
		return "Critical raise level must be greater than or equal to critical cease level";
	}

	@Override
	public String criticalRaiseLevelLessThanWarningRaiseLevel() {
		return "Critical raise level must be greater than or equal to warning raise level";
	}

	@Override
	public String levelIsNotCompletelyDefined(final String level) {
		return "Level " + level + " is not completely defined";
	}

	@Override
	public String levelIsNotDefined(final String level) {
		return "Level " + level + " is not defined";
	}

	@Override
	public String levelsAreNotDefined() {
		return "Condition levels are not defined";
	}

	@Override
	public String raise() {
		return "Raise";
	}

	@Override
	public String unableToCreatePolicyWithDeletedSource(final String source) {
		return "Task "
				+ source
				+ " became deleted while creating policy. It is not allowed to create policy for deleted task";
	}

	@Override
	public String unableToCreatePolicyWithDisabledSource(final String source) {
		return "Task "
				+ source
				+ " became disabled while creating policy. It is not allowed to create policy for disabled task";
	}

	@Override
	public String unableToInitConditionLevels() {
		return "Unable to initialize condition levels";
	}

	@Override
	public String unableToUpdatePolicyWithDeletedSource(final String source) {
		return "Task "
				+ source
				+ " became deleted while updating policy. It is not allowed to update policy for deleted task";
	}

	@Override
	public String unableToUpdatePolicyWithDisabledSource(final String source) {
		return "Task "
				+ source
				+ " became disabled while updating policy. It is not allowed to update policy for disabled task";
	}

	@Override
	public String warningRaiseLevelHigherThanWarningCeaseLevel() {
		return "Warning raise level must be less than or equal to warning cease level";
	}

	@Override
	public String warningRaiseLevelLessThanWarningCeaseLevel() {
		return "Warning raise level must be greater than or equal to warning cease level";
	}
}
