CREATE TABLE IF NOT EXISTS MPolicyConditionsTemplate (
	id bigint NOT NULL,
	name character varying(255) NOT NULL,
    conditionlevels_id bigint not null,
	CONSTRAINT mpolicyconditionstemplate_pkey PRIMARY KEY (id),
	CONSTRAINT mpolicyconditionstemplate_name_key UNIQUE (name),
	CONSTRAINT mpolicyconditionstemplate_conditionlevels_key UNIQUE (conditionlevels_id)
);