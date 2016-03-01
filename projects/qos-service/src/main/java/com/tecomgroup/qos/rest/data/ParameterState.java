package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MAlertReport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ParameterState {
    protected ParameterGroup group;
    protected final List<AlertReport> alertsHistory=new ArrayList<AlertReport>();


    protected AlertReport createReport(MAlertReport report)
    {
        return new AlertReport(report);
    }

    public AlertReport addAlertReport(MAlertReport report)
    {
        AlertReport reportResult=createReport(report);
        alertsHistory.add(reportResult);
        return reportResult;
    }

    public ParameterGroup getGroup() {
        return group;
    }

    public void setGroup(ParameterGroup group) {
        this.group = group;
    }

    public List<AlertReport> getAlertsHistory() {
        return alertsHistory;
    }

    public void  setAlertsHistory(List<AlertReport> history) {
        this.alertsHistory.clear();
        this.alertsHistory.addAll(history);
    }
}
