CREATE OR REPLACE FUNCTION copyTemplatesAndActionsRelationshipsToNewTable() RETURNS VOID AS
$BODY$
BEGIN
  IF EXISTS (select * from information_schema.columns where table_name = 'mpolicyactionste_mpolicyaction') THEN
  	insert into mpolicyaction_mpolicyactionwit (mpolicyactionstemplate_id, actions_id) select mpolicyactionstemplate_id, actions_id from mpolicyactionste_mpolicyaction;
  	drop table mpolicyactionste_mpolicyaction;
  END IF;

  RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT copyTemplatesAndActionsRelationshipsToNewTable();