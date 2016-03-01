/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.util.PolicyUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ivlev.e
 * 
 */
public class PolicyActionListValueProvider
		implements
			ValueProvider<PolicyWrapper, String> {

	private static final String POLICY_ACTION_LIST_SEPARATOR = ", ";
    private final QoSMessages messages;

    public PolicyActionListValueProvider(QoSMessages messages) {
        this.messages=messages;
    }

    private Map<String, String> getActionTypeOrder(){
        final Map<String, String> actionTypeOrder = new LinkedHashMap<String, String>();
        actionTypeOrder.put(MPolicySendAlert.class.getName(), null);
        actionTypeOrder.put(MPolicySendEmail.class.getName(), null);
        actionTypeOrder.put(MPolicySendSms.class.getName(), null);

        return actionTypeOrder;
    }

	@Override
	public String getPath() {
		return "actions";
	}

	@Override
	public String getValue(final PolicyWrapper policyWrapper) {
		final MPolicy policy = policyWrapper.getPolicy();

		String result = "";
		if (policy.getActions() != null) {
            final Map<String, String> actionTypeOrder = getActionTypeOrder();

			for (final MPolicyAction mPolicyAction : policy.getActions()) {
                final String policyActionClassName = mPolicyAction.getClass().getName();
                if (actionTypeOrder.get(policyActionClassName) == null){
                    actionTypeOrder.put(policyActionClassName,
                            getDefaultActionName(mPolicyAction));
                }
			}

            boolean isFirst = true;
            for (final Map.Entry<String, String> actionType : actionTypeOrder.entrySet()){
                final String actionTypeName = actionType.getValue();

                if (actionTypeName != null){
                    if (!isFirst) {
                        result += POLICY_ACTION_LIST_SEPARATOR;
                    }
                    result += actionTypeName;
                    isFirst = false;
                }
            }
		}
		return result;
	}

    public String getDefaultActionName(final MPolicyAction policyAction) {
        String actionName = "";
        if (policyAction instanceof MPolicySendAlert) {
            actionName = this.messages.sendAlertLabel();
        } else if (policyAction instanceof MPolicySendEmail) {
            actionName = this.messages.sendEmailLabel();
        } else if (policyAction instanceof MPolicySendSms) {
            actionName = this.messages.sendSMSLabel();
        }

        return actionName;
    }

	@Override
	public void setValue(final PolicyWrapper policy, final String value) {

	}
}
