create or replace
TRIGGER "QOS_PRO"."UPD_TASK"
 BEFORE
 UPDATE OF PERIOD, ACTION_ID, CREATION_USER, MOD_TS, AGENT_ID, ID, CREATION_DATE, VERSION, DELETED, TASK_GROUP_ID, DEVICE_INSTANCE_ID, MOD_USER
 ON QOS_PRO.TASK
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
begin
    :new.mod_ts := systimestamp;
    if :old.mod_vers < 100 then
        :new.mod_vers := :old.mod_vers + 1;
    else
        :new.mod_vers := 1;
    end if;

    if nvl(:old.deleted,0) = 0 and nvl(:new.deleted, 0) = 0 and :new.period<>:old.period then
        :new.version := :old.version + 1;
         INSERT INTO QOS_IPAD.MEVENT(ID,TYPE,OWNER,VALUE,CREATEDDATETIME) 
 VALUES ((select nvl(max(id)+1,0) from qos_ipad.mevent),'MONITORING_INTERVAL_CHANGED',:new.id,:new.period,sysdate);
    end if;
    
    if :old.deleted=0 and :new.deleted!=0 then
    INSERT INTO QOS_IPAD.MEVENT(ID,TYPE,OWNER,VALUE,CREATEDDATETIME) 
    VALUES ((select nvl(max(id)+1,0) from qos_ipad.mevent),'TASK_DELETED',:old.id,null,sysdate);
    end if;
  
    if :new.task_group_id = 0 then
        :new.task_group_id := null;
    end if;
    
    if :new.version<>:old.version then
    	 INSERT INTO QOS_IPAD.MEVENT(ID,TYPE,OWNER,VALUE,CREATEDDATETIME) 
 VALUES ((select nvl(max(id)+1,0) from qos_ipad.mevent),'VERSION_CHANGED',:new.id,:new.version,sysdate);
    end if;

end;