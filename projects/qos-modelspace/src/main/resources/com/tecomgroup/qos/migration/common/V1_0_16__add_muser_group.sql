CREATE TABLE IF NOT EXISTS MUserGroup (
	id bigint NOT NULL,
  	name character varying(255) NOT NULL,
  	CONSTRAINT musergroup_pkey PRIMARY KEY (id),
  	CONSTRAINT musergroup_name_key UNIQUE (name)
);