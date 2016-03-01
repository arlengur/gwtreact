CREATE OR REPLACE FUNCTION addParameterTypeToPolicyConditionsTemplate() RETURNS VOID AS
$BODY$
BEGIN
    IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicyconditionstemplate' and column_name='parametertype') THEN
        ALTER TABLE MPolicyConditionsTemplate ADD COLUMN  parametertype integer;
    END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addParameterTypeToPolicyConditionsTemplate();