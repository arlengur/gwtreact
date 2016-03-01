/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.Source.Type;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class MStreamWrapper extends MAbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public static String generateUniqueKey(final String taskKey,
			final String streamKey) {
		return taskKey + "." + streamKey;
	}

	@Column(nullable = false)
	protected String taskKey;

	@Column(nullable = false)
	protected String streamKey;

	@Transient
	@JsonIgnore
	protected MStream stream;

	@Transient
	@JsonIgnore
	protected MAgent agent;

	public MStreamWrapper() {
		super();
	}

	public MStreamWrapper(final MStreamWrapper streamWrapper) {
		this();
		setStreamKey(streamWrapper.getStreamKey());
		setTaskKey(streamWrapper.getTaskKey());
		setAgent(streamWrapper.getAgent());
		setStream(streamWrapper.getStream());
	}

	/**
	 * @return the agent
	 */
	public MAgent getAgent() {
		return agent;
	}

	@Transient
	public String getAgentDisplayName() {
		return agent == null ? null : agent.getDisplayName();
	}

	/**
	 * @return the stream
	 */
	public MStream getStream() {
		return stream;
	}

	/**
	 * @return the sourceKey
	 */
	public String getStreamKey() {
		return streamKey;
	}

	/**
	 * @return the taskKey
	 */
	public String getTaskKey() {
		return taskKey;
	}

	@Transient
	public String getTimeZone() {
		return agent == null ? null : agent.getTimeZone();
	}

	public String getUniqueKey() {
		return MStreamWrapper.generateUniqueKey(taskKey, streamKey);
	}

	@Transient
	@JsonIgnore
	public boolean isValid() {
		return stream != null && agent != null;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(final MAgent agent) {
		this.agent = agent;
	}

	/**
	 * @param stream
	 *            the stream to set
	 */
	public void setStream(final MStream stream) {
		this.stream = stream;
		if (stream != null) {
			if (stream.getSource() != null) {
				if (taskKey == null
						&& stream.getSource().getType() == Type.TASK) {
					taskKey = stream.getSource().getKey();
				}
			}
			if (streamKey == null) {
				streamKey = stream.getKey();
			}
		}
	}
	/**
	 * @param sourceKey
	 *            the sourceKey to set
	 */
	public void setStreamKey(final String sourceKey) {
		this.streamKey = sourceKey;
	}

	/**
	 * @param taskKey
	 *            the taskKey to set
	 */
	public void setTaskKey(final String taskKey) {
		this.taskKey = taskKey;
	}

	@Override
	public String toString() {
		return "stream wrapper: task=" + taskKey + ", stream=" + streamKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
