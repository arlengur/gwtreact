/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.snmp4j.agent.mo.snmp.DateAndTime;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import com.tecomgroup.qos.messages.DefaultPolicyValidationMessages;
import com.tecomgroup.qos.snmp.mib.QligentVisionMib;
import com.tecomgroup.qos.snmp.mib.generated.tc.IANAItuProbableCause;
import com.tecomgroup.qos.snmp.mib.generated.tc.ItuPerceivedSeverity;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.VisionAlarmUpdateType;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.VisionResultParameterType;
import com.tecomgroup.qos.util.JSEvaluator;
import com.tecomgroup.qos.util.PolicyUtils;

import static com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * Contains utility methods for converting QoS domain model objects to SNMP4J
 * variables.
 * 
 * @author novohatskiy.r
 * 
 */
public class EntityConverter {

	private final static int MILLISECONDS_IN_CENTISECOND = 10;
	private final static long UNSIGNED_INTEGER_MAX_VALUE = 4294967295L;

	private static void addVariableIfNotNull(final Date variable,
			final Variable[] destination, final int index) {
		if (variable != null) {
			final GregorianCalendar variableCalendar = new GregorianCalendar();
			variableCalendar.setTime(variable);
			destination[index] = DateAndTime.makeDateAndTime(variableCalendar);
		}
	}

	private static void addVariableIfNotNull(final Double variable,
			final Variable[] destination, final int index) {
		if (variable != null) {
			destination[index] = new OctetString(Long.toHexString(Double
					.doubleToLongBits(variable)));
		}
	}

	private static void addVariableIfNotNull(final String variable,
			final Variable[] destination, final int index) {
		if (variable != null) {
			destination[index] = new OctetString(variable);
		}
	}

	private static int convertAlarmUpdateType(final UpdateType updateType) {
		int result;

		switch (updateType) {
			case NEW :
				result = VisionAlarmUpdateType._new;
				break;
			case REPEAT :
				result = VisionAlarmUpdateType.repeat;
				break;
			case UPDATE :
				result = VisionAlarmUpdateType.update;
				break;
			case SEVERITY_UPGRADE :
				result = VisionAlarmUpdateType.severityUpgrade;
				break;
			case SEVERITY_DEGRADATION :
				result = VisionAlarmUpdateType.severityDegradation;
				break;
			case AUTO_CLEARED :
				result = VisionAlarmUpdateType.autoCleared;
				break;
			case ACK :
				result = VisionAlarmUpdateType.ack;
				break;
			case UNACK :
				result = VisionAlarmUpdateType.unack;
				break;
			case OPERATOR_CLEARED :
				result = VisionAlarmUpdateType.operatorCleared;
				break;
			case OPERATOR_DELETED :
				result = VisionAlarmUpdateType.operatorDeleted;
				break;
			case COMMENT :
				result = VisionAlarmUpdateType.comment;
				break;
			case AGENT_RESTART :
				result = VisionAlarmUpdateType.agentRestart;
				break;
			default :
				result = VisionAlarmUpdateType._new;
		}

		return result;
	}

	private static int convertPerceivedSeverityToItuStandard(
			final PerceivedSeverity perceivedSeverity) {
		int result;

		switch (perceivedSeverity) {
			case WARNING :
				result = ItuPerceivedSeverity.warning;
				break;
			case MINOR :
				result = ItuPerceivedSeverity.minor;
				break;
			case MAJOR :
				result = ItuPerceivedSeverity.major;
				break;
			case CRITICAL :
				result = ItuPerceivedSeverity.critical;
				break;
			default :
				result = ItuPerceivedSeverity.indeterminate;
		}

		return result;
	}

