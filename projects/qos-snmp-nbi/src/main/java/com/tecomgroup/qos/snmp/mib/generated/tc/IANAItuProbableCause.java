/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mib.generated.tc;

import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.smi.EnumerationConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraintValidator;
import org.snmp4j.agent.mo.snmp.tc.TextualConvention;
import org.snmp4j.smi.AssignableFromLong;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

/**
 * Textual convention IANAItuProbableCause from IANA-ITU-ALARM-TC-MIB.
 * 
 * This class was extracted from a more common generated file.
 * 
 * @generated
 * @author novohatskiy.r
 */
public class IANAItuProbableCause implements TextualConvention<Variable> {

	private static final String TC_IANAITUPROBABLECAUSE = "IANAItuProbableCause";
	private static final String TC_MODULE_IANA_ITU_ALARM_TC_MIB = "IANA-ITU-ALARM-TC-MIB";

	public static final int aIS = 1;
	public static final int callSetUpFailure = 2;
	public static final int degradedSignal = 3;
	public static final int farEndReceiverFailure = 4;
	public static final int framingError = 5;
	public static final int lossOfFrame = 6;
	public static final int lossOfPointer = 7;
	public static final int lossOfSignal = 8;
	public static final int payloadTypeMismatch = 9;
	public static final int transmissionError = 10;
	public static final int remoteAlarmInterface = 11;
	public static final int excessiveBER = 12;
	public static final int pathTraceMismatch = 13;
	public static final int unavailable = 14;
	public static final int signalLabelMismatch = 15;
	public static final int lossOfMultiFrame = 16;
	public static final int receiveFailure = 17;
	public static final int transmitFailure = 18;
	public static final int modulationFailure = 19;
	public static final int demodulationFailure = 20;
	public static final int broadcastChannelFailure = 21;
	public static final int connectionEstablishmentError = 22;
	public static final int invalidMessageReceived = 23;
	public static final int localNodeTransmissionError = 24;
	public static final int remoteNodeTransmissionError = 25;
	/*
	 * --Values 27-50 are reserved for communications alarm related --probable
	 * causes -- The following are used with equipment alarm.
	 */
	public static final int routingFailure = 26;
	public static final int backplaneFailure = 51;
	public static final int dataSetProblem = 52;
	public static final int equipmentIdentifierDuplication = 53;
	public static final int externalIFDeviceProblem = 54;
	public static final int lineCardProblem = 55;
	public static final int multiplexerProblem = 56;
	public static final int nEIdentifierDuplication = 57;
	public static final int powerProblem = 58;
	public static final int processorProblem = 59;
	public static final int protectionPathFailure = 60;
	public static final int receiverFailure = 61;
	public static final int replaceableUnitMissing = 62;
	public static final int replaceableUnitTypeMismatch = 63;
	public static final int synchronizationSourceMismatch = 64;
	public static final int terminalProblem = 65;
	public static final int timingProblem = 66;
	public static final int transmitterFailure = 67;
	public static final int trunkCardProblem = 68;
	public static final int replaceableUnitProblem = 69;
	/*
	 * --An equipment alarm to be issued if the system detects that the --real
	 * time clock has failed
	 */
	public static final int realTimeClockFailure = 70;
	public static final int antennaFailure = 71;
	public static final int batteryChargingFailure = 72;
	public static final int diskFailure = 73;
	public static final int frequencyHoppingFailure = 74;
	public static final int iODeviceError = 75;
	public static final int lossOfSynchronisation = 76;
	public static final int lossOfRedundancy = 77;
	public static final int powerSupplyFailure = 78;
	public static final int signalQualityEvaluationFailure = 79;
	public static final int tranceiverFailure = 80;
	public static final int protectionMechanismFailure = 81;
	/*
	 * -- Values 83-100 are reserved for equipment alarm related probable --
	 * causes -- The following are used with environmental alarm.
	 */
	public static final int protectingResourceFailure = 82;
	public static final int airCompressorFailure = 101;
	public static final int airConditioningFailure = 102;
	public static final int airDryerFailure = 103;
	public static final int batteryDischarging = 104;
	public static final int batteryFailure = 105;
	public static final int commercialPowerFailure = 106;
	public static final int coolingFanFailure = 107;
	public static final int engineFailure = 108;
	public static final int fireDetectorFailure = 109;
	public static final int fuseFailure = 110;
	public static final int generatorFailure = 111;
	public static final int lowBatteryThreshold = 112;
	public static final int pumpFailure = 113;
	public static final int rectifierFailure = 114;
	public static final int rectifierHighVoltage = 115;
	public static final int rectifierLowFVoltage = 116;
	public static final int ventilationsSystemFailure = 117;
	public static final int enclosureDoorOpen = 118;
	public static final int explosiveGas = 119;
	public static final int fire = 120;
	public static final int flood = 121;
	public static final int highHumidity = 122;
	public static final int highTemperature = 123;
	public static final int highWind = 124;
	public static final int iceBuildUp = 125;
	public static final int intrusionDetection = 126;
	public static final int lowFuel = 127;
	public static final int lowHumidity = 128;
	public static final int lowCablePressure = 129;
	public static final int lowTemperatue = 130;
	public static final int lowWater = 131;
	public static final int smoke = 132;
	public static final int toxicGas = 133;
	public static final int coolingSystemFailure = 134;
	public static final int externalEquipmentFailure = 135;
	/*
	 * -- Values 137-150 are reserved for environmental alarm related --
	 * probable causes -- The following are used with Processing error alarm.
	 */
	public static final int externalPointFailure = 136;
	public static final int storageCapacityProblem = 151;
	public static final int memoryMismatch = 152;
	public static final int corruptData = 153;
	public static final int outOfCPUCycles = 154;
	public static final int sfwrEnvironmentProblem = 155;
	public static final int sfwrDownloadFailure = 156;
	/*
	 * --A processing error alarm to be issued after the system has
	 * --reinitialised. This will indicate --to the management systems that the
	 * view they have of the managed --system may no longer --be valid. Usage
	 * example: The managed --system issues this alarm after a reinitialization
	 * with severity --warning to inform the --management system about the
	 * event. No clearing notification will --be sent.
	 */
	public static final int lossOfRealTimel = 157;
	public static final int applicationSubsystemFailure = 158;
	public static final int configurationOrCustomisationError = 159;
	public static final int databaseInconsistency = 160;
	public static final int fileError = 161;
	public static final int outOfMemory = 162;
	public static final int softwareError = 163;
	public static final int timeoutExpired = 164;
	public static final int underlayingResourceUnavailable = 165;
	/*
	 * --Values 168-200 are reserved for processing error alarm related --
	 * probable causes.
	 */
	public static final int versionMismatch = 166;
	public static final int bandwidthReduced = 201;
	public static final int congestion = 202;
	public static final int excessiveErrorRate = 203;
	public static final int excessiveResponseTime = 204;
	public static final int excessiveRetransmissionRate = 205;
	public static final int reducedLoggingCapability = 206;
	/* -- The following were defined X.733 */
	public static final int systemResourcesOverload = 207;
	public static final int adapterError = 500;
	public static final int applicationSubsystemFailture = 501;
	public static final int bandwidthReducedX733 = 502;
	public static final int callEstablishmentError = 503;
	public static final int communicationsProtocolError = 504;
	public static final int communicationsSubsystemFailure = 505;
	public static final int configurationOrCustomizationError = 506;
	public static final int congestionX733 = 507;
	public static final int coruptData = 508;
	public static final int cpuCyclesLimitExceeded = 509;
	public static final int dataSetOrModemError = 510;
	public static final int degradedSignalX733 = 511;
	public static final int dteDceInterfaceError = 512;
	public static final int enclosureDoorOpenX733 = 513;
	public static final int equipmentMalfunction = 514;
	public static final int excessiveVibration = 515;
	public static final int fileErrorX733 = 516;
	public static final int fireDetected = 517;
	public static final int framingErrorX733 = 518;
	public static final int heatingVentCoolingSystemProblem = 519;
	public static final int humidityUnacceptable = 520;
	public static final int inputOutputDeviceError = 521;
	public static final int inputDeviceError = 522;
	public static final int lanError = 523;
	public static final int leakDetected = 524;
	public static final int localNodeTransmissionErrorX733 = 525;
	public static final int lossOfFrameX733 = 526;
	public static final int lossOfSignalX733 = 527;
	public static final int materialSupplyExhausted = 528;
	public static final int multiplexerProblemX733 = 529;
	public static final int outOfMemoryX733 = 530;
	public static final int ouputDeviceError = 531;
	public static final int performanceDegraded = 532;
	public static final int powerProblems = 533;
	public static final int pressureUnacceptable = 534;
	public static final int processorProblems = 535;
	public static final int pumpFailureX733 = 536;
	public static final int queueSizeExceeded = 537;
	public static final int receiveFailureX733 = 538;
	public static final int receiverFailureX733 = 539;
	public static final int remoteNodeTransmissionErrorX733 = 540;
	public static final int resourceAtOrNearingCapacity = 541;
	public static final int responseTimeExecessive = 542;
	public static final int retransmissionRateExcessive = 543;
	public static final int softwareErrorX733 = 544;
	public static final int softwareProgramAbnormallyTerminated = 545;
	public static final int softwareProgramError = 546;
	public static final int storageCapacityProblemX733 = 547;
	public static final int temperatureUnacceptable = 548;
	public static final int thresholdCrossed = 549;
	public static final int timingProblemX733 = 550;
	public static final int toxicLeakDetected = 551;
	public static final int transmitFailureX733 = 552;
	public static final int transmiterFailure = 553;
	public static final int underlyingResourceUnavailable = 554;
	/* -- The following are defined in X.736 */
	public static final int versionMismatchX733 = 555;
	public static final int authenticationFailure = 600;
	public static final int breachOfConfidentiality = 601;
	public static final int cableTamper = 602;
	public static final int delayedInformation = 603;
	public static final int denialOfService = 604;
	public static final int duplicateInformation = 605;
	public static final int informationMissing = 606;
	public static final int informationModificationDetected = 607;
	public static final int informationOutOfSequence = 608;
	public static final int keyExpired = 609;
	public static final int nonRepudiationFailure = 610;
	public static final int outOfHoursActivity = 611;
	public static final int outOfService = 612;
	public static final int proceduralError = 613;
	public static final int unauthorizedAccessAttempt = 614;
	public static final int unexpectedInformation = 615;
	public static final int other = 1024;

