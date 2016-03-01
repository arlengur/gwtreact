package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.communication.request.RegisterAgent;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.event.AgentChangeStateEvent;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import com.tecomgroup.qos.service.RegisterAgentProcessor;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;


public class RegisterAgentMessageListener extends QoSMessageListener<RegisterAgent> {

    protected RegisterAgentProcessor registerAgentProcessor;
    @Autowired
    private InternalEventBroadcaster internalEventBroadcaster;

    private final static Logger LOGGER = Logger
            .getLogger(RegisterAgentMessageListener.class);
    @Override
    public RequestResponse handleQosMessage(RegisterAgent message) {
       return registerAgentProcessor.registerAgent(message);
    }

    @Override
    public void onMessage(Message message) {
        if (isEnabled()) {
            final byte[] correlationId = message.getMessageProperties()
                    .getCorrelationId();
            final String replyTo = message.getMessageProperties().getReplyTo();

            try {
                final RegisterAgent object = (RegisterAgent) messageConverter.fromMessage(message);
                object.setCorrelationId(correlationId);
                object.setReplyTo(replyTo);
                object.setMessageId(message.getMessageProperties()
                        .getMessageId());
                final RequestResponse response = handleQosMessage(object);
                if (response != null) {
                    response(object, response);
                }
            } catch (final Exception ex) {
                error(replyTo, correlationId, ex);
                //close registration event
                String agentKey= (String) message.getMessageProperties().getHeaders().get(RegisterAgent.AGENT_KEY_HEADER);
                String agentName= (String) message.getMessageProperties().getHeaders().get(RegisterAgent.AGENT_DISPLAY_NAME_HEADER);
                if(agentKey!=null && !agentKey.isEmpty()) {
                    MAgent fakeAgent = new MAgent();
                    fakeAgent.setKey(agentKey);
                    agentName=(agentName!=null && !agentName.isEmpty())?agentName:agentKey;
                    fakeAgent.setDisplayName(agentName);
                    internalEventBroadcaster.broadcast(Arrays
                            .asList(new AgentChangeStateEvent(MAgent.AgentRegistrationState.FAILED, fakeAgent, new Date())));
                }
            }

        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(this.getClass().getSimpleName()
                        + " is not enabled");
            }
        }
    }

    public RegisterAgentProcessor getRegisterAgentProcessor() {
        return registerAgentProcessor;
    }

    public void setRegisterAgentProcessor(RegisterAgentProcessor registerAgentProcessor) {
        this.registerAgentProcessor = registerAgentProcessor;
    }
}
