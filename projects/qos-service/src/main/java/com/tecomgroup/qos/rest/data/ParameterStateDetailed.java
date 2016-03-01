package com.tecomgroup.qos.rest.data;

import java.util.ArrayList;
import java.util.List;

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
public class ParameterStateDetailed extends ParameterState{

    public ParameterStateDetailed() {
        super();
    }

    @Override
    protected AlertReport createReport(MAlertReport report)
    {
        return new AlertReportDetailed(report);
    }
}
