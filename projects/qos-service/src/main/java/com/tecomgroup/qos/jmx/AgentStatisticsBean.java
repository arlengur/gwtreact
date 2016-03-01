/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.jmx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.naming.SelfNaming;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.util.Utils;

/**
 * 
 * @author abondin
 * 
 */
public class AgentStatisticsBean implements SelfNaming, DynamicMBean {

	private final static String AGENT_NAME_PROPERTY = "agentName";
	private final static String REGISTRATION_TIME_PROPERTY = "registrationTime";
	private final static String RESULT_HANDLED = "resultHandled";
	private final static String AGENT_DISPLAY_NAME = "displayName";
	private final static String LAST_RESULT_TIME = "lastResultTime";
	private final static String HOST_NAME = "hostName";

	private final AgentStatistic agentStatistic;

	private ObjectName objectName;

	private MBeanInfo beanInfo;
	/**
	 * @throws NullPointerException
	 * @throws MalformedObjectNameException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IntrospectionException
	 * 
	 */
	public AgentStatisticsBean(final AgentStatistic agentStatistic)
			throws MalformedObjectNameException, NullPointerException,
			IntrospectionException, SecurityException, NoSuchMethodException {
		this.agentStatistic = agentStatistic;
		initialize();

	}

	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return ((MAgent) agentStatistic.getComponent()).getName();
	}

	@Override
	public Object getAttribute(final String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		return getValue(attribute);
	}

	@Override
	public AttributeList getAttributes(final String[] attributes) {
		final AttributeList list = new AttributeList();
		for (final String attribute : attributes) {
			list.add(new Attribute(attribute, getValue(attribute)));
		}
		return null;
	}

	@ManagedAttribute
	public String getDisplayName() {
		return agentStatistic.getComponent().getDisplayName();
	}

	/**
	 * @return the hanldedResults
	 */
	public Long getHanldedResults() {
		return agentStatistic.getHanldedResults();
	}

	@ManagedAttribute
	public String getHostName() {
		return ((MAgent) agentStatistic.getComponent()).getNetAddress();
	}

	/**
	 * @return the lastResultTime
	 */
	@ManagedAttribute
	public Date getLastResultTime() {
		return agentStatistic.getLastResultTime();
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return beanInfo;
	}

	@Override
	public ObjectName getObjectName() throws MalformedObjectNameException {
		return objectName;
	}

	/**
	 * @return the registrationTime
	 */
	@ManagedAttribute
	public Date getRegistrationTime() {
		return agentStatistic.getRegistrationTime();
	}

	private Object getValue(final String attribute) {
		if (AGENT_NAME_PROPERTY.equals(attribute)) {
			return getAgentName();
		} else if (REGISTRATION_TIME_PROPERTY.equals(attribute)) {
			return getRegistrationTime();
		} else if (RESULT_HANDLED.equals(attribute)) {
			return getHanldedResults();
		} else if (AGENT_DISPLAY_NAME.equals(attribute)) {
			return getDisplayName();
		} else if (LAST_RESULT_TIME.equals(attribute)) {
			return getLastResultTime();
		} else if (HOST_NAME.equals(attribute)) {
			return getHostName();
		} else {
			return null;
		}
	}

	private void initialize() throws MalformedObjectNameException,
			NullPointerException, IntrospectionException, SecurityException,
			NoSuchMethodException {
		final List<MBeanAttributeInfo> attributeInfos = new ArrayList<MBeanAttributeInfo>();
		attributeInfos.add(new MBeanAttributeInfo(AGENT_NAME_PROPERTY,
				"Agent Name", AgentStatisticsBean.class
						.getMethod("getAgentName"), null));
		attributeInfos.add(new MBeanAttributeInfo(REGISTRATION_TIME_PROPERTY,
				"Registration Time", AgentStatisticsBean.class
						.getMethod("getRegistrationTime"), null));
		attributeInfos.add(new MBeanAttributeInfo(RESULT_HANDLED,
				"Number of handled results", AgentStatisticsBean.class
						.getMethod("getHanldedResults"), null));
		attributeInfos.add(new MBeanAttributeInfo(AGENT_DISPLAY_NAME,
				"Display Name", AgentStatisticsBean.class
						.getMethod("getDisplayName"), null));
		attributeInfos.add(new MBeanAttributeInfo(LAST_RESULT_TIME,
				"Last result time", AgentStatisticsBean.class
						.getMethod("getLastResultTime"), null));
		attributeInfos.add(new MBeanAttributeInfo(HOST_NAME, "Hostname",
				AgentStatisticsBean.class.getMethod("getHostName"), null));
		this.objectName = new ObjectName(Utils.getJMXObjectName("Agents",
				getAgentName()));
		this.beanInfo = new MBeanInfo(JMXStatisticService.class.getName(),
				"Agent Statistic",
				attributeInfos.toArray(new MBeanAttributeInfo[0]),
				new MBeanConstructorInfo[0], new MBeanOperationInfo[0],
				new MBeanNotificationInfo[0]);
	}

	@Override
	public Object invoke(final String actionName, final Object[] params,
			final String[] signature) throws MBeanException,
			ReflectionException {
		// Do nothing
		return null;
	}

	@Override
	public void setAttribute(final Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		// TODO Auto-generated method stub
	}
	@Override
	public AttributeList setAttributes(final AttributeList attributes) {
		// TODO Auto-generated method stub
		return null;
	}
}