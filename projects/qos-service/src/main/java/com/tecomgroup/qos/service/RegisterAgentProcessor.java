package com.tecomgroup.qos.service;

import com.tecomgroup.qos.communication.request.RegisterAgent;
import com.tecomgroup.qos.communication.response.RequestResponse;

/**
 * Created by stroganov.d on 26.03.2015.
 */
public interface RegisterAgentProcessor {
    public RequestResponse registerAgent(final RegisterAgent registrationInfo) ;
}
