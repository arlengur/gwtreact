CREATE TABLE IF NOT EXISTS MPolicyActionsTemplate (
	id bigint NOT NULL,
	name character varying(255) NOT NULL,
	CONSTRAINT mpolicyactionstemplate_pkey PRIMARY KEY (id),
	CONSTRAINT mpolicyactionstemplate_name_key UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS MPolicyActionsTe_MPolicyAction (
  mpolicyactionstemplate_id bigint NOT NULL,
  actions_id bigint NOT NULL,
  CONSTRAINT mpolicyactionste_mpolicyaction_mpolicyactionstemplate_id_fk
  FOREIGN KEY (mpolicyactionstemplate_id)
      REFERENCES mpolicyactionstemplate (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);