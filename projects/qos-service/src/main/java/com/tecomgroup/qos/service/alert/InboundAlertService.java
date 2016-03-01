/*
 * Copyright (C) 2015 Qligent.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.service.alert;

import com.tecomgroup.qos.domain.AlertDTO;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.service.Service;

/**
 * Created by uvarov.m on 06.07.2015.
 */
public interface InboundAlertService extends Service {

    /**
     *
     * @param indication
     * @return
     */
   public AlertDTO activateAlert(MAlertIndication indication);

    /**
     *
     * @param indication

     * @return
     */
   public AlertDTO clearAlert(MAlertIndication indication);
}
