CREATE OR REPLACE FUNCTION removeSendEmailActionSubjectBody() RETURNS VOID AS
  $BODY$
BEGIN

	IF EXISTS (select * from information_schema.columns where table_name = 'mpolicysendemailaction' and column_name='subject') THEN
		ALTER TABLE mpolicysendemailaction DROP COLUMN subject;
	END IF;

  IF EXISTS (select * from information_schema.columns where table_name = 'mpolicysendemailaction' and column_name='body') THEN
    ALTER TABLE mpolicysendemailaction DROP COLUMN body;
  END IF;

  RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT removeSendEmailActionSubjectBody();
