package com.tecomgroup.qos.rest.data;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAlertReport;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class AlertReportAggregated extends AlertReport  {


    public AlertReportAggregated() {
        super();
    }

    public AlertReportAggregated(MAlertReport malertReport) {
       super(malertReport);
    }

    public boolean touchesReportTimeLine(AlertReport report)
    {
        if(this.severity.equals(report.severity)) {
            if ((startDateTime!=null && report.startDateTime.equals(this.startDateTime)) || (this.endDateTime!=null && this.endDateTime.equals(report.endDateTime))) {
                return true;
            }
            TimeInterval currentInterval = TimeInterval.get(this.startDateTime, this.endDateTime);
            if ((report.startDateTime!=null &&  currentInterval.isDateIncluded(report.startDateTime)) || (report.endDateTime!=null && currentInterval.isDateIncluded(report.endDateTime))) {
                return true;
            }
        }
        return false;
    }

    public void aggregateTimeLine(AlertReport report)
    {
        if(this.startDateTime.after(report.startDateTime))
        {
            this.startDateTime=report.startDateTime;
        }
        else if(this.endDateTime==null || report.endDateTime==null)
        {
            this.endDateTime=null;
        }
        else if(this.endDateTime.before(report.endDateTime))
        {
            this.endDateTime=report.endDateTime;
        }
        alertReportId=0;
    }
}
