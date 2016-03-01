package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.event.AgentChangeStateEvent;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;

import com.tecomgroup.qos.communication.request.RegisterAgent;
import com.tecomgroup.qos.communication.response.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class RegisterAgentMessageRouter extends QoSMessageListener<RegisterAgent> {
    private String serviceExchangeName;
    private String routingKey;
    @Autowired
    private InternalEventBroadcaster internalEventBroadcaster;

    private final static Logger LOGGER = Logger
            .getLogger(RegisterAgentMessageRouter.class);
    @Override
    public RequestResponse handleQosMessage(RegisterAgent message) {
       return null;
    }

    @Override
    public void onMessage(Message message) {
        if (isEnabled()) {
            final byte[] correlationId = message.getMessageProperties()
                    .getCorrelationId();
            final String replyTo = message.getMessageProperties().getReplyTo();
            try {
                message.getMessageProperties().setReceivedRoutingKey(routingKey);
                final RequestResponse responseFake =new RequestResponse();
                amqpTemplate.send(serviceExchangeName,routingKey,message);
                response(correlationId,replyTo,responseFake);
                //send ACCEPTED agent registration event
                String agentKey= (String) message.getMessageProperties().getHeaders().get(RegisterAgent.AGENT_KEY_HEADER);
                String agentName= (String) message.getMessageProperties().getHeaders().get(RegisterAgent.AGENT_DISPLAY_NAME_HEADER);
                if(agentKey!=null && !agentKey.isEmpty()) {
                    MAgent fakeAgent = new MAgent();
                    fakeAgent.setKey(agentKey);
                    agentName=(agentName!=null && !agentName.isEmpty())?agentName:agentKey;
                    fakeAgent.setDisplayName(agentName);
                    internalEventBroadcaster.broadcast(Arrays
                            .asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.ACCEPTED, fakeAgent, new Date())));
                }
            } catch (final Exception ex) {
                error(replyTo, correlationId, ex);
            }
        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(this.getClass().getSimpleName()
                        + " is not enabled");
            }
        }
    }

    public void setServiceExchangeName(String serviceExchangeName) {
        this.serviceExchangeName = serviceExchangeName;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
