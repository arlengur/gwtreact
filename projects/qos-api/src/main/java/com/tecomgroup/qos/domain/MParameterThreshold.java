/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.*;

import com.tecomgroup.qos.UpdatableEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MParameterThreshold extends MAbstractEntity
		implements
			UpdatableEntity<MParameterThreshold> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static enum ThresholdType {
		NONE("NONE"), LESS("<"), GREATER(">"), EQUALS("="), NOT_EQUALS("!="), LESS_OR_EQUALS(
				"<="), GREATER_OR_EQUALS(">=");

		private final String operation;
		private ThresholdType(final String operation) {
			this.operation = operation;
		}

		public boolean accept(final Double value, final Double threshold) {
			boolean result = false;
			switch (this) {
				case EQUALS : {
					result = value.compareTo(threshold) == 0;
					break;
				}
				case NOT_EQUALS : {
					result = value.compareTo(threshold) != 0;
					break;
				}
				case LESS : {
					result = value < threshold;
					break;
				}
				case GREATER : {
					result = value > threshold;
					break;
				}
				case LESS_OR_EQUALS : {
					result = value <= threshold;
					break;
				}
				case GREATER_OR_EQUALS : {
					result = value >= threshold;
					break;
				}
				default : {
					throw new UnsupportedOperationException(
							"Unsupported threshold type " + this);
				}
			}
			return result;
		}

		public ThresholdType inverse() {
			ThresholdType inverseType = null;
			switch (this) {
				case EQUALS : {
					inverseType = NOT_EQUALS;
					break;
				}
				case NOT_EQUALS : {
					inverseType = EQUALS;
					break;
				}
				case LESS : {
					inverseType = GREATER_OR_EQUALS;
					break;
				}
				case GREATER : {
					inverseType = LESS_OR_EQUALS;
					break;
				}
				case LESS_OR_EQUALS : {
					inverseType = GREATER;
					break;
				}
				case GREATER_OR_EQUALS : {
					inverseType = LESS;
					break;
				}

				default : {
					throw new UnsupportedOperationException(
							"Unsupported threshold type " + this);
				}
			}
			return inverseType;
		}

		@Override
		public String toString() {
			return operation;
		}
	}

	/**
	 * @uml.property name="type"
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ThresholdType type;

	/**
	 * @uml.property name="warninglevel"
	 */
	private Double warningLevel;

	/**
	 * @uml.property name="criticalLevel"
	 */
	private Double criticalLevel;

	public MParameterThreshold() {
		super();
	}

	public MParameterThreshold(final MParameterThreshold threshold) {
		this();
		if (threshold != null) {
			setType(threshold.getType());
			setWarningLevel(threshold.getWarningLevel());
			setCriticalLevel(threshold.getCriticalLevel());
		}
	}

	/**
	 * @param type
	 * @param warningLevel
	 * @param criticalLevel
	 */
	public MParameterThreshold(final ThresholdType type,
			final Double warningLevel, final Double criticalLevel) {
		this();
		this.type = type;
		this.warningLevel = warningLevel;
		this.criticalLevel = criticalLevel;
	}

	/**
	 * @return the criticalLevel
	 */
	public Double getCriticalLevel() {
		return criticalLevel;
	}

	/**
	 * Getter of the property <tt>type</tt>
	 * 
	 * @return Returns the type.
	 * @uml.property name="type"
	 */
	public ThresholdType getType() {
		return type;
	}

	/**
	 * @return the warningLevel
	 */
	public Double getWarningLevel() {
		return warningLevel;
	}

	/**
	 * @param criticalLevel
	 *            the criticalLevel to set
	 */
	public void setCriticalLevel(final Double criticalLevel) {
		this.criticalLevel = criticalLevel;
	}

	/**
	 * Setter of the property <tt>type</tt>
	 * 
	 * @param type
	 *            The type to set.
	 * @uml.property name="type"
	 */
	public void setType(final ThresholdType type) {
		this.type = type;
	}

	/**
	 * @param warningLevel
	 *            the warningLevel to set
	 */
	public void setWarningLevel(final Double warningLevel) {
		this.warningLevel = warningLevel;
	}

	@Override
	public boolean updateSimpleFields(final MParameterThreshold threshold) {
		boolean isUpdated = false;

		if (threshold != null) {
			if (!equals(getType(), threshold.getType())) {
				setType(threshold.getType());
				isUpdated = true;
			}

			if (!equals(getWarningLevel(), threshold.getWarningLevel())) {
				setWarningLevel(threshold.getWarningLevel());
				isUpdated = true;
			}

			if (!equals(getCriticalLevel(), threshold.getCriticalLevel())) {
				setCriticalLevel(threshold.getCriticalLevel());
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}
