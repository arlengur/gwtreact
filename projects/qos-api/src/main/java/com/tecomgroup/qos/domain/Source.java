/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.pm.MPolicySharedData;
import com.tecomgroup.qos.exception.UnknownSourceException;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class Source implements Serializable {

	public enum Type {
		AGENT, SERVER, TASK, POLICY, POLICY_MANAGER, MODULE, STREAM
	}

	public static final String KEY_SEPARATOR = "#";

	/**
	 * Converts collection of agents to collection of sources.
	 * 
	 * @param agents
	 * @return
	 */
	public static Collection<Source> convertAgentsToSources(
			final Collection<MAgent> agents) {
		final List<Source> sources = new ArrayList<Source>();
		for (final MAgent agent : agents) {
			sources.add(Source.getAgentSource(agent.getKey()));
		}
		return sources;
	}

	/**
	 * 
	 * @param agentName
	 * @return agent source
	 */
	public static Source getAgentSource(final String agentName) {
		return new Source(Type.AGENT, agentName);
	}

	/**
	 * 
	 * @param agentName
	 * @param moduleName
	 * @return
	 */
	public static Source getModuleSource(final String agentName,
			final String moduleName) {
		return new Source(Type.MODULE, agentName + KEY_SEPARATOR + moduleName);
	}

	/**
	 * 
	 * @param policyManagerName
	 * @return
	 */
	public static Source getPolicyManagerSource(final String policyManagerName) {
		return new Source(Type.POLICY_MANAGER, policyManagerName);
	}

	/**
	 * 
	 * @param policyKey
	 * @return
	 */
	public static Source getPolicySource(final String policyKey) {
		return new Source(Type.POLICY, policyKey);
	}

	/**
	 * 
	 * @param serverName
	 * @return
	 */
	public static Source getServerSource(final String serverName) {
		return new Source(Type.SERVER, serverName);
	}

	public static Source getSource(final MSource source) {
		Source result = null;
		if (source != null) {
			if (source instanceof MAgentTask) {
				result = Source.getTaskSource(source.getKey());
			} else if (source instanceof MPolicySharedData) {
				result = Source.getPolicySource(source.getKey());
			} else if (source instanceof MAgentModule) {
				final MSource parent = source.getParent();
				if (parent != null) {
					result = Source.getModuleSource(parent.getKey(),
							source.getKey());
				}
			} else if (source instanceof MAgent) {
				result = Source.getAgentSource(source.getKey());
			} else {
				throw new UnknownSourceException("Usupported source type "
						+ source.getClass().getName());
			}
		}
		return result;
	}

	public static Source getStreamSource(final String taskKey,
			final String streamKey) {
		return new Source(Type.STREAM, taskKey + KEY_SEPARATOR + streamKey);
	}

	/**
	 * 
	 * @param taskKey
	 * @return
	 */
	public static Source getTaskSource(final String taskKey) {
		return new Source(Type.TASK, taskKey);
	}

	@Transient
	private String displayName;
	@Column(nullable = false)
	private String key;
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type type;
	protected Source() {
		super();
	}

	private Source(final Type type, final String key) {
		this();
		this.key = key;
		this.type = type;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Source other = (Source) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	public String getSimpleKey(final int index) {
		if (index < 0) {
			throw new IllegalArgumentException("Index should be positive");
		}
		final String[] keys = getKey().split("\\" + KEY_SEPARATOR);
		if (index >= keys.length) {
			throw new IllegalArgumentException("Index " + index
					+ " should be less than " + keys.length);
		}
		return keys[index];
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@JsonIgnore
	public boolean isKeySimple() {
		return !key.contains(KEY_SEPARATOR);
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
	 * @param type
	 *            the type to set
	 */
	protected void setType(final Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "{" + type + " = " + key + "}";
	}
}
