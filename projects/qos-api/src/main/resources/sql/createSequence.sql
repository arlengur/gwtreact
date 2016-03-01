DROP FUNCTION GET_ID
;
DROP SEQUENCE global_id_seq
;
CREATE SEQUENCE global_id_seq
 START WITH     1
 INCREMENT BY   1
 NOCACHE
 NOCYCLE
;
CREATE OR REPLACE FUNCTION get_id RETURN INTEGER AS
    id NUMBER;
BEGIN
   SELECT global_id_seq.nextval INTO id FROM DUAL;
   RETURN id;
END;