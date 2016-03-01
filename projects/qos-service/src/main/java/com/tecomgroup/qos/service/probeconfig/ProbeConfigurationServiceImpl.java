package com.tecomgroup.qos.service.probeconfig;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.communication.message.*;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.DefaultSystemComponentStatisticService;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

/**
 * Created by stroganov.d on 25.05.2015.
 */
public class ProbeConfigurationServiceImpl extends AbstractService implements ProbeConfigurationService {
    private final static Logger LOGGER = Logger
            .getLogger(ProbeConfigurationServiceImpl.class);

    private String serviceExchangeName;
    private String resultQueuePrefix;

    @Autowired
    DefaultSystemComponentStatisticService statisticService;

    @Autowired
    ProbeConfigStorageService configStorage;

    protected boolean enabled = true;

    protected AmqpTemplate amqpTemplate;

    protected MessageConverter messageConverter;

    @Override
    public void restartProbeHardware(String key) throws Exception {
        AgentStatistic statistic = statisticService.getAgentsStatisticByComponentKey(key);
        if(statistic == null) {
            throw new Exception("Unable to find component: " + key);
        }
        MAgent agent = statistic.getComponent();
        amqpTemplate.convertAndSend(serviceExchangeName, resultQueuePrefix + agent.getKey(), new ProbeReboot(getUserName()));
        statisticService.resetAgentStatistics(agent.getKey());
    }

    @Override
    public void restartProbeSoftware(String key) throws Exception {
        AgentStatistic statistic = statisticService.getAgentsStatisticByComponentKey(key);
        if(statistic == null) {
            throw new Exception("Unable to find component: " + key);
        }
        MAgent agent = statistic.getComponent();
        amqpTemplate.convertAndSend(serviceExchangeName, resultQueuePrefix + agent.getKey(), new Restart(getUserName()));
        statisticService.resetAgentStatistics(agent.getKey());
    }

    @Override
    public void rollback(String key) throws Exception {
        AgentStatistic statistic = statisticService.getAgentsStatisticByComponentKey(key);
        if(statistic == null) {
            throw new Exception("Unable to find component: " + key);
        }
        MAgent agent = statistic.getComponent();
        amqpTemplate.convertAndSend(serviceExchangeName, resultQueuePrefix + agent.getKey(), new Rollback(getUserName()));
        statisticService.resetAgentStatistics(agent.getKey());
    }

    @Override
    public boolean updateConfiguration(String key, String configuration) throws Exception {
        AgentStatistic statistic = statisticService.getAgentsStatisticByComponentKey(key);
        if(statistic == null) {
            throw new Exception("Unable to find component: " + key);
        }
        MAgent agent = statistic.getComponent();
        if(!configStorage.validateProbeConfig(configuration, agent.getKey())) {
            return false;
        }
        amqpTemplate.convertAndSend(serviceExchangeName, resultQueuePrefix + agent.getKey(), new ConfigUpdate(getUserName(), configuration));
        statisticService.resetAgentStatistics(agent.getKey());
        return true;
    }

    @Override
    public void swUpdate(List<String> keys, String filename) throws Exception {
        String ftpUrl = configStorage.getSwFtpUrl(filename);
        for(String key : keys) {
            AgentStatistic statistic = statisticService.getAgentsStatisticByComponentKey(key);
            if(statistic == null) {
                throw new Exception("Unable to find component: " + key);
            }
            MAgent agent = statistic.getComponent();
            amqpTemplate.convertAndSend(serviceExchangeName, resultQueuePrefix + agent.getKey(), new SwUpgrade(getUserName(), ftpUrl));
            statisticService.resetAgentStatistics(agent.getKey());
        }
    }

    @Override
    public List<String> probeSwList () throws IOException {
        return configStorage.getSwList();
    }

    @Override
    public String probeConfig (String key) throws IOException {
        return configStorage.downloadProbeConfiguration(key);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public String getServiceExchangeName() {
        return serviceExchangeName;
    }

    public void setServiceExchangeName(String serviceExchangeName) {
        this.serviceExchangeName = serviceExchangeName;
    }

    public String getResultQueuePrefix() {
        return resultQueuePrefix;
    }

    public void setResultQueuePrefix(String resultQueuePrefix) {
        this.resultQueuePrefix = resultQueuePrefix;
    }

    private String getUserName()
    {
        final Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if(authentication!=null && authentication instanceof AnonymousAuthenticationToken)
        {
            Object principal = authentication.getPrincipal();
            if(principal instanceof UserDetails){
                UserDetails user = (UserDetails) principal;
                return user.getUsername();
            }
        }
        return null;
    }


}
