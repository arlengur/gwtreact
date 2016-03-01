/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

import javax.persistence.*;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MFakeTask extends MLoggedEntity {

	@Entity(name = "MFakeTaskAttribute")
	public static class MFakeTaskAttribute extends MAbstractEntity {
		@Column
		private String name;

		@Column
		private String value;
		@Id
        @GeneratedValue(strategy = GenerationType.TABLE)
        @JsonIgnore
        private Long id;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		public Long getId() {
            return id;
        }

		public void setId(Long id) {
            this.id = id;
        }
	}

	@OneToOne(cascade = CascadeType.ALL)
	private MFakeTaskAttribute superAttribute;

	@OneToMany(cascade = CascadeType.ALL)
	private List<MFakeTaskAttribute> attributes;

	@Embedded
	private Source source;

	private String name;

	@Column(length = 1024)
	private String description;

	public MFakeTask() {
	}

	public MFakeTask(final String name) {
		this.name = name;
	}

	public MFakeTask(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the attributes
	 */
	public List<MFakeTaskAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the source
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * @return the superAttribute
	 */
	public MFakeTaskAttribute getSuperAttribute() {
		return superAttribute;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(final List<MFakeTaskAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final Source source) {
		this.source = source;
	}

	/**
	 * @param superAttribute
	 *            the superAttribute to set
	 */
	public void setSuperAttribute(final MFakeTaskAttribute superAttribute) {
		this.superAttribute = superAttribute;
	}
}
