package com.tecomgroup.qos.rest.data;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.tecomgroup.qos.domain.MAlertReport;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ParameterStateAggregated extends ParameterState {



    @Override
    protected AlertReportAggregated createReport(MAlertReport report)
    {
        return new AlertReportAggregated(report);
    }

    @Override
    public AlertReport addAlertReport(MAlertReport report)
    {
        AlertReportAggregated addedReport=createReport(report);
        for (Iterator<AlertReport> alertIterator = alertsHistory.iterator(); alertIterator.hasNext();){
            AlertReport itReport=alertIterator.next();
            if(addedReport.touchesReportTimeLine(itReport))
            {
                addedReport.aggregateTimeLine(itReport);
                alertIterator.remove();
            }
        }
        alertsHistory.add(addedReport);
        return addedReport;
    }

}
