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
import org.snmp4j.agent.mo.snmp.smi.EnumerationConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraint;
import org.snmp4j.agent.mo.snmp.smi.ValueConstraintValidator;
import org.snmp4j.agent.mo.snmp.tc.TextualConvention;
import org.snmp4j.smi.AssignableFromLong;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import com.tecomgroup.qos.snmp.mib.QligentVisionMib;

/**
 * Textual convention VisionAlarmUpdateType from QLIGENT-VISION-MIB.
 * 
 * This class was extracted from a more common generated file.
 * 
 * @generated
 * @author novohatskiy.r
 * 
 */
public class VisionAlarmUpdateType implements TextualConvention<Variable> {

	private static final String TC_VISIONALARMUPDATETYPE = "VisionAlarmUpdateType";

	public static final int _new = 1;
	public static final int repeat = 2;
	public static final int update = 3;
	public static final int severityUpgrade = 4;
	public static final int severityDegradation = 5;
	public static final int autoCleared = 6;
	public static final int ack = 7;
	public static final int unack = 8;
	public static final int operatorCleared = 9;
	public static final int operatorDeleted = 10;
	public static final int comment = 11;
	public static final int agentRestart = 12;

	private final MOFactory moFactory;

	public VisionAlarmUpdateType(final MOFactory moFactory) {
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
					_new, repeat, update, severityUpgrade, severityDegradation,
					autoCleared, ack, unack, operatorCleared, operatorDeleted,
					comment, agentRestart});
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
		final ValueConstraint vc = new EnumerationConstraint(new int[]{_new,
				repeat, update, severityUpgrade, severityDegradation,
				autoCleared, ack, unack, operatorCleared, operatorDeleted,
				comment, agentRestart});
		scalar.addMOValueValidationListener(new ValueConstraintValidator(vc));
		return scalar;
	}

	@Override
	public String getModuleName() {
		return QligentVisionMib.TC_MODULE_QLIGENT_VISION_MIB;
	}

	@Override
	public String getName() {
		return TC_VISIONALARMUPDATETYPE;
	}
}
