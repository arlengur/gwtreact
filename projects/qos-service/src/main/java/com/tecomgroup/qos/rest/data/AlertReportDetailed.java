package com.tecomgroup.qos.rest.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MRecordedStream;

/**
 *
 * @author stroganov.d
 */
@XmlType
public class AlertReportDetailed extends AlertReport {

    public AlertData alert;

    public AlertReportDetailed() {
        super();
    }

    public AlertReportDetailed(MAlertReport malertReport) {
        super(malertReport);
        this.alert=new AlertData(malertReport.getAlert());
    }
}
