create or replace
TRIGGER "QOS_PRO"."DEL_TASK"
BEFORE DELETE ON QOS_PRO.TASK
REFERENCING OLD AS OLD NEW AS NEW
FOR EACH ROW
BEGIN
  --delete qos_pro.parameters_values t where t.TASK_ID=:old.id;
  --commit;
  INSERT INTO QOS_IPAD.MEVENT(ID,TYPE,OWNER,VALUE,CREATEDDATETIME) 
  VALUES ((select nvl(max(id)+1,0) from QOS_IPAD.MEVENT),'TASK_COMPLETELY_DELETED',:old.id,null,sysdate);
END;