/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

/**
 * 
 * Показывает, что доменный объект имеет ссылку на критерион в виде
 * сереализованой строки
 * 
 * @author abondin
 * 
 */
public interface SerializedCriterionContainer {
	/**
	 * 
	 * @return
	 */
	Criterion getCriterion();

	/**
	 * @return
	 */
	String getSerializedCriterion();

	/**
	 * @param criterion
	 */
	void setCriterion(Criterion criterion);

	/**
	 * @param serializedCriterion
	 */
	void setSerializedCriterion(String serializedCriterion);
}
