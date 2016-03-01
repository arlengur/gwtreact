create or replace
TRIGGER "QOS_PRO"."INS_TASK"
 BEFORE
 INSERT
 ON QOS_PRO.TASK
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
 :new.mod_vers:=1;
 :new.version:=1;
 :new.mod_ts := systimestamp;
-- :new.creation_date:=sysdate;
 if :new.task_group_id = 0 then
    :new.task_group_id := null;
 end if;
 INSERT INTO QOS_IPAD.MEVENT(ID,TYPE,OWNER,VALUE,CREATEDDATETIME) 
 VALUES ((select nvl(max(id)+1,0) from qos_ipad.mevent),'TASK_CREATED',:new.id,null,sysdate);
 INSERT INTO QOS_IPAD.MAGENTTASK(
    ID,
    CREATEDBY,
    CREATIONDATETIME,
    MODIFICATIONDATETIME,
    MODIFIEDBY,
    VERSION,
    MODULE_ID,
    RESULTCONFIGURATION_ID,
    MONITORINGINTERVAL,
    ISDONE
) VALUES (
    :new.id,
    :new.creation_user, 
    :new.creation_date, 
    :new.mod_ts, 
    :new.mod_user, 
    :new.version,
    :new.action_id,
    null,
    :new.period,
    case when :new.deleted > 0 then 1 else 0 end
);
end;