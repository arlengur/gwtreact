/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.util.List;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.domain.MProperty;

/**
 * The container of the collection of {@link MProperty}.
 * 
 * @author kunilov.p
 * 
 */
public interface PropertiesContainer {

	@JsonIgnore
	@Transient
	void addProperty(MProperty property);

	List<MProperty> getProperties();

	@JsonIgnore
	@Transient
	MProperty getProperty(String name);

	@JsonIgnore
	@Transient
	void removeProperty(MProperty property);
}
