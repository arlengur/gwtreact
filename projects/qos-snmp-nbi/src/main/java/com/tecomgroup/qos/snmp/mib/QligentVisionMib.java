/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mib;

import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.NotificationOriginator;
import org.snmp4j.agent.mo.*;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.tecomgroup.qos.snmp.mib.generated.tc.IANAItuProbableCause;
import com.tecomgroup.qos.snmp.mib.generated.tc.ItuPerceivedSeverity;
import com.tecomgroup.qos.snmp.mib.generated.tc.SnmpAdminString;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.ElementIndex;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.Float64TC;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.VisionAlarmCountNumber;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.VisionAlarmUpdateType;
import com.tecomgroup.qos.snmp.mib.generated.tc.vision.VisionResultParameterType;
/**
 * Represents QLIGENT-VISION-MIB.
 * 
 * @author novohatskiy.r
 */
public class QligentVisionMib implements MOGroup {


    /**
	 * @generated
	 */
	public class VisionAlarmEntryRow extends DefaultMOMutableRow2PC {
		public VisionAlarmEntryRow(final OID index, final Variable[] values) {
			super(index, values);
		}

		@Override
		public Variable getValue(final int column) {
			switch (column) {
				case idxVisionAlarmIndex :
					return getVisionAlarmIndex();
				case idxVisionAlarmPerceivedSeverity :
					return getVisionAlarmPerceivedSeverity();
				case idxVisionAlarmProbableCause :
					return getVisionAlarmProbableCause();
				case idxVisionAlarmSpecificCause :
					return getVisionAlarmSpecificCause();
				case idxVisionAlarmDisplayName :
					return getVisionAlarmDisplayName();
				case idxVisionAlarmDescription :
					return getVisionAlarmDescription();
				case idxVisionAlarmCreationDateTime :
					return getVisionAlarmCreationDateTime();
				case idxVisionAlarmLastUpdateType :
					return getVisionAlarmLastUpdateType();
				case idxVisionAlarmLastUpdateDateTime :
					return getVisionAlarmLastUpdateDateTime();
				case idxVisionAlarmAcknowledged :
					return getVisionAlarmAcknowledged();
				case idxVisionAlarmAcknowledgeDateTime :
					return getVisionAlarmAcknowledgeDateTime();
				case idxVisionAlarmDuration :
					return getVisionAlarmDuration();
				case idxVisionAlarmCount :
					return getVisionAlarmCount();
				case idxVisionAlarmThresholdValue :
					return getVisionAlarmThresholdValue();
				case idxVisionAlarmDetectionValue :
					return getVisionAlarmDetectionValue();
				case idxVisionAlarmSourceId :
					return getVisionAlarmSourceId();
				case idxVisionAlarmOriginatorDisplayName:
					return getVisionAlarmOriginatorId();
				case idxVisionAlarmSettings :
					return getVisionAlarmSettings();
				default :
					return super.getValue(column);
			}
		}

		public Integer32 getVisionAlarmAcknowledged() {
			return (Integer32) super.getValue(idxVisionAlarmAcknowledged);
		}

		public OctetString getVisionAlarmAcknowledgeDateTime() {
			return (OctetString) super
					.getValue(idxVisionAlarmAcknowledgeDateTime);
		}

		public Integer32 getVisionAlarmCount() {
			return (Integer32) super.getValue(idxVisionAlarmCount);
		}

		public OctetString getVisionAlarmCreationDateTime() {
			return (OctetString) super.getValue(idxVisionAlarmCreationDateTime);
		}

		public OctetString getVisionAlarmDescription() {
			return (OctetString) super.getValue(idxVisionAlarmDescription);
		}

		public OctetString getVisionAlarmDetectionValue() {
			return (OctetString) super.getValue(idxVisionAlarmDetectionValue);
		}

		public OctetString getVisionAlarmDisplayName() {
			return (OctetString) super.getValue(idxVisionAlarmDisplayName);
		}

		public TimeTicks getVisionAlarmDuration() {
			return (TimeTicks) super.getValue(idxVisionAlarmDuration);
		}

		public Integer32 getVisionAlarmIndex() {
			return (Integer32) super.getValue(idxVisionAlarmIndex);
		}

		public OctetString getVisionAlarmLastUpdateDateTime() {
			return (OctetString) super
					.getValue(idxVisionAlarmLastUpdateDateTime);
		}

		public Integer32 getVisionAlarmLastUpdateType() {
			return (Integer32) super.getValue(idxVisionAlarmLastUpdateType);
		}

		public Integer32 getVisionAlarmOriginatorId() {
			return (Integer32) super.getValue(idxVisionAlarmOriginatorDisplayName);
		}

		public Integer32 getVisionAlarmPerceivedSeverity() {
			return (Integer32) super.getValue(idxVisionAlarmPerceivedSeverity);
		}

		public Integer32 getVisionAlarmProbableCause() {
			return (Integer32) super.getValue(idxVisionAlarmProbableCause);
		}

