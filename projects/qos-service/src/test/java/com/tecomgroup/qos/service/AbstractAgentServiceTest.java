/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractAgentServiceTest {

	@Autowired
	protected ModelSpace modelSpace;

	@Autowired
	protected TransactionTemplate transactionTemplate;

	protected MAgent agent;

	protected final String agentKey = "FirstChannel-NN";

	protected List<MAgentTask> tasks;

	protected void createAgent() {
		agent = SharedModelConfiguration.createLightWeightAgent(agentKey);
		modelSpace.save(agent);
	}

	protected void createTasks(final MAgentModule module) {
		tasks = SharedModelConfiguration.createAgentTasks(agent, module);
		for (final MAgentTask task : tasks) {
			modelSpace.save(task);
		}
	}

	protected abstract void init();

	@Before
	public void setUp() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				init();
			}
		});
	}
}
