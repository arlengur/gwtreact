/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mib;

import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PC;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PCFactory;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOTable;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableIndexValidator;
import org.snmp4j.agent.mo.MOTableModel;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.agent.mo.MOValueValidationEvent;
import org.snmp4j.agent.mo.MOValueValidationListener;
import org.snmp4j.agent.mo.snmp.smi.Constraint;
import org.snmp4j.agent.mo.snmp.smi.ConstraintsImpl;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraintValidator;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.Variable;

/**
 * Represents `entPhysicalTable` and 'entPhysicalContainsTable' elements
 * (originally from ENTITY-MIB). Should be bound to a set of OIDs. For details,
 * please refer to {@link #EntPhysicalMibSection(MOFactory, OID, OID)}.
 * 
 * This class was extracted from a more common generated file.
 * 
 * @author novohatskiy.r
 * 
 */
@SuppressWarnings({"rawtypes"})
public class EntPhysicalMibSection implements MOGroup {

	/**
	 * The <code>EntPhysicalAliasValidator</code> implements the value
	 * validation for <code>EntPhysicalAlias</code>.
	 * 
	 * @generated
	 */
	static class EntPhysicalAliasValidator implements MOValueValidationListener {

		@Override
		public void validate(final MOValueValidationEvent validationEvent) {
			final Variable newValue = validationEvent.getNewValue();
			final OctetString os = (OctetString) newValue;
			if (!(((os.length() >= 0) && (os.length() <= 32)))) {
				validationEvent
						.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_LENGTH);
				return;
			}

		}
	}

	/**
	 * The <code>EntPhysicalAssetIDValidator</code> implements the value
	 * validation for <code>EntPhysicalAssetID</code>.
	 * 
	 * @generated
	 */
	static class EntPhysicalAssetIDValidator
			implements
				MOValueValidationListener {

		@Override
		public void validate(final MOValueValidationEvent validationEvent) {
			final Variable newValue = validationEvent.getNewValue();
			final OctetString os = (OctetString) newValue;
			if (!((os.length() >= 0) && (os.length() <= 32))) {
				validationEvent
						.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_LENGTH);
				return;
			}

		}
	}

	/**
	 * @generated
	 */
	protected class EntPhysicalContainsEntryRow extends DefaultMOMutableRow2PC {
		public EntPhysicalContainsEntryRow(final OID index,
				final Variable[] values) {
			super(index, values);
		}

	}

	/**
	 * @generated
	 */
	protected class EntPhysicalContainsEntryRowFactory
			extends
				DefaultMOMutableRow2PCFactory {
		@Override
		public synchronized DefaultMOMutableRow2PC createRow(final OID index,
				final Variable[] values) throws UnsupportedOperationException {
			return createPhysicalContainsEntry(index, values);
		}
	}

	/**
	 * @generated
	 */
	protected class EntPhysicalEntryRow extends DefaultMOMutableRow2PC {
		public EntPhysicalEntryRow(final OID index, final Variable[] values) {
			super(index, values);
		}

		@Override
		public void setValue(final int column, final Variable value) {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * @generated
	 */
	protected class EntPhysicalEntryRowFactory
			extends
				DefaultMOMutableRow2PCFactory {
		@Override
		public synchronized DefaultMOMutableRow2PC createRow(final OID index,
				final Variable[] values) throws UnsupportedOperationException {
			final EntPhysicalEntryRow row = createPhysicalEntry(index, values);

			return row;
		}
	}

	/**
	 * The <code>EntPhysicalSerialNumValidator</code> implements the value
	 * validation for <code>EntPhysicalSerialNum</code>.
	 * 
	 * @generated
	 */
	static class EntPhysicalSerialNumValidator
			implements
				MOValueValidationListener {

		@Override
		public void validate(final MOValueValidationEvent validationEvent) {
			final Variable newValue = validationEvent.getNewValue();
			final OctetString os = (OctetString) newValue;
			if (!(((os.length() >= 0) && (os.length() <= 32)))) {
				validationEvent
						.setValidationStatus(SnmpConstants.SNMP_ERROR_WRONG_LENGTH);
				return;
			}

		}
	}

	public static final int ENTPHYSICAL_COLUMN_COUNT = 17;

	public static final int COL_ENTPHYSICALASSETID = 15;

	public static final int COL_ENTPHYSICALCLASS = 5;

	public static final int COL_ENTPHYSICALCONTAINEDIN = 4;

	// Column sub-identifer defintions for entPhysicalEntry:
	public static final int COL_ENTPHYSICALDESCR = 2;

	public static final int COL_ENTPHYSICALFIRMWAREREV = 9;

	public static final int COL_ENTPHYSICALHARDWAREREV = 8;

	public static final int COL_ENTPHYSICALISFRU = 16;

	public static final int COL_ENTPHYSICALMFGNAME = 12;

	public static final int COL_ENTPHYSICALMODELNAME = 13;

	// Column index defintions for entPhysicalEntry:
	public static final int COL_ENTPHYSICALNAME = 7;

	public static final int COL_ENTPHYSICALPARENTRELPOS = 6;

	public static final int COL_ENTPHYSICALSERIALNUM = 11;

	public static final int COL_ENTPHYSICALSOFTWAREREV = 10;

	public static final int COL_ENTPHYSICALVENDORTYPE = 3;

	public static final int COL_PHYSICALALIAS = 14;

	public static final int COL_ENTPHYSICALMFGDATE = 17;

	public static final int COL_ENTPHYSICALURIS = 18;

	// Column sub-identifer defintions for entPhysicalContainsEntry:
	public static final int colEntPhysicalChildIndex = 1;

	public static final int IDX_ENTPHYSICALALIAS = 12;

	public static final int IDX_ENTPHYSICALASSETID = 13;

	public static final int IDX_ENTPHYSICALCLASS = 3;

	public static final int IDX_ENTPHYSICALCONTAINEDIN = 2;

	public static final int IDX_ENTPHYSICALDESCR = 0;

	public static final int IDX_ENTPHYSICALFIRMWAREREV = 7;

	public static final int IDX_ENTPHYSICALHARDWAREREV = 6;

	public static final int IDX_ENTPHYSICALISFRU = 14;

	public static final int IDX_ENTPHYSICALMFGNAME = 10;

	public static final int IDX_ENTPHYSICALMODELNAME = 11;

	public static final int IDX_ENTPHYSICALNAME = 5;

	public static final int IDX_ENTPHYSICALPARENTPOS = 4;

	public static final int IDX_ENTPHYSICALSERIALNUM = 9;

	public static final int IDX_ENTPHYSICALSOFTWAREREV = 8;

	public static final int IDX_ENTPHYSICALVENDORTYPE = 1;

	public static final int IDX_ENTPHYSICALMFGDATE = 15;

	public static final int IDX_ENTPHYSICALURIS = 16;

	public static final int idxEntPhysicalChildIndex = 0;

	protected MOTable entPhysicalContainsEntry;

	private MOTableIndex entPhysicalContainsEntryIndex;

	private MOTableSubIndex[] entPhysicalContainsEntryIndexes;

	private MOTableModel entPhysicalContainsEntryModel;

	protected MOTable entPhysicalEntry;

	private MOTableIndex entPhysicalEntryIndex;

	private MOTableSubIndex[] entPhysicalEntryIndexes;

	private MOTableModel entPhysicalEntryModel;

	/**
	 * Constructs a mib instance and actually creates its
	 * <code>ManagedObject</code> instances using the supplied
	 * <code>MOFactory</code> and binds them to a provided OIDs. ).
	 * 
	 * @param moFactory
	 *            the <code>MOFactory</code> to be used to create the managed
	 *            objects for this module.
	 * @param oidEntPhysicalEntry
	 *            - the OID to which entPhysicalEntry will be bound.
	 * @param oidEntPhysicalContainsEntry
	 *            - the OID to which entPhysicalContainsEntry will be bound.
	 */
	public EntPhysicalMibSection(final MOFactory moFactory,
			final OID oidEntPhysicalEntry, final OID oidEntPhysicalContainsEntry) {
		createMO(moFactory, oidEntPhysicalEntry, oidEntPhysicalContainsEntry);
	}

	private void createEntPhysicalContainsEntry(final MOFactory moFactory,
			final OID oidEntPhysicalContainsEntry) {
		// Index definition
		entPhysicalContainsEntryIndexes = new MOTableSubIndex[]{
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1),
				moFactory.createSubIndex(null, SMIConstants.SYNTAX_INTEGER, 1,
						1)};

		entPhysicalContainsEntryIndex = moFactory.createIndex(
				entPhysicalContainsEntryIndexes, false,
				new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		// Columns
		final MOColumn[] entPhysicalContainsEntryColumns = new MOColumn[1];
		entPhysicalContainsEntryColumns[idxEntPhysicalChildIndex] = moFactory
				.createColumn(
						colEntPhysicalChildIndex,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		// Table model
		entPhysicalContainsEntryModel = moFactory.createTableModel(
				oidEntPhysicalContainsEntry, entPhysicalContainsEntryIndex,
				entPhysicalContainsEntryColumns);
		entPhysicalContainsEntry = moFactory.createTable(
				oidEntPhysicalContainsEntry, entPhysicalContainsEntryIndex,
				entPhysicalContainsEntryColumns, entPhysicalContainsEntryModel);
	}

	private void createEntPhysicalEntry(final MOFactory moFactory,
			final OID oidEntPhysicalEntry) {
		// Index definition
		entPhysicalEntryIndexes = new MOTableSubIndex[]{moFactory
				.createSubIndex(new OID(), SMIConstants.SYNTAX_INTEGER, 1, 1)};

		entPhysicalEntryIndex = moFactory.createIndex(entPhysicalEntryIndexes,
				false, new MOTableIndexValidator() {
					@Override
					public boolean isValidIndex(final OID index) {
						return true;
					}
				});

		// Columns
		final MOColumn[] entPhysicalEntryColumns = new MOColumn[ENTPHYSICAL_COLUMN_COUNT];
		entPhysicalEntryColumns[IDX_ENTPHYSICALDESCR] = moFactory.createColumn(
				COL_ENTPHYSICALDESCR, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALVENDORTYPE] = moFactory
				.createColumn(
						COL_ENTPHYSICALVENDORTYPE,
						SMIConstants.SYNTAX_OBJECT_IDENTIFIER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALCONTAINEDIN] = moFactory
				.createColumn(
						COL_ENTPHYSICALCONTAINEDIN,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALCLASS] = moFactory.createColumn(
				COL_ENTPHYSICALCLASS, SMIConstants.SYNTAX_INTEGER,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALPARENTPOS] = moFactory
				.createColumn(
						COL_ENTPHYSICALPARENTRELPOS,
						SMIConstants.SYNTAX_INTEGER,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALNAME] = moFactory.createColumn(
				COL_ENTPHYSICALNAME, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALHARDWAREREV] = moFactory
				.createColumn(
						COL_ENTPHYSICALHARDWAREREV,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALFIRMWAREREV] = moFactory
				.createColumn(
						COL_ENTPHYSICALFIRMWAREREV,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALSOFTWAREREV] = moFactory
				.createColumn(
						COL_ENTPHYSICALSOFTWAREREV,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALSERIALNUM] = new MOMutableColumn(
				COL_ENTPHYSICALSERIALNUM, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
				null);
		final ValueConstraint entPhysicalSerialNumVC = new ConstraintsImpl();
		((ConstraintsImpl) entPhysicalSerialNumVC).add(new Constraint(0L, 32L));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALSERIALNUM])
				.addMOValueValidationListener(new ValueConstraintValidator(
						entPhysicalSerialNumVC));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALSERIALNUM])
				.addMOValueValidationListener(new EntPhysicalSerialNumValidator());
		entPhysicalEntryColumns[IDX_ENTPHYSICALMFGNAME] = moFactory
				.createColumn(
						COL_ENTPHYSICALMFGNAME,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALMODELNAME] = moFactory
				.createColumn(
						COL_ENTPHYSICALMODELNAME,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALALIAS] = new MOMutableColumn(
				COL_PHYSICALALIAS, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
				null);
		final ValueConstraint entPhysicalAliasVC = new ConstraintsImpl();
		((ConstraintsImpl) entPhysicalAliasVC).add(new Constraint(0L, 32L));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALALIAS])
				.addMOValueValidationListener(new ValueConstraintValidator(
						entPhysicalAliasVC));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALALIAS])
				.addMOValueValidationListener(new EntPhysicalAliasValidator());
		entPhysicalEntryColumns[IDX_ENTPHYSICALASSETID] = new MOMutableColumn(
				COL_ENTPHYSICALASSETID, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE),
				null);
		final ValueConstraint entPhysicalAssetIDVC = new ConstraintsImpl();
		((ConstraintsImpl) entPhysicalAssetIDVC).add(new Constraint(0L, 32L));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALASSETID])
				.addMOValueValidationListener(new ValueConstraintValidator(
						entPhysicalAssetIDVC));
		((MOMutableColumn) entPhysicalEntryColumns[IDX_ENTPHYSICALASSETID])
				.addMOValueValidationListener(new EntPhysicalAssetIDValidator());
		entPhysicalEntryColumns[IDX_ENTPHYSICALISFRU] = moFactory.createColumn(
				COL_ENTPHYSICALISFRU, SMIConstants.SYNTAX_INTEGER,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALMFGDATE] = moFactory
				.createColumn(
						COL_ENTPHYSICALMFGDATE,
						SMIConstants.SYNTAX_OCTET_STRING,
						moFactory
								.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
		entPhysicalEntryColumns[IDX_ENTPHYSICALURIS] = moFactory.createColumn(
				COL_ENTPHYSICALURIS, SMIConstants.SYNTAX_OCTET_STRING,
				moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_WRITE));
		// Table model
		entPhysicalEntryModel = moFactory.createTableModel(oidEntPhysicalEntry,
				entPhysicalEntryIndex, entPhysicalEntryColumns);
		entPhysicalEntry = moFactory.createTable(oidEntPhysicalEntry,
				entPhysicalEntryIndex, entPhysicalEntryColumns,
				entPhysicalEntryModel);
	}

	/**
	 * Create the ManagedObjects defined for this MIB module using the specified
	 * {@link MOFactory}.
	 * 
	 * @param moFactory
	 *            the <code>MOFactory</code> instance to use for object
	 *            creation.
	 */
	protected void createMO(final MOFactory moFactory,
			final OID oidEntPhysicalEntry, final OID oidEntPhysicalContainsEntry) {
		createEntPhysicalEntry(moFactory, oidEntPhysicalEntry);
		createEntPhysicalContainsEntry(moFactory, oidEntPhysicalContainsEntry);
	}

	/**
	 * Create PhysicalContainsEntry
	 * 
	 * @generated
	 * 
	 * @param index
	 * @param values
	 * @return EntPhysicalContainsEntryRow
	 */
	protected EntPhysicalContainsEntryRow createPhysicalContainsEntry(
			final OID index, final Variable[] values) {
		return new EntPhysicalContainsEntryRow(index, values);
	}

	/**
	 * @generated
	 */
	protected EntPhysicalEntryRow createPhysicalEntry(final OID index,
			final Variable[] values) {
		return new EntPhysicalEntryRow(index, values);
	}

	/**
	 * @generated
	 */
	public MOTable getEntPhysicalContainsEntry() {
		return entPhysicalContainsEntry;
	}

	/**
	 * @generated
	 */
	public MOTable getEntPhysicalEntry() {
		return entPhysicalEntry;
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
		server.register(this.entPhysicalEntry, context);
		server.register(this.entPhysicalContainsEntry, context);
	}

	@Override
	public void unregisterMOs(final MOServer server, final OctetString context) {
		server.unregister(this.entPhysicalEntry, context);
		server.unregister(this.entPhysicalContainsEntry, context);
	}

}