	private static int convertProbableCauseToItuStandard(
			final ProbableCause probableCause) {
		int result;

		switch (probableCause) {
			case ADAPTER_ERROR :
				result = IANAItuProbableCause.adapterError;
				break;
			case APPLICATION_SUBSYSTEM_FAILURE :
				result = IANAItuProbableCause.applicationSubsystemFailure;
				break;
			case BANDWIDTH_REDUCED :
				result = IANAItuProbableCause.bandwidthReduced;
				break;
			case CALL_ESTABLISHMENT_ERROR :
				result = IANAItuProbableCause.callEstablishmentError;
				break;
			case COMMUNICATIONS_PROTOCAL_ERROR :
				result = IANAItuProbableCause.communicationsProtocolError;
				break;
			case COMMUNICATIONS_SUBSYSTEM_FAILURE :
				result = IANAItuProbableCause.communicationsSubsystemFailure;
				break;
			case CONFIGURATION_OR_CUSTOMIZATION_ERROR :
				result = IANAItuProbableCause.configurationOrCustomizationError;
				break;
			case CONGESTION :
				result = IANAItuProbableCause.congestion;
				break;
			case CORRUPT_DATA :
				result = IANAItuProbableCause.corruptData;
				break;
			case CPU_CYCLES_LIMIT_EXCEEDED :
				result = IANAItuProbableCause.cpuCyclesLimitExceeded;
				break;
			case DATASET_OR_MODEM_ERROR :
				result = IANAItuProbableCause.dataSetOrModemError;
				break;
			case DEGRADED_SIGNAL :
				result = IANAItuProbableCause.degradedSignal;
				break;
			case DTE_DCE_INTERFACE_ERROR :
				result = IANAItuProbableCause.dteDceInterfaceError;
				break;
			case ENCLOSURE_DOOR_PROBLEM :
				result = IANAItuProbableCause.enclosureDoorOpen;
				break;
			case EQUIPMENT_MALFUNCTION :
				result = IANAItuProbableCause.equipmentMalfunction;
				break;
			case EXCESSIVE_VIBRATION :
				result = IANAItuProbableCause.excessiveVibration;
				break;
			case FILE_ERROR :
				result = IANAItuProbableCause.fileError;
				break;
			case FIRE_DETECTED :
				result = IANAItuProbableCause.fireDetected;
				break;
			case FLOOD_DETECTED :
				result = IANAItuProbableCause.flood;
				break;
			case FRAMING_ERROR :
				result = IANAItuProbableCause.framingError;
				break;
			case HEATING_OR_VENTILATION_OR_COOLING_SYSTEM_PROBLEM :
				result = IANAItuProbableCause.heatingVentCoolingSystemProblem;
				break;
			case HUMIDITY_UNACCEPTABLE :
				result = IANAItuProbableCause.humidityUnacceptable;
				break;
			case INPUT_OUTPUT_DEVICE_ERROR :
				result = IANAItuProbableCause.inputOutputDeviceError;
				break;
			case INPUT_DEVICE_ERROR :
				result = IANAItuProbableCause.inputDeviceError;
				break;
			case LAN_ERROR :
				result = IANAItuProbableCause.lanError;
				break;
			case LEAK_DETECTED :
				result = IANAItuProbableCause.leakDetected;
				break;
			case LOCAL_NODE_TRANSMISSION_ERROR :
				result = IANAItuProbableCause.localNodeTransmissionError;
				break;
			case LOSS_OF_FRAME :
				result = IANAItuProbableCause.lossOfFrame;
				break;
			case LOSS_OF_SIGNAL :
				result = IANAItuProbableCause.lossOfSignal;
				break;
			case MATERIAL_SUPPLY_EXHAUSTED :
				result = IANAItuProbableCause.materialSupplyExhausted;
				break;
			case MULTIPLEXER_PROBLEM :
				result = IANAItuProbableCause.multiplexerProblem;
				break;
			case OUT_OF_MEMORY :
				result = IANAItuProbableCause.outOfMemory;
				break;
			case OUTPUT_DEVICE_ERROR :
				result = IANAItuProbableCause.ouputDeviceError;
				break;
			case PERFORMANCE_DEGRATED :
				result = IANAItuProbableCause.performanceDegraded;
				break;
			case POWER_PROBLEM :
				result = IANAItuProbableCause.powerProblem;
				break;
			case PRESSURE_UNACCEPTABLE :
				result = IANAItuProbableCause.pressureUnacceptable;
				break;
			case PROCESSOR_PROBLEM :
				result = IANAItuProbableCause.processorProblem;
				break;
			case PUMP_FAILURE :
				result = IANAItuProbableCause.pumpFailure;
				break;
			case QUEUE_SIZE_EXCEEDED :
				result = IANAItuProbableCause.queueSizeExceeded;
				break;
			case RECEIVE_FAILURE :
				result = IANAItuProbableCause.receiveFailure;
				break;
			case RECEIVER_FAILURE :
				result = IANAItuProbableCause.receiverFailure;
				break;
			case REMOTE_NODE_TRANSMISSION_ERROR :
				result = IANAItuProbableCause.remoteNodeTransmissionError;
				break;
			case RESOURCE_AT_OR_NEARING_CAPACITY :
				result = IANAItuProbableCause.resourceAtOrNearingCapacity;
				break;
			case RESPONSE_TIME_EXCESSIVE :
				result = IANAItuProbableCause.responseTimeExecessive;
				break;
			case RETRANSMISSION_RATE_EXCESSIVE :
				result = IANAItuProbableCause.retransmissionRateExcessive;
				break;
			case SOFTWARE_ERROR :
				result = IANAItuProbableCause.softwareError;
				break;
			case SOFTWARE_PROGRAM_ABNORMALLY_TERMINATED :
				result = IANAItuProbableCause.softwareProgramAbnormallyTerminated;
				break;
			case SOFTWARE_PROGRAM_ERROR :
				result = IANAItuProbableCause.softwareProgramError;
				break;
			case STORAGE_CAPACITY_PROBLEM :
				result = IANAItuProbableCause.storageCapacityProblem;
				break;
			case TEMPERATURE_UNACCEPTABLE :
				result = IANAItuProbableCause.temperatureUnacceptable;
				break;
			case THRESHOLD_CROSSED :
				result = IANAItuProbableCause.thresholdCrossed;
				break;
			case TIMING_PROBLEM :
				result = IANAItuProbableCause.timingProblem;
				break;
			case TOXIC_LEAK_DETECTED :
				result = IANAItuProbableCause.toxicLeakDetected;
				break;
			case TRANSMIT_FAILURE :
				result = IANAItuProbableCause.transmitFailure;
				break;
			case TRANSMITTER_FAILURE :
				result = IANAItuProbableCause.transmitterFailure;
				break;
			case UNDERLYING_RESOURCE_UNAVAILABLE :
				result = IANAItuProbableCause.underlyingResourceUnavailable;
				break;
			case VERSION_MISMATCH :
				result = IANAItuProbableCause.versionMismatch;
				break;
			default :
				result = 0;
		}

		return result;
	}

