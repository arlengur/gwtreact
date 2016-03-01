
CREATE OR REPLACE FUNCTION open_alert_report (
IN in_id BIGINT,
IN in_end_date_time TIMESTAMP,
IN in_severity VARCHAR(255),
IN in_start_date_time TIMESTAMP) RETURNS VOID
AS
  $BODY$
  BEGIN
    IF NOT EXISTS (SELECT * FROM MAlertReport WHERE alert_id = in_id AND enddatetime IS NULL) THEN
        INSERT INTO MAlertReport (id, enddatetime, perceivedseverity, startdatetime, alert_id )
          VALUES(nextval('malertreport_id_seq'), in_end_date_time,in_severity,in_start_date_time,in_id);
    END IF;
  END;
  $BODY$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION close_alert_report (
  OUT out_count BIGINT,
  IN in_id BIGINT,
  IN in_end_date_time TIMESTAMP)
  AS
  $BODY$
  BEGIN
      UPDATE MAlertReport SET enddatetime = in_end_date_time
        WHERE alert_id = in_id AND enddatetime IS NULL AND startdatetime < in_end_date_time;
      GET DIAGNOSTICS out_count = ROW_COUNT;
  END;
  $BODY$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_alert_update(
  IN in_comment VARCHAR(1024),
  IN in_date_time TIMESTAMP,
  IN in_field VARCHAR(255),
  IN in_new_value VARCHAR(255),
  IN in_out_value VARCHAR(255),
  IN in_update_type VARCHAR(255),
  IN in_user_name VARCHAR(255),
  IN in_alert_id BIGINT) RETURNS VOID
  AS
  $BODY$
  BEGIN
    INSERT INTO MalertUpdate (id, comment, datetime,
                            field, newvalue, oldvalue,
                            updatetype, user_name, alert_id)
    VALUES (nextval('malertupdate_id_seq'),in_comment,in_date_time,
            in_field,in_new_value,in_out_value,
            in_update_type,in_user_name,in_alert_id);
  END;
  $BODY$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_alert(
  OUT out_alert_id BIGINT,
  IN in_context VARCHAR(255),
  IN in_extra_data VARCHAR(255),
  IN in_perceived_severity INTEGER,
  IN in_settings VARCHAR(255),
  IN in_specific_reason VARCHAR(255),
  IN in_alert_type_id BIGINT,
  IN in_acknowledged BOOLEAN,
  IN in_acknowledgment_date_time TIMESTAMP,
  IN in_alert_count BIGINT,
  IN in_cleared_date_time TIMESTAMP,
  IN in_counts_since_last_ack BIGINT,
  IN in_creation_date_time TIMESTAMP,
  IN in_disabled BOOLEAN,
  IN in_last_update_date_time TIMESTAMP,
  IN in_last_update_type VARCHAR(255),
  IN in_status VARCHAR(255),
  IN in_originator_id BIGINT,
  IN in_source_id BIGINT,
  IN in_detection_value DOUBLE precision,
  IN in_severity_change_date_time TIMESTAMP)
AS
  $BODY$
  BEGIN
    out_alert_id := nextval('malert_id_seq');
  INSERT INTO MAlert (id,context,extradata,
                      perceivedseverity,settings,specificreason,
                      alerttype_id,acknowledged,acknowledgmentdatetime,
                      alert_count,cleareddatetime,countsincelastack,
                      creationdatetime,disabled,lastupdatedatetime,
                      lastupdatetype,status,originator_id,
                      source_id,detectionvalue,severitychangedatetime)
  VALUES (
    out_alert_id,in_context,in_extra_data,
    in_perceived_severity,in_settings,in_specific_reason,
    in_alert_type_id, in_acknowledged, in_acknowledgment_date_time,
    in_alert_count,in_cleared_date_time,in_counts_since_last_ack,
    in_creation_date_time,in_disabled,in_last_update_date_time,
    in_last_update_type,in_status,in_originator_id,
    in_source_id,in_detection_value,in_severity_change_date_time
  );
  END;
  $BODY$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_alert(
  IN in_alert_id BIGINT,
  IN in_context VARCHAR(255),
  IN in_extra_data VARCHAR(255),
  IN in_perceived_severity INTEGER,
  IN in_specific_reason VARCHAR(255),
  IN in_acknowledged BOOLEAN,
  IN in_acknowledgment_date_time TIMESTAMP,
  IN in_alert_count BIGINT,
  IN in_cleared_date_time TIMESTAMP,
  IN in_counts_since_last_ack BIGINT,
  IN in_creation_date_time TIMESTAMP,
  IN in_disabled BOOLEAN,
  IN in_last_update_date_time TIMESTAMP,
  IN in_last_update_type VARCHAR(255),
  IN in_status VARCHAR(255),
  IN in_detection_value DOUBLE precision,
  IN in_severity_change_date_time TIMESTAMP) RETURNS VOID
AS
  $BODY$
  BEGIN
  UPDATE MAlert SET context = in_context,extradata = in_extra_data,
    perceivedseverity = in_perceived_severity,specificreason = in_specific_reason,
    acknowledged = in_acknowledged,acknowledgmentdatetime=in_acknowledgment_date_time,
    alert_count=in_alert_count,cleareddatetime=in_cleared_date_time,countsincelastack=in_counts_since_last_ack,
    creationdatetime=in_creation_date_time,disabled=in_disabled,lastupdatedatetime=in_last_update_date_time,
    lastupdatetype=in_last_update_type,status=in_status,
    detectionvalue=in_detection_value,severitychangedatetime=in_severity_change_date_time
  WHERE id = in_alert_id;
  END;
  $BODY$
LANGUAGE plpgsql;