		public OctetString getVisionAlarmSettings() {
			return (OctetString) super.getValue(idxVisionAlarmSettings);
		}

		public Integer32 getVisionAlarmSourceId() {
			return (Integer32) super.getValue(idxVisionAlarmSourceId);
		}

		public OctetString getVisionAlarmSpecificCause() {
			return (OctetString) super.getValue(idxVisionAlarmSpecificCause);
		}

		public OctetString getVisionAlarmThresholdValue() {
			return (OctetString) super.getValue(idxVisionAlarmThresholdValue);
		}

		@Override
		public void setValue(final int column, final Variable value) {
			switch (column) {
				case idxVisionAlarmIndex :
					setVisionAlarmIndex((Integer32) value);
					break;
				case idxVisionAlarmPerceivedSeverity :
					setVisionAlarmPerceivedSeverity((Integer32) value);
					break;
				case idxVisionAlarmProbableCause :
					setVisionAlarmProbableCause((Integer32) value);
					break;
				case idxVisionAlarmSpecificCause :
					setVisionAlarmSpecificCause((OctetString) value);
					break;
				case idxVisionAlarmDisplayName :
					setVisionAlarmDisplayName((OctetString) value);
					break;
				case idxVisionAlarmDescription :
					setVisionAlarmDescription((OctetString) value);
					break;
				case idxVisionAlarmCreationDateTime :
					setVisionAlarmCreationDateTime((OctetString) value);
					break;
				case idxVisionAlarmLastUpdateType :
					setVisionAlarmLastUpdateType((Integer32) value);
					break;
				case idxVisionAlarmLastUpdateDateTime :
					setVisionAlarmLastUpdateDateTime((OctetString) value);
					break;
				case idxVisionAlarmAcknowledged :
					setVisionAlarmAcknowledged((Integer32) value);
					break;
				case idxVisionAlarmAcknowledgeDateTime :
					setVisionAlarmAcknowledgeDateTime((OctetString) value);
					break;
				case idxVisionAlarmDuration :
					setVisionAlarmDuration((TimeTicks) value);
					break;
				case idxVisionAlarmCount :
					setVisionAlarmCount((Integer32) value);
					break;
				case idxVisionAlarmThresholdValue :
					setVisionAlarmThresholdValue((OctetString) value);
					break;
				case idxVisionAlarmDetectionValue :
					setVisionAlarmDetectionValue((OctetString) value);
					break;
				case idxVisionAlarmSourceId :
					setVisionAlarmSourceId((Integer32) value);
					break;
				case idxVisionAlarmOriginatorDisplayName:
					setVisionAlarmOriginatorId((Integer32) value);
					break;
				case idxVisionAlarmSettings :
					setVisionAlarmSettings((OctetString) value);
					break;
				default :
					super.setValue(column, value);
			}
		}

		public void setVisionAlarmAcknowledged(final Integer32 newValue) {
			super.setValue(idxVisionAlarmAcknowledged, newValue);
		}

		public void setVisionAlarmAcknowledgeDateTime(final OctetString newValue) {
			super.setValue(idxVisionAlarmAcknowledgeDateTime, newValue);
		}

		public void setVisionAlarmCount(final Integer32 newValue) {
			super.setValue(idxVisionAlarmCount, newValue);
		}

		public void setVisionAlarmCreationDateTime(final OctetString newValue) {
			super.setValue(idxVisionAlarmCreationDateTime, newValue);
		}

		public void setVisionAlarmDescription(final OctetString newValue) {
			super.setValue(idxVisionAlarmDescription, newValue);
		}

		public void setVisionAlarmDetectionValue(final OctetString newValue) {
			super.setValue(idxVisionAlarmDetectionValue, newValue);
		}

		public void setVisionAlarmDisplayName(final OctetString newValue) {
			super.setValue(idxVisionAlarmDisplayName, newValue);
		}

		public void setVisionAlarmDuration(final TimeTicks newValue) {
			super.setValue(idxVisionAlarmDuration, newValue);
		}

		public void setVisionAlarmIndex(final Integer32 newValue) {
			super.setValue(idxVisionAlarmIndex, newValue);
		}

		public void setVisionAlarmLastUpdateDateTime(final OctetString newValue) {
			super.setValue(idxVisionAlarmLastUpdateDateTime, newValue);
		}

		public void setVisionAlarmLastUpdateType(final Integer32 newValue) {
			super.setValue(idxVisionAlarmLastUpdateType, newValue);
		}

		public void setVisionAlarmOriginatorId(final Integer32 newValue) {
			super.setValue(idxVisionAlarmOriginatorDisplayName, newValue);
		}

		public void setVisionAlarmPerceivedSeverity(final Integer32 newValue) {
			super.setValue(idxVisionAlarmPerceivedSeverity, newValue);
		}

		public void setVisionAlarmProbableCause(final Integer32 newValue) {
			super.setValue(idxVisionAlarmProbableCause, newValue);
		}

