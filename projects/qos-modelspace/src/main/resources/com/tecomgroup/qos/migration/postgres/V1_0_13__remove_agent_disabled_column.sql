CREATE OR REPLACE FUNCTION removeAgentDisabledColumn() RETURNS VOID AS
$BODY$
BEGIN
	IF EXISTS (select * from information_schema.columns where table_name = 'magent' and column_name='disabled') THEN
		ALTER TABLE MAgent DROP COLUMN disabled;
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT removeAgentDisabledColumn();