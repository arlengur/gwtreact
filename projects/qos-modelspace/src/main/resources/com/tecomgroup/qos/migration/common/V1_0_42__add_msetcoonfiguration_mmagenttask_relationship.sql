CREATE TABLE IF NOT EXISTS mpolicyaction_mcontactinformat (
  mpolicyactionwithcontacts_id bigint NOT NULL,
  contacts_id bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS msetconfiguration_magenttask
(
  defaultstreamsource boolean NOT NULL,
  pk_msetconfiguration_id bigint NOT NULL,
  pk_magenttask_id bigint NOT NULL,
  CONSTRAINT msetconfiguration_magenttask_pkey PRIMARY KEY (pk_msetconfiguration_id, pk_magenttask_id),
  CONSTRAINT msetconfiguration_magenttask_pk_magenttask_id_fkey FOREIGN KEY (pk_magenttask_id)
      REFERENCES magenttask (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT msetconfiguration_magenttask_pk_msetconfiguration_id_fkey FOREIGN KEY (pk_msetconfiguration_id)
      REFERENCES msetconfiguration (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);