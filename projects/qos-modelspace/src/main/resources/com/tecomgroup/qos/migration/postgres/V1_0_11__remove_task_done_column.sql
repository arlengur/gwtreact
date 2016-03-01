CREATE OR REPLACE FUNCTION removeTaskDoneColumn() RETURNS VOID AS
$BODY$
BEGIN
	IF EXISTS (select * from information_schema.columns where table_name = 'magenttask' and column_name='done') THEN
		ALTER TABLE MAgentTask DROP COLUMN done;
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT removeTaskDoneColumn();