/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mib;

import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.NotificationOriginator;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PC;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PCFactory;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableIndexValidator;
import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

/**
 * Represents ENTITY-MIB.
 * 
 * @author novohatskiy.r
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityMib implements MOGroup {

	/**
	 * @generated
	 */
	public class EntAliasMappingEntryRow extends DefaultMOMutableRow2PC {
		public EntAliasMappingEntryRow(final OID index, final Variable[] values) {
			super(index, values);
		}

	}

	/**
	 * @generated
	 */
	class EntAliasMappingEntryRowFactory extends DefaultMOMutableRow2PCFactory {
		@Override
		public synchronized DefaultMOMutableRow2PC createRow(final OID index,
				final Variable[] values) throws UnsupportedOperationException {
			final EntAliasMappingEntryRow row = new EntAliasMappingEntryRow(
					index, values);

			return row;
		}
	}

	/**
	 * @generated
	 */
	public class EntLogicalEntryRow extends DefaultMOMutableRow2PC {
		public EntLogicalEntryRow(final OID index, final Variable[] values) {
			super(index, values);
		}

	}

	class EntLogicalEntryRowFactory extends DefaultMOMutableRow2PCFactory {
		@Override
		public synchronized DefaultMOMutableRow2PC createRow(final OID index,
				final Variable[] values) throws UnsupportedOperationException {
			final EntLogicalEntryRow row = new EntLogicalEntryRow(index, values);

			return row;
		}
	}

	/**
	 * @generated
	 */
	public class EntLPMappingEntryRow extends DefaultMOMutableRow2PC {
		public EntLPMappingEntryRow(final OID index, final Variable[] values) {
			super(index, values);
		}

	}

	/**
	 * @generated
	 */
	class EntLPMappingEntryRowFactory extends DefaultMOMutableRow2PCFactory {
		@Override
		public synchronized DefaultMOMutableRow2PC createRow(final OID index,
				final Variable[] values) throws UnsupportedOperationException {
			final EntLPMappingEntryRow row = new EntLPMappingEntryRow(index,
					values);

			return row;
		}
	}

	private final EntPhysicalMibSection physicalSection;

	// Column sub-identifier definitions for entAliasMappingEntry:
	public static final int colEntAliasMappingIdentifier = 2;

	public static final int colEntLogicalCommunity = 4;

	public static final int colEntLogicalContextEngineID = 7;

	public static final int colEntLogicalContextName = 8;

	// Column sub-identifier definitions for entLogicalEntry:
	public static final int colEntLogicalDescr = 2;

	public static final int colEntLogicalTAddress = 5;

	public static final int colEntLogicalTDomain = 6;

	public static final int colEntLogicalType = 3;

	// Column sub-identifier definitions for entLPMappingEntry:
	public static final int colEntLPPhysicalIndex = 1;

	// Column index definitions for entAliasMappingEntry:
	public static final int idxEntAliasMappingIdentifier = 0;

	public static final int idxEntLogicalCommunity = 2;

	public static final int idxEntLogicalContextEngineID = 5;

	public static final int idxEntLogicalContextName = 6;

	// Column index definitions for entLogicalEntry:
	public static final int idxEntLogicalDescr = 0;

	public static final int idxEntLogicalTAddress = 3;

	public static final int idxEntLogicalTDomain = 4;

	public static final int idxEntLogicalType = 1;

	public static final int idxEntLPPhysicalIndex = 0;

	public static final OID oidEntPhysicalContainsEntry = new OID(new int[]{1,
			3, 6, 1, 2, 1, 47, 1, 3, 3, 1});

	public static final OID oidEntPhysicalEntry = new OID(new int[]{1, 3, 6, 1,
			2, 1, 47, 1, 1, 1, 1});

	public static final OID oidEntPhysicalIndex = new OID(new int[]{1, 3, 6, 1,
			2, 1, 47, 1, 1, 1, 1, 1});

	public static final OID oidEntPhysicalDescr = new OID(new int[]{1, 3, 6, 1,
			2, 1, 47, 1, 1, 1, 1, 2});

	public static final OID oidEntAliasMappingEntry = new OID(new int[]{1, 3,
			6, 1, 2, 1, 47, 1, 3, 2, 1});

	public static final OID oidEntConfigChange = new OID(new int[]{1, 3, 6, 1,
			2, 1, 47, 2, 0, 1});

	public static final OID oidEntLastChangeTime = new OID(new int[]{1, 3, 6,
			1, 2, 1, 47, 1, 4, 1, 0});

	public static final OID oidEntLogicalEntry = new OID(new int[]{1, 3, 6, 1,
			2, 1, 47, 1, 2, 1, 1});

	public static final OID oidEntLPMappingEntry = new OID(new int[]{1, 3, 6,
			1, 2, 1, 47, 1, 3, 1, 1});

	private static final String TC_MODULE_SNMPV2_TC = "SNMPv2-TC";

	private static final String TC_TIMESTAMP = "TimeStamp";

	private MOTable entAliasMappingEntry;

	private MOTableIndex entAliasMappingEntryIndex;

	private MOTableSubIndex[] entAliasMappingEntryIndexes;

	private MOTableModel entAliasMappingEntryModel;

	private MOScalar entLastChangeTime;

	private MOTable entLogicalEntry;

	private MOTableIndex entLogicalEntryIndex;

	private MOTableSubIndex[] entLogicalEntryIndexes;

	private MOTableModel entLogicalEntryModel;

	private MOTable entLPMappingEntry;

	private MOTableIndex entLPMappingEntryIndex;

	private MOTableSubIndex[] entLPMappingEntryIndexes;

	private MOTableModel entLPMappingEntryModel;

	/**
	 * Constructs an ENTITY-MIB instance and actually creates its
	 * <code>ManagedObject</code> instances using the supplied
	 * <code>MOFactory</code>.
	 * 
	 * @param moFactory
	 *            the <code>MOFactory</code> to be used to create the managed
	 *            objects for this module.
	 */
	public EntityMib(final MOFactory moFactory) {
		physicalSection = new EntPhysicalMibSection(moFactory,
				oidEntPhysicalEntry, oidEntPhysicalContainsEntry);
		createMO(moFactory);
	}

	/**
	 * @generated
	 */
	private void createEntAliasMappingEntry(final MOFactory moFactory) {
		// Index definition
		entAliasMappingEntryIndexes = new MOTableSubIndex[]{
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1),
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1)};

		entAliasMappingEntryIndex = moFactory.createIndex(
				entAliasMappingEntryIndexes, false,
				new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		// Columns
		final MOColumn[] entAliasMappingEntryColumns = new MOColumn[1];
		entAliasMappingEntryColumns[idxEntAliasMappingIdentifier] = moFactory
				.createColumn(
						colEntAliasMappingIdentifier,
						SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		// Table model
		entAliasMappingEntryModel = moFactory.createTableModel(
				oidEntAliasMappingEntry, entAliasMappingEntryIndex,
				entAliasMappingEntryColumns);
		((MOMutableTableModel) entAliasMappingEntryModel)
				.setRowFactory(new EntAliasMappingEntryRowFactory());
		entAliasMappingEntry = moFactory.createTable(oidEntAliasMappingEntry,
				entAliasMappingEntryIndex, entAliasMappingEntryColumns,
				entAliasMappingEntryModel);
	}

	/**
	 * @generated
	 */
	private void createEntLogicalEntry(final MOFactory moFactory) {
		// Index definition
		entLogicalEntryIndexes = new MOTableSubIndex[]{moFactory
				.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1, 1)};

		entLogicalEntryIndex = moFactory.createIndex(entLogicalEntryIndexes,
				false, new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		// Columns
		final MOColumn[] entLogicalEntryColumns = new MOColumn[7];
		entLogicalEntryColumns[idxEntLogicalDescr] = moFactory.createColumn(
				colEntLogicalDescr, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalType] = moFactory.createColumn(
				colEntLogicalType, SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalCommunity] = moFactory
				.createColumn(
						colEntLogicalCommunity,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalTAddress] = moFactory.createColumn(
				colEntLogicalTAddress, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalTDomain] = moFactory.createColumn(
				colEntLogicalTDomain, SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalContextEngineID] = moFactory
				.createColumn(
						colEntLogicalContextEngineID,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entLogicalEntryColumns[idxEntLogicalContextName] = moFactory
				.createColumn(
						colEntLogicalContextName,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		// Table model
		entLogicalEntryModel = moFactory.createTableModel(oidEntLogicalEntry,
				entLogicalEntryIndex, entLogicalEntryColumns);
		((MOMutableTableModel) entLogicalEntryModel)
				.setRowFactory(new EntLogicalEntryRowFactory());
		entLogicalEntry = moFactory.createTable(oidEntLogicalEntry,
				entLogicalEntryIndex, entLogicalEntryColumns,
				entLogicalEntryModel);
	}

	/**
	 * @generated
	 */
	private void createEntLPMappingEntry(final MOFactory moFactory) {
		// Index definition
		entLPMappingEntryIndexes = new MOTableSubIndex[]{
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1),
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1)};

		entLPMappingEntryIndex = moFactory.createIndex(
				entLPMappingEntryIndexes, false, new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		// Columns
		final MOColumn[] entLPMappingEntryColumns = new MOColumn[1];
		entLPMappingEntryColumns[idxEntLPPhysicalIndex] = moFactory
				.createColumn(
						colEntLPPhysicalIndex,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		// Table model
		entLPMappingEntryModel = moFactory.createTableModel(
				oidEntLPMappingEntry, entLPMappingEntryIndex,
				entLPMappingEntryColumns);
		((MOMutableTableModel) entLPMappingEntryModel)
				.setRowFactory(new EntLPMappingEntryRowFactory());
		entLPMappingEntry = moFactory.createTable(oidEntLPMappingEntry,
				entLPMappingEntryIndex, entLPMappingEntryColumns,
				entLPMappingEntryModel);
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
		entLastChangeTime = moFactory.createScalar(oidEntLastChangeTime,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY),
				new TimeTicks(), TC_MODULE_SNMPV2_TC, TC_TIMESTAMP);
		createEntLogicalEntry(moFactory);
		createEntLPMappingEntry(moFactory);
		createEntAliasMappingEntry(moFactory);
		physicalSection.createMO(moFactory, oidEntPhysicalEntry,
				oidEntPhysicalContainsEntry);
	}

	/**
	 * @generated
	 */
	public void entConfigChange(
			final NotificationOriginator notificationOriginator,
			final OctetString context, final VariableBinding[] vbs) {
		notificationOriginator.notify(context, oidEntConfigChange, vbs);
	}

	/**
	 * @generated
	 */
	public MOTable getEntAliasMappingEntry() {
		return entAliasMappingEntry;
	}

	/**
	 * @generated
	 */
	public MOTable getEntLogicalEntry() {
		return entLogicalEntry;
	}

	/**
	 * @generated
	 */
	public MOTable getEntLPMappingEntry() {
		return entLPMappingEntry;
	}

	/**
	 * Added for overriding in derived class.
	 * 
	 * protected abstract void processEntityPhysicalEntry(OID rowIndex, int
	 * column, Variable newValue);
	 */
	@Override
	public void registerMOs(final MOServer server, final OctetString context)
			throws DuplicateRegistrationException {
		server.register(this.entLastChangeTime, context);
		server.register(this.entLogicalEntry, context);
		server.register(this.entLPMappingEntry, context);
		server.register(this.entAliasMappingEntry, context);
		physicalSection.registerMOs(server, context);
	}

	@Override
	public void unregisterMOs(final MOServer server, final OctetString context) {
		server.unregister(this.entLastChangeTime, context);
		server.unregister(this.entLogicalEntry, context);
		server.unregister(this.entLPMappingEntry, context);
		server.unregister(this.entAliasMappingEntry, context);
		physicalSection.unregisterMOs(server, context);
	}

	/**
	 * @generated
	 */
	protected void updateEntLastChangeTime(final TimeTicks time) {
		entLastChangeTime.setValue(time);
	}
}
