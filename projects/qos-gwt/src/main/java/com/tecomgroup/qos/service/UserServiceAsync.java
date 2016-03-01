/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;

import org.dom4j.tree.AbstractEntity;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;

import static com.tecomgroup.qos.dashboard.DashboardChartWidget.ChartSeriesData;

/**
 * @author abondin
 *
 */
public interface UserServiceAsync {
	void getAllContactInformations(
			AsyncCallback<List<MContactInformation>> callback);

	void getCurrentUser(AsyncCallback<MUser> callback);

	void getDashboard(String username, AsyncCallback<MDashboard> callback);

	void getLdapUsers(AsyncCallback<List<MUser>> callback);

	void getTemplate(TemplateType type, Long userId, String templateName,
			AsyncCallback<MUserAbstractTemplate> callback);

	void getTemplates(TemplateType type, Long userId,
			AsyncCallback<List<MUserAbstractTemplate>> callback);

	<M extends WidgetData> void loadWigetData(HasUpdatableData<M> widget,
			AsyncCallback<List<M>> callback);

	void removeTemplate(TemplateType type, Long userId, String templateName,
			AsyncCallback<Void> callback);

	void saveTemplate(MUserAbstractTemplate template,
			AsyncCallback<MUserAbstractTemplate> callback);

	void updateCurrentUser(MUser user, AsyncCallback<Void> callback);

	void updateDashboard(MDashboard dashboard, AsyncCallback<Void> callback);

	void updatePassword(String oldPassword, String newPassword,
			AsyncCallback<Void> callback);

    void addWidgetToDashboard(DashboardWidget widget, AsyncCallback<Void> async);

    void removeWidgetFromDashboard(String widgetKey, AsyncCallback<Void> async);
}
