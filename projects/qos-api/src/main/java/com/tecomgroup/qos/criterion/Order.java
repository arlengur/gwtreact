/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

import java.io.Serializable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tecomgroup.qos.OrderType;

/**
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class Order implements Serializable {

	/**
	 * 
	 * @param propertyName
	 * @return ASSENDING order
	 */
	public static Order asc(final String propertyName) {
		return new Order(OrderType.ASC, propertyName);
	}
	/**
	 * 
	 * @param propertyName
	 * @return DESSENDING order
	 */
	public static Order desc(final String propertyName) {
		return new Order(OrderType.DESC, propertyName);
	}

	public static Order get(final String type, final String propertyName) {
		return new Order(OrderType.valueOf(type.toUpperCase()), propertyName);
	}

	private String propertyName;

	@Enumerated(EnumType.STRING)
	private OrderType type;

	public Order() {
		super();
	}

	public Order(final Order order) {
		this();
		setPropertyName(order.getPropertyName());
		setType(order.getType());
	}

	/**
	 * @param type
	 * @param propertyName
	 */
	private Order(final OrderType type, final String propertyName) {
		this.type = type;
		this.propertyName = propertyName;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the type
	 */
	public OrderType getType() {
		return type;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final OrderType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return propertyName + " " + type;
	}
}
