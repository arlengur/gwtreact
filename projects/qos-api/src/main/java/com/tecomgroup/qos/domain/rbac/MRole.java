package com.tecomgroup.qos.domain.rbac;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import javax.persistence.*;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.hibernate.annotations.Type;
import com.tecomgroup.qos.domain.MAbstractEntity;

/**
 * Created by kiselev.a on Sat Feb 20 10:32:57 2016.
 */

@Entity
public class MRole extends MAbstractEntity {
	private static final long serialVersionUID = 7291748661266382256L;

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

	@Column
	private String comment;

	@Type(type="com.tecomgroup.qos.modelspace.hibernate.HibernateUISubjectArrayType")
	@Column(name="subjects") 
	private Set<UISubject> subjects;
	
	public MRole() {
		return;
	}

	public MRole(String name) {
		this.name = name;
	}

	public MRole(String name, UISubject ... sbj) {
		this(name);
		setSubjects(new HashSet<UISubject>(Arrays.asList(sbj)));
	}

	public MRole(Long id, 
				 String name, 
				 UISubject ... sbj) {
		this(name, sbj);
		setId(id);
	}

	public Set<UISubject> getSubjects() {
		return this.subjects;
	}

	public Long[] getSubjectsId() {
		ArrayList<Long> rst = new ArrayList<Long>();
		if(subjects != null) {
			for(UISubject uis : subjects) {
				rst.add(uis.getId());
			}
		}
		return rst.toArray(new Long[subjects.size()]);
	}
	
	public void setSubjects(Set<UISubject> subjects) {
		this.subjects = subjects;
	}

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

	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isPermitted(List<UISubject> page) {
		if(subjects != null) {
			for(UISubject uis : subjects) {
				if(page.contains(uis)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isPermitted(UISubject page) {
		if(subjects != null) {
			for(UISubject uis : subjects) {
				if(page.equals(uis)) {
					return true;
				}
			}
		}
		return false;
	}
	public List<String> getPermittedSubjects() {
		List<String> names = new ArrayList<String>();
		if(subjects != null) {
			for(UISubject uis: this.subjects) {
				names.add(uis.getName());
			}
		}
		return names;
	}

	@Override
	public int hashCode() {
		int sumCode = 0;
		if(subjects != null) {
			for(UISubject item : subjects) {
				sumCode += item.hashCode();
			}
		}
		return (id == null ? 0 :id.hashCode()) 
			+ (name == null ? 0 : name.hashCode())
			+ (comment == null ? 0 : comment.hashCode())
			+ sumCode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || (obj instanceof MRole) == false) {
			return false;
		}

		MRole other = (MRole)obj;
		boolean subjectsEquals = false;

		if(getSubjects() == other.getSubjects()) { //the same or both is null
			subjectsEquals = true;
		} else if(getSubjects() != null && other.getSubjects() != null) {
			subjectsEquals = other.getSubjects().size() == getSubjects().size() 
				&& subjects.containsAll(other.getSubjects());
		}

		boolean commentEq = false;

		if(getComment() == other.getComment()) {
			commentEq = true;
		} else if(getComment() != null && other.getComment() != null) {
			commentEq = getComment().equals(other.getComment());
		}

		return getName().equals(other.getName()) 
			&& getId().equals(other.getId())
			&& commentEq
			&& subjectsEquals;
	}
	
	@Override
	public String toString() {
		return ""+id+":"+name;
	}
}
