CREATE TABLE IF NOT EXISTS MSetConfiguration
(
  id bigint NOT NULL,
  timeinterval bigint NOT NULL,
  ischannel boolean NOT NULL ,
  name character varying(255) NOT NULL,
  logo character varying(1024),
  CONSTRAINT msetconfiguration_pk PRIMARY KEY (id)
);