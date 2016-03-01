package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by uvarov.m on 06.07.2015.
 */
public interface AlertServiceDao {

    public AlertDTO getAlert(MAlertIndication indication);

    public BigDecimal getAlertTypeId(String alertTypeName);

    public BigDecimal getSourceTaskId(String sourceKey);

    public BigDecimal getPolicyId(String originatorKey);

    public void registerAlertTypes(final List<MAlertType> types);

    public void insertAlert(final AlertDTO alert);

    public void updateAlert(final AlertDTO alert);

    public void insertUpdates(final List<AlertUpdateDTO> updates);

    public Long openAlertReport(BigDecimal alertId,
                                  Date endDateTime,
                                  MAlertType.PerceivedSeverity severity,
                                  Date startDateTime);

    public Long closeAlertReport(BigDecimal alertId,
                                Date endDateTime);

    public String getAgentKey(String sourceKey);

    public List<AlertDTO> getActiveAlerts();

}
