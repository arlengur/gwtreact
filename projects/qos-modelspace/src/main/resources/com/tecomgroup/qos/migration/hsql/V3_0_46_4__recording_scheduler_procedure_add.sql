CREATE PROCEDURE recording_scheduler_seq_nextval (
    OUT out_id BIGINT)
  MODIFIES SQL DATA
  BEGIN ATOMIC
    SELECT NEXT VALUE FOR recording_scheduler_id_seq into out_id FROM muser where muser.id = 1; -- #1399 Cannot assign sequence value to a variable in a stored procedure
  END;