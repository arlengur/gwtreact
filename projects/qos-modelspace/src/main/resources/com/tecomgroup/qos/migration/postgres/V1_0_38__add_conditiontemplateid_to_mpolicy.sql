CREATE OR REPLACE FUNCTION addConditionTemplateIdToMPolicy() RETURNS VOID AS
$BODY$
BEGIN
    IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicy' and column_name='conditionstemplate_id') THEN
        ALTER TABLE MPolicy ADD COLUMN conditionstemplate_id bigint DEFAULT null;
    END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addConditionTemplateIdToMPolicy();