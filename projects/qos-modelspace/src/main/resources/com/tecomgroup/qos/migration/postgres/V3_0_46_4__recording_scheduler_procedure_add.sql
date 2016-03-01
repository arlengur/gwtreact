CREATE OR REPLACE FUNCTION recording_scheduler_seq_nextval (
  OUT out_id BIGINT)
  AS
  $BODY$
  BEGIN
      out_id := nextval('recording_scheduler_id_seq');
  END;
  $BODY$
LANGUAGE plpgsql;