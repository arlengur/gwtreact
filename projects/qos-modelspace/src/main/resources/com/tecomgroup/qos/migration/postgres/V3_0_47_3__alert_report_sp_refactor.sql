DROP FUNCTION open_alert_report(bigint,timestamp without time zone,character varying,timestamp without time zone);
DROP FUNCTION close_alert_report(bigint,timestamp without time zone);
CREATE OR REPLACE FUNCTION open_alert_report (
OUT out_id BIGINT,
IN in_id BIGINT,
IN in_end_date_time TIMESTAMP,
IN in_severity VARCHAR(255),
IN in_start_date_time TIMESTAMP)
AS
  $BODY$
  BEGIN
    IF NOT EXISTS (SELECT * FROM MAlertReport WHERE alert_id = in_id AND enddatetime IS NULL) THEN
      out_id:=nextval('malertreport_id_seq');
      INSERT INTO MAlertReport (id, enddatetime, perceivedseverity, startdatetime, alert_id )
        VALUES(out_id, in_end_date_time,in_severity,in_start_date_time,in_id);
    END IF;
  END;
  $BODY$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION close_alert_report (
  OUT out_count BIGINT,
  OUT out_id BIGINT,
  IN in_id BIGINT,
  IN in_end_date_time TIMESTAMP)
  AS
  $BODY$
  BEGIN
      UPDATE MAlertReport SET enddatetime = in_end_date_time
        WHERE alert_id = in_id AND enddatetime IS NULL AND startdatetime < in_end_date_time
        RETURNING  id into out_id;
      GET DIAGNOSTICS out_count = ROW_COUNT;
  END;
  $BODY$
LANGUAGE plpgsql;