		public void setVisionAlarmSettings(final OctetString newValue) {
			super.setValue(idxVisionAlarmSettings, newValue);
		}

		public void setVisionAlarmSourceId(final Integer32 newValue) {
			super.setValue(idxVisionAlarmSourceId, newValue);
		}

		public void setVisionAlarmSpecificCause(final OctetString newValue) {
			super.setValue(idxVisionAlarmSpecificCause, newValue);
		}

		public void setVisionAlarmThresholdValue(final OctetString newValue) {
			super.setValue(idxVisionAlarmThresholdValue, newValue);
		}
	}

	/**
	 * @generated
	 */
	public class VisionCurrentResultEntryRow extends DefaultMOMutableRow2PC {
		public VisionCurrentResultEntryRow(final OID index,
				final Variable[] values) {
			super(index, values);
		}

		@Override
		public Variable getValue(final int column) {
			switch (column) {
                case idxVisionCurrentResultTaskId :
                    return getVisionCurrentResultTaskId();
				case idxVisionCurrentResultType :
					return getVisionCurrentResultType();
				case idxVisionCurrentResultDisplayName :
					return getVisionCurrentResultDisplayName();
				case idxVisionCurrentResultDescription :
					return getVisionCurrentResultDescription();
				case idxVisionCurrentResultUnits :
					return getVisionCurrentResultUnits();
				case idxVisionCurrentResultValue :
					return getVisionCurrentResultValue();
				case idxVisionCurrentResultDateTime :
					return getVisionCurrentResultDateTime();
				default :
					return super.getValue(column);
			}
		}

        public Integer32 getVisionCurrentResultTaskId() {
            return (Integer32) super.getValue(idxVisionCurrentResultTaskId);
        }

		public OctetString getVisionCurrentResultDateTime() {
			return (OctetString) super.getValue(idxVisionCurrentResultDateTime);
		}

		public OctetString getVisionCurrentResultDescription() {
			return (OctetString) super
					.getValue(idxVisionCurrentResultDescription);
		}

		public OctetString getVisionCurrentResultDisplayName() {
			return (OctetString) super
					.getValue(idxVisionCurrentResultDisplayName);
		}

		public Integer32 getVisionCurrentResultType() {
			return (Integer32) super.getValue(idxVisionCurrentResultType);
		}

		public OctetString getVisionCurrentResultUnits() {
			return (OctetString) super.getValue(idxVisionCurrentResultUnits);
		}

		public OctetString getVisionCurrentResultValue() {
			return (OctetString) super.getValue(idxVisionCurrentResultValue);
		}

		@Override
		public void setValue(final int column, final Variable value) {
			switch (column) {
                case idxVisionCurrentResultTaskId :
                    setVisionCurrentResultTaskId((Integer32) value);
                    break;
				case idxVisionCurrentResultType :
					setVisionCurrentResultType((Integer32) value);
					break;
				case idxVisionCurrentResultDisplayName :
					setVisionCurrentResultDisplayName((OctetString) value);
					break;
				case idxVisionCurrentResultDescription :
					setVisionCurrentResultDescription((OctetString) value);
					break;
				case idxVisionCurrentResultUnits :
					setVisionCurrentResultUnits((OctetString) value);
					break;
				case idxVisionCurrentResultValue :
					setVisionCurrentResultValue((OctetString) value);
					break;
				case idxVisionCurrentResultDateTime :
					setVisionCurrentResultDateTime((OctetString) value);
					break;
				default :
					super.setValue(column, value);
			}
		}

        public void setVisionCurrentResultTaskId(final Integer32 newValue) {
            super.setValue(idxVisionCurrentResultTaskId, newValue);
        }

		public void setVisionCurrentResultDateTime(final OctetString newValue) {
			super.setValue(idxVisionCurrentResultDateTime, newValue);
		}

		public void setVisionCurrentResultDescription(final OctetString newValue) {
			super.setValue(idxVisionCurrentResultDescription, newValue);
		}

		public void setVisionCurrentResultDisplayName(final OctetString newValue) {
			super.setValue(idxVisionCurrentResultDisplayName, newValue);
		}

		public void setVisionCurrentResultType(final Integer32 newValue) {
			super.setValue(idxVisionCurrentResultType, newValue);
		}

		public void setVisionCurrentResultUnits(final OctetString newValue) {
			super.setValue(idxVisionCurrentResultUnits, newValue);
		}

		public void setVisionCurrentResultValue(final OctetString newValue) {
			super.setValue(idxVisionCurrentResultValue, newValue);
		}

	}

    public class VisionPhysicalTopLevelEntryRow extends DefaultMOTableRow {
        public VisionPhysicalTopLevelEntryRow(OID index, Variable[] values) {
            super(index, values);
        }
    }

