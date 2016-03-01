/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp.mib.generated.tc.vision;

import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.smi.Constraint;
import org.snmp4j.agent.mo.snmp.smi.ConstraintsImpl;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraintValidator;
import org.snmp4j.agent.mo.snmp.tc.TextualConvention;
import org.snmp4j.smi.AssignableFromLong;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

import com.tecomgroup.qos.snmp.mib.QligentVisionMib;

/**
 * Textual convention Float64TC from QLIGENT-VISION-MIB.
 * 
 * This class was extracted from a more common generated file.
 * 
 * @generated
 * @author novohatskiy.r
 * 
 */
public class Float64TC implements TextualConvention<Variable> {

	private static final String TC_FLOAT64TC = "Float64TC";

	private final MOFactory moFactory;

	public Float64TC(final MOFactory moFactory) {
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
			final ValueConstraint vc = new ConstraintsImpl();
			((ConstraintsImpl) vc).add(new Constraint(8L, 8L));
			mcol.addMOValueValidationListener(new ValueConstraintValidator(vc));
		}
		return col;
	}

	@Override
	public Variable createInitialValue() {
		final Variable v = new OctetString();
		if (v instanceof AssignableFromLong) {
			((AssignableFromLong) v).setValue(8L);
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MOScalar<Variable> createScalar(final OID oid,
			final MOAccess access, final Variable value) {
		final MOScalar<Variable> scalar = moFactory.createScalar(oid, access,
				value);
		final ValueConstraint vc = new ConstraintsImpl();
		((ConstraintsImpl) vc).add(new Constraint(8L, 8L));
		scalar.addMOValueValidationListener(new ValueConstraintValidator(vc));
		return scalar;
	}

	@Override
	public String getModuleName() {
		return QligentVisionMib.TC_MODULE_QLIGENT_VISION_MIB;
	}

	@Override
	public String getName() {
		return TC_FLOAT64TC;

	}
}