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
 * Textual convention ItuPerceivedSeverity from ITU-ALARM-TC-MIB.
 * 
 * This class was extracted from a more common generated file.
 * 
 * @generated
 * @author novohatskiy.r
 */
public class ItuPerceivedSeverity implements TextualConvention<Variable> {

	private static final String TC_MODULE_ITU_ALARM_TC_MIB = "ITU-ALARM-TC-MIB";
	private static final String TC_ITUPERCEIVEDSEVERITY = "ItuPerceivedSeverity";

	public static final int cleared = 1;
	public static final int indeterminate = 2;
	public static final int critical = 3;
	public static final int major = 4;
	public static final int minor = 5;
	public static final int warning = 6;

	private final MOFactory moFactory;

	public ItuPerceivedSeverity(final MOFactory moFactory) {
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
			final ValueConstraint vc = new EnumerationConstraint(new int[]{
					cleared, indeterminate, critical, major, minor, warning});
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
		final ValueConstraint vc = new EnumerationConstraint(new int[]{cleared,
				indeterminate, critical, major, minor, warning});
		scalar.addMOValueValidationListener(new ValueConstraintValidator(vc));
		return scalar;
	}

	@Override
	public String getModuleName() {
		return TC_MODULE_ITU_ALARM_TC_MIB;
	}

	@Override
	public String getName() {
		return TC_ITUPERCEIVEDSEVERITY;
	}
}