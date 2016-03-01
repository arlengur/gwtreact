/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tecomgroup.qos.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import static javax.persistence.GenerationType.*;

/**
 *
 * @author stroganov.d
 */
@Entity
@Table(name = "msetconfiguration")
@XmlRootElement
public class MSetConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private long timeinterval;
    private String name;
    private boolean ischannel;
    private String logo;
    private Collection<MSetConfigurationMAgentTask> msetConfigurationMagentTaskCollection =new ArrayList<>(0);
    private long userId;
    private boolean isFavourite;

    public MSetConfiguration() {
    }

    public MSetConfiguration(Long id) {
        this.id = id;
    }

    public MSetConfiguration(Long id, long timeinterval, String name, boolean ischannel) {
        this.id = id;
        this.timeinterval = timeinterval;
        this.name = name;
        this.ischannel = ischannel;
    }

    @Id
    //@GeneratedValue(strategy = IDENTITY)
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id", unique = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "ischannel")
    public boolean getChannel() {
        return ischannel;
    }

    public void setChannel(boolean ischannel) {
        this.ischannel = ischannel;
    }

    public long getTimeinterval() {
        return timeinterval;
    }

    public void setTimeinterval(long timeinterval) {
        this.timeinterval = timeinterval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @OneToMany(mappedBy = "pk.msetconfiguration", cascade=CascadeType.ALL)
    public Collection<MSetConfigurationMAgentTask> getMsetConfigurationMagentTaskCollection() {
        return msetConfigurationMagentTaskCollection;
    }

    public void setMsetConfigurationMagentTaskCollection(Collection<MSetConfigurationMAgentTask> msetConfigurationMagentTaskCollection) {
        this.msetConfigurationMagentTaskCollection = msetConfigurationMagentTaskCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MSetConfiguration)) {
            return false;
        }
        MSetConfiguration other = (MSetConfiguration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.test.MSetConfiguration[ id=" + id + " ]";
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isFavourite() { return isFavourite;}

    public void setFavourite(boolean isFavourite) { this.isFavourite = isFavourite; }

}
