CREATE PROCEDURE open_alert_report (
  OUT out_id BIGINT,
  IN in_id BIGINT,
  IN in_end_date_time DATETIME,
  IN in_severity VARCHAR(255),
  IN in_start_date_time DATETIME)
  MODIFIES SQL DATA
  BEGIN ATOMIC
    IF NOT EXISTS (SELECT * FROM MAlertReport WHERE alert_id = in_id AND enddatetime IS NULL) THEN
        INSERT INTO MAlertReport (id, enddatetime, perceivedseverity, startdatetime, alert_id )
          VALUES(NEXT VALUE FOR malertreport_id_seq, in_end_date_time,in_severity,in_start_date_time,in_id);
      SET  out_id=IDENTITY();
    END IF;
  END;

CREATE PROCEDURE close_alert_report (
  OUT out_count BIGINT,
  OUT out_id BIGINT,
  IN in_id BIGINT,
  IN in_end_date_time DATETIME)
  MODIFIES SQL DATA
    BEGIN ATOMIC
      UPDATE MAlertReport SET enddatetime = in_end_date_time
        WHERE alert_id = in_id AND enddatetime IS NULL AND startdatetime < in_end_date_time;
      SET out_id=IDENTITY();
      GET DIAGNOSTICS out_count = ROW_COUNT;
  END;