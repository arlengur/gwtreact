
create or replace view MDevice 
(
      ID,
      CREATEDBY,
      CREATIONDATETIME,
      MODIFICATIONDATETIME,
      MODIFIEDBY,
      VERSION,
      DESCRIPTION,
      NAME,
      DEVICEDETAILS_ID,
      DEVICETYPE_ID,
      DIVISION_ID,
      MODEL_ID
)
as
select 
      ID,
      CREATION_USER,
      CREATION_DATE,
      MOD_TS,
      MOD_USER,
      MOD_VERS,
      NOTE,
      NAME,
      -- it is necessary to add device properties (ADDRESS, IS_DEVICE_SERVICED, AAA) to MProperty, MDevice_MProperty tables
      -- device details is not supported right now
      null,
      (select device_type_id from QOS_PRO.Model model where model.id=device_instance.model_id),
      DIVISION_ID,
      MODEL_ID
from QOS_PRO.Device_Instance
;
    
    

    