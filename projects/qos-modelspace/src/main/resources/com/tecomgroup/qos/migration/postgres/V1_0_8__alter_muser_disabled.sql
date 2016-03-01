CREATE OR REPLACE FUNCTION addDisabledUserSupport() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muser' and column_name='disabled') THEN
		alter table MUser add disabled boolean; 
		update MUser set disabled=false where disabled IS NULL; 
		alter table MUser alter column disabled set not null; 
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addDisabledUserSupport();