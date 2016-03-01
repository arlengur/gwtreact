CREATE OR REPLACE FUNCTION addAlertSeverityChangeDateTime()
  RETURNS VOID AS
  $BODY$
  BEGIN
    IF NOT EXISTS(SELECT
                    *
                  FROM information_schema.columns
                  WHERE table_name = 'malert' AND column_name = 'severitychangedatetime')
    THEN
      ALTER TABLE MAlert ADD severitychangedatetime TIMESTAMP;
      UPDATE malert
      SET severitychangedatetime =
      GREATEST((SELECT
                  datetime
                FROM malertupdate
                WHERE field = 'perceivedSeverity' AND alert_id = malert.id
                ORDER BY datetime DESC
                LIMIT 1), creationdatetime);
      ALTER TABLE MAlert ALTER COLUMN severitychangedatetime SET NOT NULL;
    END IF;
    RETURN;
  END;
  $BODY$
LANGUAGE plpgsql;

SELECT
  addAlertSeverityChangeDateTime();