	public static final OID oidQligentVisionMib = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2});
	public static final OID oidEntPhysicalContainsEntry = new OID(new int[]{1,
			3, 6, 1, 4, 1, 40554, 2, 1, 1, 2, 1});
	public static final OID oidEntPhysicalEntry = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2, 1, 1, 1, 1});
	public static final OID oidEntPhysicalIndex = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2, 1, 1, 1, 1, 1});
	public static final OID oidEntPhysicalDescr = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2, 1, 1, 1, 1, 2});
	public static final OID oidVisionAlarmEntry = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2, 1, 2, 1, 1});
	public static final OID oidVisionAlarmIndex = new OID(new int[]{1, 3, 6, 1,
			4, 1, 40554, 2, 1, 2, 1, 1, 1});
    public static final OID oidVisionAlarmActiveState = new OID(new int[]{1, 3,
            6, 1, 4, 1, 40554, 2, 1, 2, 2, 1});
	public static final OID oidVisionAlarmClearState = new OID(new int[]{1, 3,
			6, 1, 4, 1, 40554, 2, 1, 2, 2, 2});
    public static OID oidVisionAlarmUpdateState = new OID(new int[]{1, 3,
			6, 1, 4, 1, 40554, 2, 1, 2, 2, 3});
	public static final OID oidTrapVarVisionAlarmIndex = new OID(new int[]{1,
			3, 6, 1, 4, 1, 40554, 2, 1, 2, 1, 1, 1});
	public static final OID oidTrapVarVisionAlarmSourceId = new OID(new int[]{
			1, 3, 6, 1, 4, 1, 40554, 2, 1, 2, 1, 1, 16});
	public static final OID oidVisionCurrentResultEntry = new OID(new int[]{1,
			3, 6, 1, 4, 1, 40554, 2, 1, 3, 1, 1});
	public static final OID oidVisionCurrentResultIndex = new OID(new int[]{1,
			3, 6, 1, 4, 1, 40554, 2, 1, 3, 1, 1, 1});
    public static final OID oidVisionPhysicalTopLevelEntry = new OID(new int[]
            {1, 3, 6, 1, 4, 1, 40554, 2, 1, 1, 3, 1});
    public static final OID oidVisionPhysicalTopLevelIndex = new OID(new int[]
            {1, 3, 6, 1, 4, 1, 40554, 2, 1, 1, 3, 1, 1});


	public static final String TC_MODULE_QLIGENT_VISION_MIB = "QligentVisionMIB";

	public static final String tcDefElementIndex = "ElementIndex";
	public static final String tcModuleSnmpFrameworkMib = "SNMP-FRAMEWORK-MIB";
	public static final String tcDefSnmpAdminString = "SnmpAdminString";
	public static final String tcModuleSNMPv2Tc = "SNMPv2-TC";
	public static final String tcModuleQligentVisionMib = "QligentVisionMIB";
	public static final String tcDefTruthValue = "TruthValue";
	public static final String tcDefDateAndTime = "DateAndTime";
	public static final String tcDefVisionResultParameterType = "VisionResultParameterType";

	// Column TC definitions for visionAlarmEntry:
	public static final String tcModuleItuAlarmTcMib = "ITU-ALARM-TC-MIB";
	public static final String tcDefItuPerceivedSeverity = "ItuPerceivedSeverity";
	public static final String tcModuleIanaItuAlarmTcMib = "IANA-ITU-ALARM-TC-MIB";
	public static final String tcDefIANAItuProbableCause = "IANAItuProbableCause";
	public static final String tcDefVisionAlarmUpdateType = "VisionAlarmUpdateType";
	public static final String tcDefVisionAlarmCountNumber = "VisionAlarmCountNumber";
	public static final String tcDefFloat64TC = "Float64TC";

	public static final int VISION_ALARM_TABLE_COLUMN_COUNT = 19;
	public static final int VISION_CURRENT_RESULT_TABLE_COLUMN_COUNT = 8;

	// Column sub-identifier definitions for visionAlarmEntry:
	public static final int colVisionAlarmIndex = 1;
	public static final int colVisionAlarmPerceivedSeverity = 2;
	public static final int colVisionAlarmProbableCause = 3;
	public static final int colVisionAlarmSpecificCause = 4;
	public static final int colVisionAlarmDisplayName = 5;
	public static final int colVisionAlarmDescription = 6;
	public static final int colVisionAlarmCreationDateTime = 7;
	public static final int colVisionAlarmLastUpdateType = 8;
	public static final int colVisionAlarmLastUpdateDateTime = 9;
	public static final int colVisionAlarmAcknowledged = 10;
	public static final int colVisionAlarmAcknowledgeDateTime = 11;
	public static final int colVisionAlarmDuration = 12;
	public static final int colVisionAlarmCount = 13;
	public static final int colVisionAlarmThresholdValue = 14;
	public static final int colVisionAlarmDetectionValue = 15;
	public static final int colVisionAlarmSourceId = 16;
    public static final int colVisionAlarmParameterId = 17;
	public static final int colVisionAlarmOriginatorDisplayName = 18;
	public static final int colVisionAlarmSettings = 19;

	// Column index definitions for visionAlarmEntry:
	public static final int idxVisionAlarmIndex = 0;
	public static final int idxVisionAlarmPerceivedSeverity = 1;
	public static final int idxVisionAlarmProbableCause = 2;
	public static final int idxVisionAlarmSpecificCause = 3;
	public static final int idxVisionAlarmDisplayName = 4;
	public static final int idxVisionAlarmDescription = 5;
	public static final int idxVisionAlarmCreationDateTime = 6;
	public static final int idxVisionAlarmLastUpdateType = 7;
	public static final int idxVisionAlarmLastUpdateDateTime = 8;
	public static final int idxVisionAlarmAcknowledged = 9;
	public static final int idxVisionAlarmAcknowledgeDateTime = 10;
	public static final int idxVisionAlarmDuration = 11;
	public static final int idxVisionAlarmCount = 12;
	public static final int idxVisionAlarmThresholdValue = 13;
	public static final int idxVisionAlarmDetectionValue = 14;
	public static final int idxVisionAlarmSourceId = 15;
    public static final int idxVisionAlarmParameterId = 16;
	public static final int idxVisionAlarmOriginatorDisplayName = 17;
	public static final int idxVisionAlarmSettings = 18;

	private MOTableSubIndex[] visionAlarmEntryIndexes;
	private MOTableIndex visionAlarmEntryIndex;

	private MOTable<VisionAlarmEntryRow, MOColumn<Variable>, MOTableModel<VisionAlarmEntryRow>> visionAlarmEntry;
	private MOTableModel<VisionAlarmEntryRow> visionAlarmEntryModel;

	// Column sub-identifier definitions for visionCurrentResultEntry:
    public static final int colVisionCurrentResultTaskId = 2;
	public static final int colVisionCurrentResultType = 3;
	public static final int colVisionCurrentResultDisplayName = 4;
	public static final int colVisionCurrentResultDescription = 5;
	public static final int colVisionCurrentResultUnits = 6;
	public static final int colVisionCurrentResultValue = 7;
	public static final int colVisionCurrentResultDateTime = 8;

	// Column index definitions for visionCurrentResultEntry:
    public static final int idxVisionCurrentResultTaskId = 0;
	public static final int idxVisionCurrentResultType = 1;
	public static final int idxVisionCurrentResultDisplayName = 2;
	public static final int idxVisionCurrentResultDescription = 3;
	public static final int idxVisionCurrentResultUnits = 4;
	public static final int idxVisionCurrentResultValue = 5;
	public static final int idxVisionCurrentResultDateTime = 6;

	private MOTableSubIndex[] visionCurrentResultEntryIndexes;
	private MOTableIndex visionCurrentResultEntryIndex;
	private MOTable<VisionCurrentResultEntryRow, MOColumn<Variable>, MOTableModel<VisionCurrentResultEntryRow>> visionCurrentResultEntry;
	private MOTableModel<VisionCurrentResultEntryRow> visionCurrentResultEntryModel;

    // Column sub-identifier definitions for visionPhysicalTopLevelEntry:
    public static final int colVisionPhysicalTopLevelIndex = 1;

    // Column index definitions for visionPhysicalTopLevelEntry:
    public static final int idxVisionPhysicalTopLevelIndex = 0;

    private MOTableSubIndex[] visionPhysicalTopLevelEntryIndexes;
    private MOTableIndex visionPhysicalTopLevelEntryIndex;
    private MOTable<VisionPhysicalTopLevelEntryRow, MOColumn<Variable>, MOTableModel<VisionPhysicalTopLevelEntryRow>> visionPhysicalTopLevelEntry;
    private MOTableModel<VisionPhysicalTopLevelEntryRow> visionPhysicalTopLevelEntryModel;

	private final EntPhysicalMibSection entitySection;

	public QligentVisionMib(final MOFactory moFactory) {
		createMO(moFactory);
		entitySection = new EntPhysicalMibSection(moFactory,
				oidEntPhysicalEntry, oidEntPhysicalContainsEntry);
	}

	/**
	 * Textual Definitions of other MIB modules
	 * 
	 * @generated
	 */
	public void addImportedTCsToFactory(final MOFactory moFactory) {
		moFactory.addTextualConvention(new SnmpAdminString(moFactory));
		moFactory.addTextualConvention(new IANAItuProbableCause(moFactory));
		moFactory.addTextualConvention(new ItuPerceivedSeverity(moFactory));
	}

	/**
	 * Textual Definitions of MIB module QligentVisionMIB
	 * 
	 * @generated
	 */
	protected void addTextualConventions(final MOFactory moFactory) {
		moFactory.addTextualConvention(new VisionResultParameterType(moFactory));
		moFactory.addTextualConvention(new VisionAlarmCountNumber(moFactory));
		moFactory.addTextualConvention(new Float64TC(moFactory));
		moFactory.addTextualConvention(new VisionAlarmUpdateType(moFactory));
		moFactory.addTextualConvention(new ElementIndex(moFactory));
	}

	/**
	 * Create the ManagedObjects defined for this MIB module using the specified
	 * {@link MOFactory}.
	 * 
	 * @param moFactory
	 *            the <code>MOFactory</code> instance to use for object
	 *            creation.
	 */
	protected void createMO(final MOFactory moFactory) {
		addTextualConventions(moFactory);
		createVisionAlarmEntry(moFactory);
		createVisionCurrentResultEntry(moFactory);
        createVisionTopLevelEntityEntry(moFactory);
	}

    @SuppressWarnings(value = {"unchecked"})
    private void createVisionTopLevelEntityEntry(final MOFactory moFactory) {
        visionPhysicalTopLevelEntryIndexes = new MOTableSubIndex[] {
               moFactory.createSubIndex(oidVisionPhysicalTopLevelIndex,
                       SMIConstants.SYNTAX_INTEGER, 1, 1)};

        visionPhysicalTopLevelEntryIndex = moFactory.createIndex(visionPhysicalTopLevelEntryIndexes,
                false, new MOTableIndexValidator() {
                    @Override
                    public boolean isValidIndex(final OID index) {
                        return false;
                    }
                });

        final MOColumn[] visionPhysicalTopLevelEntryColumns = new MOColumn[1];

        visionPhysicalTopLevelEntryColumns[idxVisionPhysicalTopLevelIndex] =
                moFactory.createColumn(
                        colVisionPhysicalTopLevelIndex,
                        SMIConstants.SYNTAX_INTEGER32,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                        tcModuleQligentVisionMib,
                        tcDefElementIndex);

        visionPhysicalTopLevelEntryModel = moFactory.createTableModel(oidVisionPhysicalTopLevelEntry,
                visionPhysicalTopLevelEntryIndex, visionPhysicalTopLevelEntryColumns);
        visionPhysicalTopLevelEntry = moFactory.createTable(oidVisionPhysicalTopLevelEntry,
                visionPhysicalTopLevelEntryIndex, visionPhysicalTopLevelEntryColumns,
                visionPhysicalTopLevelEntryModel);
    }

    /**
	 * @generated
	 */
	@SuppressWarnings(value = {"unchecked"})
	private void createVisionAlarmEntry(final MOFactory moFactory) {
		visionAlarmEntryIndexes = new MOTableSubIndex[]{moFactory
				.createSubIndex(oidVisionAlarmIndex,
						SMIConstants.SYNTAX_INTEGER, 1, 1)};

		visionAlarmEntryIndex = moFactory.createIndex(visionAlarmEntryIndexes,
				false, new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		final MOColumn<Variable>[] visionAlarmEntryColumns = new MOColumn[VISION_ALARM_TABLE_COLUMN_COUNT];
		visionAlarmEntryColumns[idxVisionAlarmIndex] = moFactory.createColumn(
				colVisionAlarmIndex, SMIConstants.SYNTAX_INTEGER32,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
				tcModuleQligentVisionMib, tcDefElementIndex);
		visionAlarmEntryColumns[idxVisionAlarmPerceivedSeverity] = moFactory
				.createColumn(
                        colVisionAlarmPerceivedSeverity,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory
                                .createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                        tcModuleItuAlarmTcMib, tcDefItuPerceivedSeverity);
		visionAlarmEntryColumns[idxVisionAlarmProbableCause] = moFactory
				.createColumn(
						colVisionAlarmProbableCause,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleIanaItuAlarmTcMib, tcDefIANAItuProbableCause);
		visionAlarmEntryColumns[idxVisionAlarmSpecificCause] = moFactory
				.createColumn(
						colVisionAlarmSpecificCause,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionAlarmEntryColumns[idxVisionAlarmDisplayName] = moFactory
				.createColumn(
						colVisionAlarmDisplayName,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionAlarmEntryColumns[idxVisionAlarmDescription] = moFactory
				.createColumn(
						colVisionAlarmDescription,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionAlarmEntryColumns[idxVisionAlarmCreationDateTime] = moFactory
				.createColumn(
						colVisionAlarmCreationDateTime,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSNMPv2Tc, tcDefDateAndTime);
		visionAlarmEntryColumns[idxVisionAlarmLastUpdateType] = moFactory
				.createColumn(
						colVisionAlarmLastUpdateType,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib, tcDefVisionAlarmUpdateType);
		visionAlarmEntryColumns[idxVisionAlarmLastUpdateDateTime] = moFactory
				.createColumn(
						colVisionAlarmLastUpdateDateTime,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSNMPv2Tc, tcDefDateAndTime);
		visionAlarmEntryColumns[idxVisionAlarmAcknowledged] = moFactory
				.createColumn(
						colVisionAlarmAcknowledged,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSNMPv2Tc, tcDefTruthValue);
		visionAlarmEntryColumns[idxVisionAlarmAcknowledgeDateTime] = moFactory
				.createColumn(
						colVisionAlarmAcknowledgeDateTime,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSNMPv2Tc, tcDefDateAndTime);
		visionAlarmEntryColumns[idxVisionAlarmDuration] = moFactory
				.createColumn(
						colVisionAlarmDuration,
						SMIConstants.SYNTAX_TIMETICKS,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		visionAlarmEntryColumns[idxVisionAlarmCount] = moFactory.createColumn(
                colVisionAlarmCount, SMIConstants.SYNTAX_INTEGER32,
                moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                tcModuleQligentVisionMib, tcDefVisionAlarmCountNumber);
		visionAlarmEntryColumns[idxVisionAlarmThresholdValue] = moFactory
				.createColumn(
                        colVisionAlarmThresholdValue,
                        SMIConstants.SYNTAX_OCTET_STRING,
                        moFactory
                                .createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                        tcModuleQligentVisionMib, tcDefFloat64TC);
		visionAlarmEntryColumns[idxVisionAlarmDetectionValue] = moFactory
				.createColumn(
						colVisionAlarmDetectionValue,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib, tcDefFloat64TC);
		visionAlarmEntryColumns[idxVisionAlarmSourceId] = moFactory
				.createColumn(
						colVisionAlarmSourceId,
						SMIConstants.SYNTAX_INTEGER32,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib, tcDefElementIndex);
        visionAlarmEntryColumns[idxVisionAlarmParameterId] = moFactory
                .createColumn(
                        colVisionAlarmParameterId,
                        SMIConstants.SYNTAX_INTEGER32,
                        moFactory
                                .createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                        tcModuleQligentVisionMib, tcDefElementIndex);
		visionAlarmEntryColumns[idxVisionAlarmOriginatorDisplayName] = moFactory
				.createColumn(
                        colVisionAlarmOriginatorDisplayName,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib, tcDefElementIndex);
		visionAlarmEntryColumns[idxVisionAlarmSettings] = moFactory
				.createColumn(
						colVisionAlarmSettings,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionAlarmEntryModel = moFactory.createTableModel(oidVisionAlarmEntry,
				visionAlarmEntryIndex, visionAlarmEntryColumns);
		visionAlarmEntry = moFactory.createTable(oidVisionAlarmEntry,
				visionAlarmEntryIndex, visionAlarmEntryColumns,
				visionAlarmEntryModel);
	}

	/**
	 * @generated
	 */
	@SuppressWarnings(value = {"unchecked"})
	private void createVisionCurrentResultEntry(final MOFactory moFactory) {
		visionCurrentResultEntryIndexes = new MOTableSubIndex[]{
				moFactory.createSubIndex(oidEntPhysicalIndex,
						SMIConstants.SYNTAX_INTEGER, 1, 1),
				moFactory.createSubIndex(oidVisionCurrentResultIndex,
						SMIConstants.SYNTAX_INTEGER, 1, 1)};

		visionCurrentResultEntryIndex = moFactory.createIndex(
				visionCurrentResultEntryIndexes, false,
				new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		final MOColumn<Variable>[] visionCurrentResultEntryColumns = new MOColumn[7];
        visionCurrentResultEntryColumns[idxVisionCurrentResultTaskId] = moFactory
                .createColumn(
                        colVisionCurrentResultTaskId,
                        SMIConstants.SYNTAX_INTEGER32,
                        moFactory
                                .createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
                        tcModuleQligentVisionMib, tcDefElementIndex);
		visionCurrentResultEntryColumns[idxVisionCurrentResultType] = moFactory
				.createColumn(
						colVisionCurrentResultType,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib,
						tcDefVisionResultParameterType);
		visionCurrentResultEntryColumns[idxVisionCurrentResultDisplayName] = moFactory
				.createColumn(
						colVisionCurrentResultDisplayName,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionCurrentResultEntryColumns[idxVisionCurrentResultDescription] = moFactory
				.createColumn(
						colVisionCurrentResultDescription,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionCurrentResultEntryColumns[idxVisionCurrentResultUnits] = moFactory
				.createColumn(
						colVisionCurrentResultUnits,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSnmpFrameworkMib, tcDefSnmpAdminString);
		visionCurrentResultEntryColumns[idxVisionCurrentResultValue] = moFactory
				.createColumn(
						colVisionCurrentResultValue,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleQligentVisionMib, tcDefFloat64TC);
		visionCurrentResultEntryColumns[idxVisionCurrentResultDateTime] = moFactory
				.createColumn(
						colVisionCurrentResultDateTime,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
						tcModuleSNMPv2Tc, tcDefDateAndTime);
		visionCurrentResultEntryModel = moFactory.createTableModel(
				oidVisionCurrentResultEntry, visionCurrentResultEntryIndex,
				visionCurrentResultEntryColumns);
		visionCurrentResultEntry = moFactory.createTable(
				oidVisionCurrentResultEntry, visionCurrentResultEntryIndex,
				visionCurrentResultEntryColumns, visionCurrentResultEntryModel);
	}

	/**
	 * @generated
	 */
	public MOTable<VisionAlarmEntryRow, MOColumn<Variable>, MOTableModel<VisionAlarmEntryRow>> getVisionAlarmEntry() {
		return visionAlarmEntry;
	}

	/**
	 * @generated
	 */
	public MOTable<VisionCurrentResultEntryRow, MOColumn<Variable>, MOTableModel<VisionCurrentResultEntryRow>> getVisionCurrentResultEntry() {
		return visionCurrentResultEntry;
	}

    public MOTable<VisionPhysicalTopLevelEntryRow, MOColumn<Variable>, MOTableModel<VisionPhysicalTopLevelEntryRow>> getVisionPhysicalTopLevelEntry() {
        return visionPhysicalTopLevelEntry;
    }

    @Override
	public void registerMOs(final MOServer server, final OctetString context)
			throws DuplicateRegistrationException {
		entitySection.registerMOs(server, context);
		server.register(this.visionAlarmEntry, context);
		server.register(this.visionCurrentResultEntry, context);
        server.register(this.visionPhysicalTopLevelEntry, context);
	}

	@Override
	public void unregisterMOs(final MOServer server, final OctetString context) {
		entitySection.unregisterMOs(server, context);
		server.unregister(this.visionAlarmEntry, context);
		server.unregister(this.visionCurrentResultEntry, context);
        server.unregister(this.visionPhysicalTopLevelEntry, context);
	}

	/**
	 * @generated
	 */
	public void visionAlarmActiveState(
			final NotificationOriginator notificationOriginator,
			final OctetString context, final VariableBinding[] vbs) {
		if (vbs.length < 2) {
			throw new IllegalArgumentException("Too few notification objects: "
					+ vbs.length + "<2");
		}
		if (!(vbs[0].getOid().startsWith(oidTrapVarVisionAlarmIndex))) {
			throw new IllegalArgumentException("Variable 0 has wrong OID: "
					+ vbs[0].getOid() + " does not start with "
					+ oidTrapVarVisionAlarmIndex);
		}
		if (!visionAlarmEntryIndex.isValidIndex(visionAlarmEntry
				.getIndexPart(vbs[0].getOid()))) {
			throw new IllegalArgumentException(
					"Illegal index for variable 0 specified: "
							+ visionAlarmEntry.getIndexPart(vbs[0].getOid()));
		}
		if (!(vbs[1].getOid().startsWith(oidTrapVarVisionAlarmSourceId))) {
			throw new IllegalArgumentException("Variable 1 has wrong OID: "
					+ vbs[1].getOid() + " does not start with "
					+ oidTrapVarVisionAlarmSourceId);
		}
		if (!visionAlarmEntryIndex.isValidIndex(visionAlarmEntry
				.getIndexPart(vbs[1].getOid()))) {
			throw new IllegalArgumentException(
					"Illegal index for variable 1 specified: "
							+ visionAlarmEntry.getIndexPart(vbs[1].getOid()));
		}
		notificationOriginator.notify(context, oidVisionAlarmActiveState, vbs);
	}

	/**
	 * @generated
	 */
	public void visionAlarmClearState(
			final NotificationOriginator notificationOriginator,
			final OctetString context, final VariableBinding[] vbs) {
		if (vbs.length < 2) {
			throw new IllegalArgumentException("Too few notification objects: "
					+ vbs.length + "<2");
		}
		if (!(vbs[0].getOid().startsWith(oidTrapVarVisionAlarmIndex))) {
			throw new IllegalArgumentException("Variable 0 has wrong OID: "
					+ vbs[0].getOid() + " does not start with "
					+ oidTrapVarVisionAlarmIndex);
		}
		if (!visionAlarmEntryIndex.isValidIndex(visionAlarmEntry
				.getIndexPart(vbs[0].getOid()))) {
			throw new IllegalArgumentException(
					"Illegal index for variable 0 specified: "
							+ visionAlarmEntry.getIndexPart(vbs[0].getOid()));
		}
		if (!(vbs[1].getOid().startsWith(oidTrapVarVisionAlarmSourceId))) {
			throw new IllegalArgumentException("Variable 1 has wrong OID: "
					+ vbs[1].getOid() + " does not start with "
					+ oidTrapVarVisionAlarmSourceId);
		}
		if (!visionAlarmEntryIndex.isValidIndex(visionAlarmEntry
				.getIndexPart(vbs[1].getOid()))) {
			throw new IllegalArgumentException(
					"Illegal index for variable 1 specified: "
							+ visionAlarmEntry.getIndexPart(vbs[1].getOid()));
		}
		notificationOriginator.notify(context, oidVisionAlarmClearState, vbs);
	}
}
