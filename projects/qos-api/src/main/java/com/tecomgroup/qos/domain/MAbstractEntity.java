/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Abstract model class
 * 
 * @author abondin
 * @uml.stereotype uml_id="Standard::Type"
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class MAbstractEntity implements Serializable {
	/**
	 * @see java.util.Objects#equals(Object, Object)
	 */
	public static boolean equals(final Object a, final Object b) {
		return (a == b) || (a != null && a.equals(b));
	}


	public abstract Long getId();

	public abstract void setId(Long id);
}
