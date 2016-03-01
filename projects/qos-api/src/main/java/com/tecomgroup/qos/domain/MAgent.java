/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.tecomgroup.qos.Deleted;

/**
 * Класс для представления Блока Контроля.
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MAgent extends MSystemComponent implements Deleted {
	@Column(nullable = false)
	private boolean deleted = false;

	/**
	 * Сетевой адрес БК (IP/hostname)
	 */
	private String netAddress;

	/**
	 * Структурное подразделение, в который входит БК.
	 * 
	 * @uml.property name="division"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	private MDivision division;

	/**
	 * Выбранный текущий профиль.
	 * 
	 * @uml.property name="selectedProfile"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	private MProfile selectedProfile;

	/**
	 * Аппаратная и программная платформа БК
	 */
	private String platform;

	/**
	 * Набор всех сконфигуренных профилей для БК.
	 * 
	 * @uml.property name="profiles"
	 */
	@OneToMany(cascade = {CascadeType.ALL})
	private Set<MProfile> profiles;

	/**
	 * Набор динамически создаваемых пропертей для БК.
	 * 
	 * @uml.property name="properties"
	 */
	@OneToMany(cascade = {CascadeType.ALL})
	private Set<MProperty> properties;

	/**
	 * {@link MSource#key}
	 */
	@Formula("entity_key")
	@Deprecated
	private String name;

	/**
	 * @uml.property name="description"
	 */
	@Column(length = 1024)
	private String description;

	/**
	 * Тип блока контроля (внешний - доступен из внешней сети, внутренний -
	 * доступен только из внутренней сети)
	 * 
	 * @uml.property name="agentType"
	 */
	@Enumerated(EnumType.STRING)
	private MAgentType agentType;

	/**
	 * @uml.property name="gis"
	 */
	@Embedded
	private GISPosition gisPosition;

	/**
	 * @uml.property name="timeZone"
	 */
	private String timeZone;

	public enum AgentRegistrationState{

		NO_STATE(0), ACCEPTED(1),SUCCESS(2), IN_PROGRESS(3), PARTIALLY(4), FAILED(5);

		private int state;

		AgentRegistrationState(final int state) {
			this.state = state;
		}
	}

	/**
	 * Getter of the property <tt>agentType</tt>
	 * 
	 * @return Returns the agentType.
	 * @uml.property name="agentType"
	 */
	public MAgentType getAgentType() {
		return agentType;
	}

	/**
	 * Getter of the property <tt>description</tt>
	 * 
	 * @return Returns the description.
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Getter of the property <tt>division</tt>
	 * 
	 * @return Returns the division.
	 * @uml.property name="division"
	 */
	public MDivision getDivision() {
		return division;
	}

	/**
	 * @return the gisPosition
	 */
	public GISPosition getGisPosition() {
		return gisPosition;
	}

	/**
	 * {@link MSource#getKey()}
	 */
	@Deprecated
	@Transient
	public String getName() {
		return getKey();
	}

	/**
	 * @return the netAddress
	 */
	public String getNetAddress() {
		return netAddress;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

	/**
	 * Getter of the property <tt>profiles</tt>
	 * 
	 * @return Returns the profiles.
	 * @uml.property name="profiles"
	 */
	public Set<MProfile> getProfiles() {
		return profiles;
	}

	/**
	 * Getter of the property <tt>properties</tt>
	 * 
	 * @return Returns the properties.
	 * @uml.property name="properties"
	 */
	public Set<MProperty> getProperties() {
		return properties;
	}

	/**
	 * Getter of the property <tt>profile</tt>
	 * 
	 * @return Returns the profile.
	 * @uml.property name="selectedProfile"
	 */
	public MProfile getSelectedProfile() {
		return selectedProfile;
	}

	/**
	 * Getter of the property <tt>timeZone</tt>
	 * 
	 * @return Returns the timeZone.
	 * @uml.property name="timeZone"
	 */
	public String getTimeZone() {
		return timeZone;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Setter of the property <tt>agentType</tt>
	 * 
	 * @param agentType
	 *            The agentType to set.
	 * @uml.property name="agentType"
	 */
	public void setAgentType(final MAgentType agentType) {
		this.agentType = agentType;
	}

	@Override
	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * Setter of the property <tt>description</tt>
	 * 
	 * @param description
	 *            The description to set.
	 * @uml.property name="description"
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Setter of the property <tt>division</tt>
	 * 
	 * @param division
	 *            The division to set.
	 * @uml.property name="division"
	 */
	public void setDivision(final MDivision division) {
		this.division = division;
	}

	/**
	 * @param gisPosition
	 *            the gisPosition to set
	 */
	public void setGisPosition(final GISPosition gisPosition) {
		this.gisPosition = gisPosition;
	}

	/**
	 * {@link MSource#setKey(String)}
	 */
	@Deprecated
	@Transient
	public void setName(final String name) {
		setKey(name);
	}

	/**
	 * @param netAddress
	 *            the netAddress to set
	 */
	public void setNetAddress(final String netAddress) {
		this.netAddress = netAddress;
	}

	/**
	 * @param platform
	 *            the platform to set
	 */
	public void setPlatform(final String platform) {
		this.platform = platform;
	}

	/**
	 * Setter of the property <tt>profiles</tt>
	 * 
	 * @param profiles
	 *            The profiles to set.
	 * @uml.property name="profiles"
	 */
	public void setProfiles(final Set<MProfile> profiles) {
		this.profiles = profiles;
	}

	/**
	 * Setter of the property <tt>properties</tt>
	 * 
	 * @param properties
	 *            The properties to set.
	 * @uml.property name="properties"
	 */
	public void setProperties(final Set<MProperty> properties) {
		this.properties = properties;
	}

	/**
	 * Setter of the property <tt>profile</tt>
	 * 
	 * @param profile
	 *            The profile to set.
	 * @uml.property name="selectedProfile"
	 */
	public void setSelectedProfile(final MProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}

	/**
	 * Setter of the property <tt>timeZone</tt>
	 * 
	 * @param timeZone
	 *            The timeZone to set.
	 * @uml.property name="timeZone"
	 */
	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public boolean updateSimpleFields(final MSource source) {
		boolean isUpdated = super.updateSimpleFields(source);

		if (source instanceof MAgent) {
			final MAgent sourceAgent = (MAgent) source;

			if (!equals(getTimeZone(), sourceAgent.getTimeZone())) {
				setTimeZone(sourceAgent.getTimeZone());
				isUpdated = true;
			}

			if (!equals(getDescription(), sourceAgent.getDescription())) {
				setDescription(sourceAgent.getDescription());
				isUpdated = true;
			}

			if (!equals(getPlatform(), sourceAgent.getPlatform())) {
				setPlatform(sourceAgent.getPlatform());
				isUpdated = true;
			}

			final GISPosition agentGisPosition = sourceAgent.getGisPosition();
			if (getGisPosition() != null) {
				isUpdated |= getGisPosition().updateSimpleFields(
						agentGisPosition);
			} else {
				setGisPosition(agentGisPosition);
				isUpdated = true;
			}

			if (!equals(getAgentType(), sourceAgent.getAgentType())) {
				setAgentType(sourceAgent.getAgentType());
				isUpdated = true;
			}

			if (!equals(getNetAddress(), sourceAgent.getNetAddress())) {
				setNetAddress(sourceAgent.getNetAddress());
				isUpdated = true;
			}
		}
		return isUpdated;
	}

	public static MAgent copy(MAgent from) {
		MAgent a = new MAgent();
		if(from.getProfiles() != null) {
			a.setProfiles(new HashSet<MProfile>(from.getProfiles()));
		}
		if(from.getProperties() != null) {
			a.setProperties(new HashSet<MProperty>(from.getProperties()));
		}

		a.setDisplayName(from.getDisplayName());
		a.setAgentType(from.getAgentType());
		a.setDeleted(from.isDeleted());
		a.setDescription(from.getDescription());
		a.setDivision(from.getDivision());
		a.setGisPosition(from.getGisPosition());
		a.setNetAddress(from.getNetAddress());
		a.setPlatform(from.getPlatform());

		a.setSelectedProfile(from.getSelectedProfile());
		a.setTimeZone(from.getTimeZone());
		a.setCreatedBy(from.getCreatedBy());
		a.setCreationDateTime(from.getCreationDateTime());
		a.setId(from.getId());
		a.setKey(from.getKey());
		a.setModificationDateTime(from.getModificationDateTime());
		a.setParent(from.getParent());
		a.setSnmpId(from.getSnmpId());
		a.setVersion(from.getVersion());
		a.setName(from.getName());
		return a;
	}

}
