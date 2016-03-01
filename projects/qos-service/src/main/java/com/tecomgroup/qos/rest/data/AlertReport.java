package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class AlertReport {
    public long alertReportId;
    public Date startDateTime;
    public Date endDateTime;
    public MAlertType.PerceivedSeverity severity;

    public AlertReport() {
    }

    public AlertReport(MAlertReport malertReport) {
        this.alertReportId=malertReport.getId();
        this.startDateTime=malertReport.getStartDateTime();
        this.endDateTime=malertReport.getEndDateTime();
        this.severity=malertReport.getPerceivedSeverity();
    }
}
