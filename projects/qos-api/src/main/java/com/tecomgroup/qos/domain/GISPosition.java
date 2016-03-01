/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.tecomgroup.qos.UpdatableEntity;

/**
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Embeddable
public class GISPosition implements Serializable, UpdatableEntity<GISPosition> {

	/**
	 * @uml.property name="latitude"
	 */
	private Double latitude;

	/**
	 * @uml.property name="longitude"
	 */
	private Double longitude;

	/**
	 * @uml.property name="title"
	 */
	private String title;

	public GISPosition() {
		super();
	}

	public GISPosition(final double longitude, final double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Transient
	public boolean coordinateEquals(final GISPosition position) {
		return latitude.equals(position.getLatitude())
				&& longitude.equals(position.getLongitude());
	}

	/**
	 * Getter of the property <tt>latitude</tt>
	 * 
	 * @return Returns the latitude.
	 * @uml.property name="latitude"
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Getter of the property <tt>longitude</tt>
	 * 
	 * @return Returns the longitude.
	 * @uml.property name="longitude"
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Getter of the property <tt>title</tt>
	 * 
	 * @return Returns the title.
	 * @uml.property name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter of the property <tt>latitude</tt>
	 * 
	 * @param latitude
	 *            The latitude to set.
	 * @uml.property name="latitude"
	 */
	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Setter of the property <tt>longitude</tt>
	 * 
	 * @param longitude
	 *            The longitude to set.
	 * @uml.property name="longitude"
	 */
	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Setter of the property <tt>title</tt>
	 * 
	 * @param title
	 *            The title to set.
	 * @uml.property name="title"
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public boolean updateSimpleFields(final GISPosition sourcePosition) {
		boolean isUpdated = false;

		if (sourcePosition != null) {
			if (!MAbstractEntity.equals(getLatitude(),
					sourcePosition.getLatitude())) {
				setLatitude(sourcePosition.getLatitude());
				isUpdated = true;
			}

			if (!MAbstractEntity.equals(getLongitude(),
					sourcePosition.getLongitude())) {
				setLongitude(sourcePosition.getLongitude());
				isUpdated = true;
			}

			if (!MAbstractEntity.equals(getTitle(), sourcePosition.getTitle())) {
				setTitle(sourcePosition.getTitle());
				isUpdated = true;
			}
		}
		return isUpdated;
	}
}
