/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.*;

import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.rest.data.Comment;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.SimpleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.AlertUpdateEvent;

/**
 * @author kunilov.p
 */
@Service("alertHistoryService")
@Transactional(readOnly = true)
public class DefaultAlertHistoryService extends AbstractService
        implements
        AlertHistoryService {

    @Autowired
    private AlertReportService alertReportService;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    @Transactional(readOnly = false)
    public void addAlertUpdate(final MAlert alert, final UpdateType updateType,
                               final Date dateTime, final String user, final String field,
                               final Object oldValue, final Object newValue, final String comment) {
        final MAlertUpdate alertUpdate = new MAlertUpdate(alert, updateType,
                dateTime, user, field, oldValue, newValue, comment);
        modelSpace.save(alertUpdate);
        if (updateType == UpdateType.NEW || updateType.isSeverityChanged()
                || updateType.isCleared()) {
            alertReportService.processAlertUpdateEvent(alertUpdate);
        }
        eventBroadcastDispatcher.broadcast(Arrays.asList(new AlertUpdateEvent(
                EventType.CREATE, alertUpdate.getId(), updateType)));
    }

    @Override
    public List<MAlertUpdate> getAlertHistory(final MAlert alert,
                                              final Criterion criterion, final Order order,
                                              final Integer startPosition, final Integer size) {

        final CriterionQuery query = modelSpace.createCriterionQuery();
        Criterion c = criterion;
        if (alert != null) {
            c = query.and(c, query.eq("alert.id", alert.getId()));
        }

        final Criterion probeCriterion = buildProbeFilterCriterion();
        if(probeCriterion == null) {
            return new ArrayList<MAlertUpdate>();
        }

        return modelSpace.find(MAlertUpdate.class, SimpleUtils.mergeCriterions(c, probeCriterion), order, startPosition,
                size);
    }

    @Override
    public Long getAlertHistoryTotalCount(final Criterion criterion) {
        final Criterion probeCriterion = buildProbeFilterCriterion();
        if(probeCriterion == null) {
            return 0L;
        }

        return modelSpace.count(MAlertUpdate.class, SimpleUtils.mergeCriterions(criterion, probeCriterion));
    }

    private Criterion buildProbeFilterCriterion() {
        List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
        if(!agentKeys.isEmpty()) {
            return modelSpace.createCriterionQuery().in("alert.source.parent.parent.key", agentKeys);
        }
        return null;
    }

    @Override
    public List<MAlertUpdate> getAllAlertHistory(final Criterion criterion,
                                                 final Order order, final Integer startPosition, final Integer size) {
        List<MAlertUpdate> updates = getAlertHistory(null, criterion, order, startPosition, size);
        return updates;
    }


    @Override
    public Map<Long, List<MAlertUpdate>> getAlertReportComments(final List<MAlertReport> reports, Date startDate, Date endDate) {
        final List<MAlertUpdate> comments = new ArrayList<>();
        final Map<Long, List<MAlertUpdate>> reportComments = new HashMap<>();
        final CriterionQuery query = CriterionQueryFactory.getQuery();

        Criterion criterion = query.between("dateTime", startDate, endDate);
        criterion = query.and(criterion, query.isNotNull("comment"));
        criterion = query.and(criterion, query.not(query.eq("comment", "")));

        comments.addAll(modelSpace.find(MAlertUpdate.class, criterion));


        for (final MAlertReport report : reports) {
            Iterator<MAlertUpdate> it = comments.iterator();
            while (it.hasNext()) {
                final MAlertUpdate comment = it.next();

                if (comment.getAlert().getId().equals(report.getAlert().getId())) {
                    boolean inInterval;
                    Date date = comment.getDateTime();

                    if (report.getEndDateTime() == null) {
                        inInterval = date.compareTo(report.getStartDateTime()) >= 0;
                    } else {
                        inInterval = date.compareTo(report.getStartDateTime()) >= 0 &&
                                date.compareTo(report.getEndDateTime()) <= 0;
                    }
                    if (inInterval) {
                        List<MAlertUpdate> repComments = reportComments.get(report.getId());
                        if (repComments == null) {
                            repComments = new ArrayList<>();
                            reportComments.put(report.getId(), repComments);
                        }
                        repComments.add(comment);
                        it.remove();
                    }
                }
            }
        }

        return reportComments;
    }

    @Override
    public List<Comment> getAlertReportComments(Long alertId) {
        StringBuilder query = new StringBuilder()
                .append("SELECT id, comment, datetime, updatetype, user_name FROM malertupdate WHERE alert_id = ")
                .append(alertId)
                .append(" AND comment IS NOT NULL")
                .append(" AND COALESCE(comment, '') != ''");
        Iterator result = modelSpace.createQuery(query.toString()).iterator();
        List<Comment> resultList = new ArrayList<>();
        while (result.hasNext()) {
            Comment comment = new Comment((Object[]) result.next());
            resultList.add(comment);
        }
        return resultList;
    }
}
