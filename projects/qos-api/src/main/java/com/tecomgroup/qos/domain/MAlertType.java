/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;

/**
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MAlertType extends MAbstractEntity {

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

	public enum PerceivedSeverity {
		// Don't change the order of the enum values. The order is used in
		// {@link AlertSeverityToolbar}. Otherwise the order of severities will
		// be changed in the GUI.
		CRITICAL(6), MAJOR(5), WARNING(4), MINOR(3), NOTICE(2), INDETERMINATE(1);

		private int severity;

		private PerceivedSeverity(final int severity) {
			this.severity = severity;
		}

		public static PerceivedSeverity getByOrdinal(int ordinal) {
			if(ordinal<0 || ordinal>=PerceivedSeverity.values().length)
			{
				return INDETERMINATE;
			}
			else{
				return PerceivedSeverity.values()[ordinal];
			}
		}

		public int getID()
		{
			return severity;
		}

		public boolean ge(final PerceivedSeverity perceivedSeverity) {
			return perceivedSeverity == null
					|| severity >= perceivedSeverity.severity;
		}

		public boolean greater(final PerceivedSeverity perceivedSeverity) {
			return perceivedSeverity == null
					|| severity > perceivedSeverity.severity;
		}

		public boolean le(final PerceivedSeverity perceivedSeverity) {
			return perceivedSeverity != null
					&& severity <= perceivedSeverity.severity;
		}

		public boolean less(final PerceivedSeverity perceivedSeverity) {
			return perceivedSeverity != null
					&& severity < perceivedSeverity.severity;
		}
	}

	public enum ProbableCause {
		ADAPTER_ERROR,
		APPLICATION_SUBSYSTEM_FAILURE,
		BANDWIDTH_REDUCED,
		CALL_ESTABLISHMENT_ERROR,
		COMMUNICATIONS_PROTOCAL_ERROR,
		COMMUNICATIONS_SUBSYSTEM_FAILURE,
		CONFIGURATION_OR_CUSTOMIZATION_ERROR,
		CONGESTION,
		CORRUPT_DATA,
		CPU_CYCLES_LIMIT_EXCEEDED,
		DATASET_OR_MODEM_ERROR,
		DEGRADED_SIGNAL,
		DTE_DCE_INTERFACE_ERROR,
		ENCLOSURE_DOOR_PROBLEM,
		EQUIPMENT_MALFUNCTION, EXCESSIVE_VIBRATION, FILE_ERROR, FIRE_DETECTED, FLOOD_DETECTED, FRAMING_ERROR,
		HEATING_OR_VENTILATION_OR_COOLING_SYSTEM_PROBLEM, HUMIDITY_UNACCEPTABLE, INPUT_OUTPUT_DEVICE_ERROR,
		INPUT_DEVICE_ERROR, LAN_ERROR, LEAK_DETECTED, LOCAL_NODE_TRANSMISSION_ERROR, LOSS_OF_FRAME, LOSS_OF_SIGNAL,
		MATERIAL_SUPPLY_EXHAUSTED, MULTIPLEXER_PROBLEM, OUT_OF_MEMORY, OUTPUT_DEVICE_ERROR, PERFORMANCE_DEGRATED,
		POWER_PROBLEM, PRESSURE_UNACCEPTABLE, PROCESSOR_PROBLEM, PUMP_FAILURE, QUEUE_SIZE_EXCEEDED, RECEIVE_FAILURE,
		RECEIVER_FAILURE, REMOTE_NODE_TRANSMISSION_ERROR, RESOURCE_AT_OR_NEARING_CAPACITY, RESPONSE_TIME_EXCESSIVE,
		RETRANSMISSION_RATE_EXCESSIVE, SOFTWARE_ERROR, SOFTWARE_PROGRAM_ABNORMALLY_TERMINATED, SOFTWARE_PROGRAM_ERROR,
		STORAGE_CAPACITY_PROBLEM, TEMPERATURE_UNACCEPTABLE, THRESHOLD_CROSSED, TIMING_PROBLEM, TOXIC_LEAK_DETECTED,
		TRANSMIT_FAILURE, TRANSMITTER_FAILURE, UNDERLYING_RESOURCE_UNAVAILABLE, VERSION_MISMATCH;
	}

	public enum SpecificReason {
		NONE, UNKNOWN;

		public static SpecificReason getByString(String status) {
			if ("NONE".equals(status)) {
				return NONE;
			} else
				return UNKNOWN;
			}
	}

	public static enum Status {
		ACTIVE, CLEARED, NONE;
		public static Status getByString(String status) {
			if ("ACTIVE".equals(status)) {
				return ACTIVE;
			} else if ("CLEARED".equals(status)) {
				return CLEARED;
			} else {
				return NONE;
			}
		}
	}

	public enum UpdateType {
		NEW, REPEAT, UPDATE, SEVERITY_UPGRADE, SEVERITY_DEGRADATION, AUTO_CLEARED, ACK, UNACK, OPERATOR_CLEARED, OPERATOR_DELETED, COMMENT, AGENT_RESTART;

		public boolean isCleared() {
			return this == AUTO_CLEARED || this == OPERATOR_CLEARED
					|| this == OPERATOR_DELETED;
		}

		public boolean isSeverityChanged() {
			return this == SEVERITY_UPGRADE || this == SEVERITY_DEGRADATION;
		}

		public static UpdateType getByString(String status) {
			if ("NEW".equals(status)) {
				return NEW;
			} else if ("REPEAT".equals(status)) {
				return REPEAT;
			} else if ("UPDATE".equals(status)) {
				return UPDATE;
			} else if ("SEVERITY_UPGRADE".equals(status)) {
				return SEVERITY_UPGRADE;
			} else if ("SEVERITY_DEGRADATION".equals(status)) {
				return SEVERITY_DEGRADATION;
			} else if ("AUTO_CLEARED".equals(status)) {
				return AUTO_CLEARED;
			} else if ("ACK".equals(status)) {
				return ACK;
			} else if ("UNACK".equals(status)) {
				return UNACK;
			} else if ("OPERATOR_CLEARED".equals(status)) {
				return OPERATOR_CLEARED;
			} else if ("OPERATOR_DELETED".equals(status)) {
				return OPERATOR_DELETED;
			} else if ("COMMENT".equals(status)) {
				return COMMENT;
			} else if ("AGENT_RESTART".equals(status)) {
				return AGENT_RESTART;
			} else {
				return NEW;
			}
		}
    }

	/**
	 * @uml.property name="name"
	 */
	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * @uml.property name="probableCause"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ProbableCause probableCause;

	/**
	 * @uml.property name="displayName"
	 */
	@Column(nullable = false)
	private String displayName;

	/**
	 * @uml.property name="displayTemplate"
	 */
	private String displayTemplate;

	/**
	 * @uml.property name="description"
	 */
	@Column(length = 1024)
	private String description;

	public MAlertType() {
		super();
	}

	public MAlertType(final String alertTypeName) {
		this();
		setName(alertTypeName);
	}

	public MAlertType(final String alertTypeName, final String displayName,
			final ProbableCause probableCause) {
		this(alertTypeName);
		setDisplayName(displayName);
		setProbableCause(probableCause);
	}

	/**
	 * Getter of the property <tt>description</tt>
	 * 
	 * @return Returns the description.
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Getter of the property <tt>displayName</tt>
	 * 
	 * @return Returns the displayName.
	 * @uml.property name="displayName"
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Getter of the property <tt>displayTemplate</tt>
	 * 
	 * @return Returns the displayTemplate.
	 * @uml.property name="displayTemplate"
	 */
	public String getDisplayTemplate() {
		return displayTemplate;
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter of the property <tt>probableCause</tt>
	 * 
	 * @return Returns the probableCause.
	 * @uml.property name="probableCause"
	 */
	public ProbableCause getProbableCause() {
		return probableCause;
	}

	/**
	 * Setter of the property <tt>description</tt>
	 * 
	 * @param description
	 *            The description to set.
	 * @uml.property name="description"
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Setter of the property <tt>displayName</tt>
	 * 
	 * @param displayName
	 *            The displayName to set.
	 * @uml.property name="displayName"
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Setter of the property <tt>displayTemplate</tt>
	 * 
	 * @param displayTemplate
	 *            The displayTemplate to set.
	 * @uml.property name="displayTemplate"
	 */
	public void setDisplayTemplate(final String displayTemplate) {
		this.displayTemplate = displayTemplate;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="name"
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Setter of the property <tt>probableCause</tt>
	 * 
	 * @param probableCause
	 *            The probableCause to set.
	 * @uml.property name="probableCause"
	 */
	public void setProbableCause(final ProbableCause probableCause) {
		this.probableCause = probableCause;
	}

	@Override
	public String toString() {
		return "{name=" + getName() + "}";
	}

}
