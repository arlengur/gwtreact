CREATE TABLE IF NOT EXISTS mpolicyaction_mpolicyactionwit (
  mpolicyactionstemplate_id bigint NOT NULL,
  actions_id bigint NOT NULL,
  CONSTRAINT mpolicyaction_mpolicyactionwit_mpolicyactionstemplate_id_fkey FOREIGN KEY (mpolicyactionstemplate_id)
      REFERENCES mpolicyactionstemplate (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT mpolicyaction_mpolicyactionwit_actions_id_key UNIQUE (actions_id)
)