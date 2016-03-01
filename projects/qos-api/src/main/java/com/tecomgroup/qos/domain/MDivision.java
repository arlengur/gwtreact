/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Структурное подразделение. (Волгателеком и т.д.)
 * 
 * @author kunilov.p
 */
@SuppressWarnings("serial")
@Entity
public class MDivision extends MAbstractEntity {

	/**
	 * @uml.property name="name"
	 */
	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * @uml.property name="parent"
	 */
	@OneToOne(cascade = {CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	private MDivision parent;

	/**
	 * @uml.property name="orderNumber"
	 */
	@Column(nullable = true)
	private Integer orderNumber;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public MDivision() {
		super();
	}

	public MDivision(final String name) {
		this();
		setName(name);
	}

	public MDivision(final String name, final Integer orderNumber) {
		this(name);
		setOrderNumber(orderNumber);
	}

	public MDivision(final String name, final Integer orderNumber,
			final MDivision parent) {
		this(name, orderNumber);
		setParent(parent);
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter of the property <tt>orderNumber</tt>
	 * 
	 * @return Returns the orderNumber.
	 * @uml.property name="orderNumber"
	 */
	public Integer getOrderNumber() {
		return orderNumber;
	}

	/**
	 * Getter of the property <tt>parent</tt>
	 * 
	 * @return Returns the parent.
	 * @uml.property name="parent"
	 */
	public MDivision getParent() {
		return parent;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="name"
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Setter of the property <tt>orderNumber</tt>
	 * 
	 * @param orderNumber
	 *            The orderNumber to set.
	 * @uml.property name="orderNumber"
	 */
	public void setOrderNumber(final Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Setter of the property <tt>parent</tt>
	 * 
	 * @param parent
	 *            The parent to set.
	 * @uml.property name="parent"
	 */
	public void setParent(final MDivision parent) {
		this.parent = parent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
