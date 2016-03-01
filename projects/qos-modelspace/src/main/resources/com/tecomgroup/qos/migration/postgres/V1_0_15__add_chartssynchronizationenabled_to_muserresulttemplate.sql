CREATE OR REPLACE FUNCTION addUserResultTemplateChartsSynchronizationEnabledColumn() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='chartssynchronizationenabled') THEN
		ALTER TABLE MUserResultTemplate ADD chartssynchronizationenabled boolean; 
		UPDATE MUserResultTemplate set chartssynchronizationenabled=false;
		ALTER TABLE MUserResultTemplate ALTER COLUMN chartssynchronizationenabled set not null;
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addUserResultTemplateChartsSynchronizationEnabledColumn();