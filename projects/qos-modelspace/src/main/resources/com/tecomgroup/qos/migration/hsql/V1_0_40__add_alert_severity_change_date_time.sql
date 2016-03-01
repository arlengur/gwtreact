ALTER TABLE MAlert ADD COLUMN severitychangedatetime timestamp;
UPDATE malert set severitychangedatetime=
   GREATEST((SELECT datetime from malertupdate where field='perceivedSeverity' and alert_id=malert.id ORDER BY datetime DESC LIMIT 1), creationdatetime);
ALTER TABLE MAlert ALTER COLUMN severitychangedatetime SET NOT NULL;