CREATE OR REPLACE FUNCTION addActionsTemplateIdToMPolicy() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicy' and column_name='actionstemplate_id') THEN
    	ALTER TABLE MPolicy ADD COLUMN actionstemplate_id bigint DEFAULT null;
   	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addActionsTemplateIdToMPolicy();