	private static int convertResultParameterType(
			final ParameterType parameterType) {
		int result;

		switch (parameterType) {
			case LEVEL :
				result = VisionResultParameterType.level;
				break;
			case COUNTER :
				result = VisionResultParameterType.counter;
				break;
			case PERCENTAGE :
				result = VisionResultParameterType.percentage;
				break;
			case BOOL :
				result = VisionResultParameterType.bool;
				break;
			case PROPERTY :
				result = VisionResultParameterType.property;
				break;
			default :
				result = 0;
		}

		return result;
	}

	public static OID convertToOID(final Pair<Integer, Integer> index) {
		return new OID(new int[]{index.getLeft(), index.getRight()});
	}

	public static VariableBinding[] convertToTrapVariableBindings(
			final MAlert alert) {
		final List<VariableBinding> bindings = new ArrayList<VariableBinding>();
		final Variable[] values = convertToVariables(alert);
		for (int i = 0; i < values.length; i++) {
			final Variable currentVariable = values[i];
			if (currentVariable != null) {
				bindings.add(new VariableBinding(new OID(
						QligentVisionMib.oidVisionAlarmEntry).append(i + 1),
						currentVariable));
			}

		}
		bindings.add(new VariableBinding(
				QligentVisionMib.oidTrapVarVisionAlarmIndex, new Integer32(
						alert.getId().intValue())));
		return bindings.toArray(new VariableBinding[bindings.size()]);
	}

