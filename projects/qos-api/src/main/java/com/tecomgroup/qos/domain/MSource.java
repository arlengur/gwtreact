/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.tecomgroup.qos.HasUniqueKey;
import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * The main class (in the domain entities hierarchy) represents entities which
 * can be regarded as source for some other entites of the system. <br />
 * 
 * For example, {@link MPolicy} can be an originator for {@link MAlert}.
 * {@link MAgent}, {@link MAgentModule}, {@link MAgentTask} can be sources for
 * {@link MAlert}.
 * 
 * @author kunilov.p
 * 
 */

@SuppressWarnings("serial")
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MSource extends MLoggedEntity
		implements
			UpdatableEntity<MSource>,
			HasUniqueKey {

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	protected MSource parent;

	@Column(nullable = false, updatable = false, unique = true, name = "entity_key")
	protected String key;

	@Column(nullable = false)
	protected String displayName;

	@Column(nullable = false, insertable = false, updatable = false, unique = true, name = "snmp_id")
	@Generated(GenerationTime.INSERT)
	private int snmpId;

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the key
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * @return the parent
	 */
	public MSource getParent() {
		return parent;
	}

	/**
	 * @return the snmpId
	 */
	public int getSnmpId() {
		return snmpId;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(final MSource parent) {
		this.parent = parent;
	}

	/**
	 * @param snmpId
	 *            the snmpId to set
	 */
	public void setSnmpId(final int snmpId) {
		this.snmpId = snmpId;
	}

	@Override
	public String toString() {
		return "{key = " + getKey() + ", displayName = " + getDisplayName()
				+ "}";
	}

	@Override
	public boolean updateSimpleFields(final MSource source) {
		boolean isUpdated = false;

		if (source != null) {
			if (!equals(getDisplayName(), source.getDisplayName())) {
				setDisplayName(source.getDisplayName());
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}
