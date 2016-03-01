/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tecomgroup.qos.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author stroganov.d
 */
@Entity
@Table(name = "msetconfiguration_magenttask")
@AssociationOverrides({
		@AssociationOverride(name = "pk.msetconfiguration", joinColumns = @JoinColumn(name = "pk_msetconfiguration_id")),
		//@AssociationOverride(name = "pk.magenttask", joinColumns = @JoinColumn(name = "CATEGORY_ID"))
		})
@XmlRootElement
public class MSetConfigurationMAgentTask implements Serializable {
	private static final long serialVersionUID = 1L;

	protected MSetConfigurationMAgentTaskPK pk=new MSetConfigurationMAgentTaskPK();
	private boolean defaultstreamsource;

	public MSetConfigurationMAgentTask() {
	}

	@EmbeddedId
	public MSetConfigurationMAgentTaskPK getPk() {
		return pk;
	}

	public void setPk(MSetConfigurationMAgentTaskPK pk) {
		this.pk = pk;
	}

	@Transient
	public MSetConfiguration getmSetConfiguration() {
		return pk.getMsetconfiguration();
	}

	public void setmSetConfiguration(MSetConfiguration mSetConfiguration) {
		this.pk.setMsetconfiguration(mSetConfiguration);
	}

	@Transient
	public MAgentTask getmAgentTask() {
		return pk.getMagenttask();
	}

	public void setmAgentTask(MAgentTask mAgentTask) {
		this.pk.setMagenttask(mAgentTask);
	}

	public boolean isDefaultstreamsource() {
		return defaultstreamsource;
	}

	public void setDefaultstreamsource(boolean defaultstreamsource) {
		this.defaultstreamsource = defaultstreamsource;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (pk != null
				? pk.hashCode()
				: 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof MSetConfigurationMAgentTask)) {
			return false;
		}
		MSetConfigurationMAgentTask other = (MSetConfigurationMAgentTask) object;
		if ((this.pk == null && other.pk != null)
				|| (this.pk != null && !this.pk
						.equals(other.pk))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "entities.test.MsetconfigurationMagenttask[ mSetConfigurationMAgentTaskPK="
				+ pk + " ]";
	}

}
