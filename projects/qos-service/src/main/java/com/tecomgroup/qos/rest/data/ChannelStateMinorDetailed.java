package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MAlertReport;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author stroganov.d
 */
@XmlType(propOrder = {"channelId","logo","channelName"})
@JsonPropertyOrder({"channelId","logo","channelName"})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ChannelStateMinorDetailed  extends ChannelStateCommon {
    final  protected Map<ParameterGroup,ParameterState> parameterStates=new HashMap<>();

    public ChannelStateMinorDetailed() {
        super();
    }
    public ChannelStateMinorDetailed(SetConfiguration setConfiguration) {
       super(setConfiguration);
    }

    public ParameterState createParameterState(ParameterGroup alertGroup)
    {
        /**@TODO set agregation condition to the REST parameter*/
        ParameterState parameterState=new ParameterStateAggregated();
        parameterState.setGroup(alertGroup);
        return parameterState;
    }

    @Override
    public AlertReport updateState(ParameterGroup parameterGroup, MAlertReport report) {
        ParameterState parameterState=parameterStates.get(parameterGroup);
        if(parameterState==null)
        {
            parameterState=createParameterState(parameterGroup);
            parameterStates.put(parameterGroup,parameterState);
        }
        return parameterState.addAlertReport(report);
    }

    public Collection<ParameterState> getParameterStates() {
        return parameterStates.values();
    }

    public void setParameterStates(Collection<ParameterState> states) {
         parameterStates.clear();
         for (ParameterState state : states)
         {
             parameterStates.put(state.getGroup(),state);
         }
    }
}