	private final MOFactory moFactory;

	public IANAItuProbableCause(final MOFactory moFactory) {
		this.moFactory = moFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOColumn<Variable> createColumn(final int columnID,
			final int syntax, final MOAccess access,
			final Variable defaultValue, final boolean mutableInService) {
		final MOColumn<Variable> col = moFactory.createColumn(columnID, syntax,
				access, defaultValue, mutableInService);
		if (col instanceof MOMutableColumn) {
			final MOMutableColumn<Variable> mcol = (MOMutableColumn<Variable>) col;
			final ValueConstraint vc = new EnumerationConstraint(new int[]{aIS,
					callSetUpFailure, degradedSignal, farEndReceiverFailure,
					framingError, lossOfFrame, lossOfPointer, lossOfSignal,
					payloadTypeMismatch, transmissionError,
					remoteAlarmInterface, excessiveBER, pathTraceMismatch,
					unavailable, signalLabelMismatch, lossOfMultiFrame,
					receiveFailure, transmitFailure, modulationFailure,
					demodulationFailure, broadcastChannelFailure,
					connectionEstablishmentError, invalidMessageReceived,
					localNodeTransmissionError, remoteNodeTransmissionError,
					routingFailure, backplaneFailure, dataSetProblem,
					equipmentIdentifierDuplication, externalIFDeviceProblem,
					lineCardProblem, multiplexerProblem,
					nEIdentifierDuplication, powerProblem, processorProblem,
					protectionPathFailure, receiverFailure,
					replaceableUnitMissing, replaceableUnitTypeMismatch,
					synchronizationSourceMismatch, terminalProblem,
					timingProblem, transmitterFailure, trunkCardProblem,
					replaceableUnitProblem, realTimeClockFailure,
					antennaFailure, batteryChargingFailure, diskFailure,
					frequencyHoppingFailure, iODeviceError,
					lossOfSynchronisation, lossOfRedundancy,
					powerSupplyFailure, signalQualityEvaluationFailure,
					tranceiverFailure, protectionMechanismFailure,
					protectingResourceFailure, airCompressorFailure,
					airConditioningFailure, airDryerFailure,
					batteryDischarging, batteryFailure, commercialPowerFailure,
					coolingFanFailure, engineFailure, fireDetectorFailure,
					fuseFailure, generatorFailure, lowBatteryThreshold,
					pumpFailure, rectifierFailure, rectifierHighVoltage,
					rectifierLowFVoltage, ventilationsSystemFailure,
					enclosureDoorOpen, explosiveGas, fire, flood, highHumidity,
					highTemperature, highWind, iceBuildUp, intrusionDetection,
					lowFuel, lowHumidity, lowCablePressure, lowTemperatue,
					lowWater, smoke, toxicGas, coolingSystemFailure,
					externalEquipmentFailure, externalPointFailure,
					storageCapacityProblem, memoryMismatch, corruptData,
					outOfCPUCycles, sfwrEnvironmentProblem,
					sfwrDownloadFailure, lossOfRealTimel,
					applicationSubsystemFailure,
					configurationOrCustomisationError, databaseInconsistency,
					fileError, outOfMemory, softwareError, timeoutExpired,
					underlayingResourceUnavailable, versionMismatch,
					bandwidthReduced, congestion, excessiveErrorRate,
					excessiveResponseTime, excessiveRetransmissionRate,
					reducedLoggingCapability, systemResourcesOverload,
					adapterError, applicationSubsystemFailture,
					bandwidthReducedX733, callEstablishmentError,
					communicationsProtocolError,
					communicationsSubsystemFailure,
					configurationOrCustomizationError, congestionX733,
					coruptData, cpuCyclesLimitExceeded, dataSetOrModemError,
					degradedSignalX733, dteDceInterfaceError,
					enclosureDoorOpenX733, equipmentMalfunction,
					excessiveVibration, fileErrorX733, fireDetected,
					framingErrorX733, heatingVentCoolingSystemProblem,
					humidityUnacceptable, inputOutputDeviceError,
					inputDeviceError, lanError, leakDetected,
					localNodeTransmissionErrorX733, lossOfFrameX733,
					lossOfSignalX733, materialSupplyExhausted,
					multiplexerProblemX733, outOfMemoryX733, ouputDeviceError,
					performanceDegraded, powerProblems, pressureUnacceptable,
					processorProblems, pumpFailureX733, queueSizeExceeded,
					receiveFailureX733, receiverFailureX733,
					remoteNodeTransmissionErrorX733,
					resourceAtOrNearingCapacity, responseTimeExecessive,
					retransmissionRateExcessive, softwareErrorX733,
					softwareProgramAbnormallyTerminated, softwareProgramError,
					storageCapacityProblemX733, temperatureUnacceptable,
					thresholdCrossed, timingProblemX733, toxicLeakDetected,
					transmitFailureX733, transmiterFailure,
					underlyingResourceUnavailable, versionMismatchX733,
					authenticationFailure, breachOfConfidentiality,
					cableTamper, delayedInformation, denialOfService,
					duplicateInformation, informationMissing,
					informationModificationDetected, informationOutOfSequence,
					keyExpired, nonRepudiationFailure, outOfHoursActivity,
					outOfService, proceduralError, unauthorizedAccessAttempt,
					unexpectedInformation, other});
			mcol.addMOValueValidationListener(new ValueConstraintValidator(vc));
		}
		return col;
	}

	@Override
	public Variable createInitialValue() {
		final Variable v = new Integer32();
		if (v instanceof AssignableFromLong) {
			((AssignableFromLong) v).setValue(1);
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOScalar<Variable> createScalar(final OID oid,
			final MOAccess access, final Variable value) {
		final MOScalar<Variable> scalar = moFactory.createScalar(oid, access,
				value);
		final ValueConstraint vc = new EnumerationConstraint(new int[]{aIS,
				callSetUpFailure, degradedSignal, farEndReceiverFailure,
				framingError, lossOfFrame, lossOfPointer, lossOfSignal,
				payloadTypeMismatch, transmissionError, remoteAlarmInterface,
				excessiveBER, pathTraceMismatch, unavailable,
				signalLabelMismatch, lossOfMultiFrame, receiveFailure,
				transmitFailure, modulationFailure, demodulationFailure,
				broadcastChannelFailure, connectionEstablishmentError,
				invalidMessageReceived, localNodeTransmissionError,
				remoteNodeTransmissionError, routingFailure, backplaneFailure,
				dataSetProblem, equipmentIdentifierDuplication,
				externalIFDeviceProblem, lineCardProblem, multiplexerProblem,
				nEIdentifierDuplication, powerProblem, processorProblem,
				protectionPathFailure, receiverFailure, replaceableUnitMissing,
				replaceableUnitTypeMismatch, synchronizationSourceMismatch,
				terminalProblem, timingProblem, transmitterFailure,
				trunkCardProblem, replaceableUnitProblem, realTimeClockFailure,
				antennaFailure, batteryChargingFailure, diskFailure,
				frequencyHoppingFailure, iODeviceError, lossOfSynchronisation,
				lossOfRedundancy, powerSupplyFailure,
				signalQualityEvaluationFailure, tranceiverFailure,
				protectionMechanismFailure, protectingResourceFailure,
				airCompressorFailure, airConditioningFailure, airDryerFailure,
				batteryDischarging, batteryFailure, commercialPowerFailure,
				coolingFanFailure, engineFailure, fireDetectorFailure,
				fuseFailure, generatorFailure, lowBatteryThreshold,
				pumpFailure, rectifierFailure, rectifierHighVoltage,
				rectifierLowFVoltage, ventilationsSystemFailure,
				enclosureDoorOpen, explosiveGas, fire, flood, highHumidity,
				highTemperature, highWind, iceBuildUp, intrusionDetection,
				lowFuel, lowHumidity, lowCablePressure, lowTemperatue,
				lowWater, smoke, toxicGas, coolingSystemFailure,
				externalEquipmentFailure, externalPointFailure,
				storageCapacityProblem, memoryMismatch, corruptData,
				outOfCPUCycles, sfwrEnvironmentProblem, sfwrDownloadFailure,
				lossOfRealTimel, applicationSubsystemFailure,
				configurationOrCustomisationError, databaseInconsistency,
				fileError, outOfMemory, softwareError, timeoutExpired,
				underlayingResourceUnavailable, versionMismatch,
				bandwidthReduced, congestion, excessiveErrorRate,
				excessiveResponseTime, excessiveRetransmissionRate,
				reducedLoggingCapability, systemResourcesOverload,
				adapterError, applicationSubsystemFailture,
				bandwidthReducedX733, callEstablishmentError,
				communicationsProtocolError, communicationsSubsystemFailure,
				configurationOrCustomizationError, congestionX733, coruptData,
				cpuCyclesLimitExceeded, dataSetOrModemError,
				degradedSignalX733, dteDceInterfaceError,
				enclosureDoorOpenX733, equipmentMalfunction,
				excessiveVibration, fileErrorX733, fireDetected,
				framingErrorX733, heatingVentCoolingSystemProblem,
				humidityUnacceptable, inputOutputDeviceError, inputDeviceError,
				lanError, leakDetected, localNodeTransmissionErrorX733,
				lossOfFrameX733, lossOfSignalX733, materialSupplyExhausted,
				multiplexerProblemX733, outOfMemoryX733, ouputDeviceError,
				performanceDegraded, powerProblems, pressureUnacceptable,
				processorProblems, pumpFailureX733, queueSizeExceeded,
				receiveFailureX733, receiverFailureX733,
				remoteNodeTransmissionErrorX733, resourceAtOrNearingCapacity,
				responseTimeExecessive, retransmissionRateExcessive,
				softwareErrorX733, softwareProgramAbnormallyTerminated,
				softwareProgramError, storageCapacityProblemX733,
				temperatureUnacceptable, thresholdCrossed, timingProblemX733,
				toxicLeakDetected, transmitFailureX733, transmiterFailure,
				underlyingResourceUnavailable, versionMismatchX733,
				authenticationFailure, breachOfConfidentiality, cableTamper,
				delayedInformation, denialOfService, duplicateInformation,
				informationMissing, informationModificationDetected,
				informationOutOfSequence, keyExpired, nonRepudiationFailure,
				outOfHoursActivity, outOfService, proceduralError,
				unauthorizedAccessAttempt, unexpectedInformation, other});
		scalar.addMOValueValidationListener(new ValueConstraintValidator(vc));
		return scalar;
	}

	@Override
	public String getModuleName() {
		return TC_MODULE_IANA_ITU_ALARM_TC_MIB;
	}

	@Override
	public String getName() {
		return TC_IANAITUPROBABLECAUSE;
	}
}