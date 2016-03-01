CREATE OR REPLACE FUNCTION copyEmailAndSmsActionsAndContactsRelationshipsToNewTable() RETURNS VOID AS
$BODY$
BEGIN
  IF EXISTS (select * from information_schema.columns where table_name = 'mpolicysendem_mcontactinformat') THEN
  	insert into mpolicyaction_mcontactinformat (mpolicyactionwithcontacts_id, contacts_id) select mpolicysendemailaction_id, contacts_id from mpolicysendem_mcontactinformat;
  	drop table mpolicysendem_mcontactinformat;
  END IF;
  
  IF EXISTS (select * from information_schema.columns where table_name = 'mpolicysendsms_mcontactinfor') THEN
  	insert into mpolicyaction_mcontactinformat (mpolicyactionwithcontacts_id, contacts_id) select mpolicysendsmsaction_id, contacts_id from mpolicysendsms_mcontactinfor;
  	drop table mpolicysendsms_mcontactinfor;
  END IF;

  RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT copyEmailAndSmsActionsAndContactsRelationshipsToNewTable();