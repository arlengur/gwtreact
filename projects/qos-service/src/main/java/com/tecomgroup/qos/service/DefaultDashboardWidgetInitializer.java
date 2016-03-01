/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.*;
import java.util.Map.Entry;

import com.tecomgroup.qos.dashboard.*;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;
import com.tecomgroup.qos.dashboard.EmergencyAgentsTopWidget.ChartData;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 */
@Component
@Profile(AbstractService.TEST_CONTEXT_PROFILE)
public class DefaultDashboardWidgetInitializer
        implements
        DashboardWidgetInitializer {

    @Autowired
    private AlertService alertService;

    @Autowired
    private ModelSpace modelSpace;

    @Autowired
    protected AuthorizeService authorizeService;

    @SuppressWarnings("unchecked")
    @Override
    public <M extends WidgetData> List<M> loadData(
            final HasUpdatableData<M> widget) {
        if (widget instanceof LatestAlertsWidget) {
            return (List<M>) getLatestAlertWidgetData((LatestAlertsWidget) widget);
        } else if (widget instanceof EmergencyAgentsTopWidget) {
            return (List<M>) getEmergencyAgentsWidgetData((EmergencyAgentsTopWidget) widget);
        } else if (widget instanceof DashboardChartWidget) {
            return (List<M>) getChartWidgetData((DashboardChartWidget) widget);
        }
        return null;
    }

    /**
     * @param rawData - sorted by value in DESC order
     * @param topSize
     * @return
     */
    private List<ChartData> convertRawDataToChartData(
            final Map<String, Long> rawData, final int topSize) {
        final Map<ChartData, Double> result = new LinkedHashMap<>();
        long totalDuration = 0;
        int agentCount = 0;
        long othersDuration = 0;
        for (final Long value : rawData.values()) {
            totalDuration += value;
            if (agentCount >= topSize) {
                othersDuration += value;
            }
            agentCount++;
        }
        agentCount = 0;
        // Sum of integral parts of real number
        int integralPartsSum = 0;
        for (final Entry<String, Long> entry : rawData.entrySet()) {
            if (agentCount < topSize) {
                final double value = 100.0 * entry.getValue() / totalDuration;
                final double fractionalPart = value - (int) value;
                result.put(new ChartData(entry.getKey(), (int) value),
                        fractionalPart);
                integralPartsSum += (int) value;
            } else {
                break;
            }
            agentCount++;
        }
        final double othersAgentsValue = 100.0 * othersDuration / totalDuration;
        integralPartsSum += (int) othersAgentsValue;
        final double fractionalPart = othersAgentsValue
                - (int) othersAgentsValue;
        result.put(new ChartData(EmergencyAgentsTopWidget.OTHERS_AGENTS,
                (int) othersAgentsValue), fractionalPart);
        // post processing to ensure sum of percent is equal to 100
        final List<Double> fractionalParts = new LinkedList<>(result.values());
        while (integralPartsSum < 100) {
            final Double maxFractionalPart = Collections.max(fractionalParts);
            fractionalParts.remove(maxFractionalPart);
            final Set<ChartData> foundKeys = SimpleUtils.getKeysByValue(result,
                    maxFractionalPart);
            final ChartData chartData = foundKeys.iterator().next();
            chartData.setSummaryDuration(chartData
                    .getSummaryDuration() + 1);
            integralPartsSum += 1;
        }
        return new ArrayList<>(result.keySet());
    }

    private List<ChartData> getEmergencyAgentsWidgetData(EmergencyAgentsTopWidget widget) {
        final Map<String, Long> durationByAgentKey = alertService
                .getAlertsSummaryDurationByAgentKey(
                        widget.getSeverities(),
                        widget.getIntervalType());
        return convertRawDataToChartData(
                durationByAgentKey,
                widget.getTopSize());
    }


    private List<MChartSeries> getChartWidgetData(DashboardChartWidget widget) {
        final List<DashboardChartWidget.ChartSeriesData> series = widget.getSeriesData();
        final Set<Long> ids = new TreeSet<>();
        for (final DashboardChartWidget.ChartSeriesData chartSeriesData : series) {
            ids.add(chartSeriesData.getId());
        }
        List<MChartSeries> chartSerieses = modelSpace.find(
                MChartSeries.class,
                CriterionQueryFactory.getQuery().in("id", ids));
        List<MChartSeries> results = new LinkedList<>();
        for(MChartSeries result: chartSerieses) {
            String agentKey = result.getTask().getModule().getAgent().getKey();
            if(authorizeService.isPermittedProbes(Arrays.asList(agentKey))) {
                results.add(result);
            }
        }
        return results;
    }

    private List<MAlert> getLatestAlertWidgetData(LatestAlertsWidget widget) {
        final CriterionQuery criterionQuery = CriterionQueryFactory
                .getQuery();
        Criterion criterion = null;
        if (!widget.getAgentKeys().isEmpty()) {
            // TODO: implicitly use systemComponentKey instead of
            // source.parent.parent.key
            criterion = criterionQuery.in("source.parent.parent.key",
                    widget.getAgentKeys());
        }
        if (!widget.getSeverities().isEmpty()) {
            criterion = SimpleUtils.mergeCriterions(
                    criterion,
                    criterionQuery.in("perceivedSeverity",
                            widget.getSeverities()));
        }
        criterion = SimpleUtils.mergeCriterions(criterion,
                criterionQuery.eq("status", Status.ACTIVE));
        return alertService.getAlerts(criterion,
                Order.desc("lastUpdateDateTime"), 0,
                widget.getVisibleAlertCount());
    }

    @Override
    public void setupWidget(final DashboardWidget widget) throws Exception {
        // TODO Implement me
    }

}
