package com.tecomgroup.qos.domain.rbac;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import javax.persistence.*;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Type;
import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * Created by kiselev.a on Sat Feb 20 10:20:20 2016.
 */

@Entity
public class UISubject extends MAbstractEntity {
	private static final long serialVersionUID = -4209543869377685170L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "rbac_seq")
	@SequenceGenerator(name = "rbac_seq",
			sequenceName = "rbac_id_seq", allocationSize = 1)
	@JsonIgnore
	@Column
	private Long id;

	@Column
	private String name;

	@Type(type="com.tecomgroup.qos.modelspace.hibernate.HibernateStringArrayType")
	@Column(name="extension") 
	private Set<String> extension;

	public UISubject() {
		return;
	}

	public UISubject(String name) {
		setName(name);
	}

	public UISubject(Long id, String name) {
		setId(id);
		setName(name);
	}

	public UISubject(String		name, 
					 String ... exts) {
		setName(name);
		setExtension(new HashSet<String>(Arrays.asList(exts)));
	}

	public Set<String> getExtension() {
		return this.extension;
	}
	
	public void setExtension(Set<String> extension) {
		this.extension = extension;
	}

	// TODO: hasExtension
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ""+id+":"+name;
	}

	@Override
	public int hashCode() {
		int sumCode = 0;
		if(extension != null) {
			for(String item : extension) {
				sumCode += item.hashCode();
			}
		}
		return (id == null ? 0 :id.hashCode()) 
			+ (name == null ? 0 : name.hashCode())
			+ sumCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || (obj instanceof UISubject) == false) {
			return false;
		}

		UISubject other = (UISubject)obj;
		boolean extensionEquals = false;

		if(getExtension() == other.getExtension()) {
			extensionEquals = true;
		} else if(extension != null && other.getExtension() != null) {
			extensionEquals = other.getExtension().size() == extension.size() &&
				extension.containsAll(other.getExtension());
		} 
		
		return getName().equals(other.getName())
			&& getId().equals(other.getId())
			&& extensionEquals;
	}
}