	public static Variable[] convertToVariables(final MAlert alert) {
		final Variable[] values = new Variable[QligentVisionMib.VISION_ALARM_TABLE_COLUMN_COUNT];

		values[QligentVisionMib.idxVisionAlarmPerceivedSeverity] = new Integer32(
				convertPerceivedSeverityToItuStandard(alert
						.getPerceivedSeverity()));
		values[QligentVisionMib.idxVisionAlarmProbableCause] = new Integer32(
				convertProbableCauseToItuStandard(alert.getAlertType()
						.getProbableCause()));
		values[QligentVisionMib.idxVisionAlarmSpecificCause] = new OctetString(
				alert.getSpecificReason().name());
		values[QligentVisionMib.idxVisionAlarmDisplayName] = new OctetString(
				alert.getAlertType().getDisplayName());

		addVariableIfNotNull(alert.getAlertType().getDescription(), values,
				QligentVisionMib.idxVisionAlarmDescription);

		final GregorianCalendar creationDateTimeCalendar = new GregorianCalendar();
		creationDateTimeCalendar.setTime(alert.getCreationDateTime());
		values[QligentVisionMib.idxVisionAlarmCreationDateTime] = DateAndTime
				.makeDateAndTime(creationDateTimeCalendar);

		values[QligentVisionMib.idxVisionAlarmLastUpdateType] = new Integer32(
				convertAlarmUpdateType(alert.getLastUpdateType()));

		addVariableIfNotNull(alert.getLastUpdateDateTime(), values,
				QligentVisionMib.idxVisionAlarmLastUpdateDateTime);

		values[QligentVisionMib.idxVisionAlarmAcknowledged] = new Integer32(
				alert.isAcknowledged() ? 1 : 2);

		addVariableIfNotNull(alert.getAcknowledgmentDateTime(), values,
				QligentVisionMib.idxVisionAlarmAcknowledgeDateTime);

		values[QligentVisionMib.idxVisionAlarmDuration] = new TimeTicks(
				getSafeUnsignedInteger(alert.getDuration()
						/ MILLISECONDS_IN_CENTISECOND));
		values[QligentVisionMib.idxVisionAlarmCount] = new Integer32(alert
				.getAlertCount().intValue());

		final Double thresholdValue = PolicyUtils.getAlertThresholdValue(alert,
				JSEvaluator.getInstance(),
				DefaultPolicyValidationMessages.getInstance());
		addVariableIfNotNull(thresholdValue, values,
				QligentVisionMib.idxVisionAlarmThresholdValue);
		addVariableIfNotNull(alert.getDetectionValue(), values,
				QligentVisionMib.idxVisionAlarmDetectionValue);

		values[QligentVisionMib.idxVisionAlarmSourceId] = new Integer32(alert
				.getSource().getSnmpId());

        final MSource source = alert.getOriginator();
        int paramSnmpId = 0;
        if(source instanceof MPolicy) {
            final MPolicy policy = (MPolicy) source;
            final MPolicyCondition condition = policy.getCondition();
            if (condition instanceof MContinuousThresholdFallCondition) {
                final MContinuousThresholdFallCondition fallCondition =
                        (MContinuousThresholdFallCondition) condition;
                final ParameterIdentifier parameterIdentifier =
                        fallCondition.getParameterIdentifier();
                final MSource alertSource = alert.getSource();
                if (alertSource instanceof MAgentTask) {
                    final MAgentTask task = (MAgentTask) alertSource;
                    final MResultConfiguration resultConfiguration = task.getResultConfiguration();
                    if (resultConfiguration != null) {
                        final MResultParameterConfiguration resultParameterConfiguration =
                                resultConfiguration.findParameterConfiguration(parameterIdentifier);
                        if (resultParameterConfiguration != null) {
                            paramSnmpId = resultParameterConfiguration.getSnmpId();
                        }
                    }
                }
            }
        }
        values[QligentVisionMib.idxVisionAlarmParameterId] = new Integer32(paramSnmpId);

		values[QligentVisionMib.idxVisionAlarmOriginatorDisplayName] = new OctetString(
				alert.getOriginator().getDisplayName());

		addVariableIfNotNull(alert.getSettings(), values,
				QligentVisionMib.idxVisionAlarmSettings);


		return values;
	}

	public static Variable[] convertToVariables(final ResultWrapper result) {
		final MResultParameterConfiguration resultParameterConfiguration = result
				.getResultParameterConfiguration();

		final Variable[] values = new Variable[QligentVisionMib.VISION_CURRENT_RESULT_TABLE_COLUMN_COUNT];

        values[QligentVisionMib.idxVisionCurrentResultTaskId] = new Integer32(result.getTaskSnmpId());

		values[QligentVisionMib.idxVisionCurrentResultType] = new Integer32(
				convertResultParameterType(resultParameterConfiguration
						.getType()));

		addVariableIfNotNull(
				resultParameterConfiguration.getParsedDisplayFormat(), values,
				QligentVisionMib.idxVisionCurrentResultDisplayName);
		addVariableIfNotNull(resultParameterConfiguration.getDescription(),
				values, QligentVisionMib.idxVisionCurrentResultDescription);
		addVariableIfNotNull(resultParameterConfiguration.getUnits(), values,
				QligentVisionMib.idxVisionCurrentResultUnits);
		addVariableIfNotNull((Double) result.getValue(), values,
				QligentVisionMib.idxVisionCurrentResultValue);
		addVariableIfNotNull(result.getDateTime(), values,
				QligentVisionMib.idxVisionCurrentResultDateTime);

		return values;
	}

	private static long getSafeUnsignedInteger(final long value) {
		long result = 0;
		if (value > UNSIGNED_INTEGER_MAX_VALUE) {
			result = UNSIGNED_INTEGER_MAX_VALUE;
		} else if (value > 0) {
			result = value;
		}
		return result;
	}

}
