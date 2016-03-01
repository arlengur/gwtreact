CREATE OR REPLACE FUNCTION addLdapUserSupport() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muser' and column_name='ldap_authenticated') THEN
		alter table MUser add ldap_authenticated boolean; 
		update MUser set ldap_authenticated=false where ldap_authenticated IS NULL; 
		alter table MUser alter column ldap_authenticated set not null; 
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addLdapUserSupport();