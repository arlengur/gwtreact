/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tecomgroup.qos.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 *
 * @author stroganov.d
 */
@Embeddable
public class MSetConfigurationMAgentTaskPK implements Serializable {

    private MSetConfiguration msetconfiguration;
    private MAgentTask magenttask;

    @ManyToOne
    public MSetConfiguration getMsetconfiguration() {
        return msetconfiguration;
    }

    public void setMsetconfiguration(MSetConfiguration msetconfiguration) {
        this.msetconfiguration = msetconfiguration;
    }

    @ManyToOne
    public MAgentTask getMagenttask() {
        return magenttask;
    }

    public void setMagenttask(MAgentTask magenttask) {
        this.magenttask = magenttask;
    }

    @Override
    public int hashCode() {
        int result;
        result = (msetconfiguration != null && msetconfiguration.getId()!=null? msetconfiguration.getId().hashCode() : 0);
        result = 31 * result + (magenttask != null &&  magenttask.getId()!=null ? magenttask.getId().hashCode() : 0);
        return result;
    }

    /**
     * Сравнение идёт только по ID ключевых параметров
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MSetConfigurationMAgentTaskPK that = (MSetConfigurationMAgentTaskPK) o;
        Long thisConfigId=msetconfiguration!=null?msetconfiguration.getId():null;
        Long thatConfigID=that.msetconfiguration!=null?that.msetconfiguration.getId():null;
        Long thisTaskId=magenttask!=null?magenttask.getId():null;
        Long thatTaskID=that.magenttask!=null?that.magenttask.getId():null;

        if (thisConfigId != null ? !thisConfigId.equals(thatConfigID) : thatConfigID != null) return false;
        if (thisTaskId != null ? !thisTaskId.equals(thatTaskID) :thatTaskID != null)
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "entities.test.MsetconfigurationMagenttaskPK[ msetconfiguration=" + msetconfiguration + ", magenttask=" + magenttask + " ]";
    }
    
}
