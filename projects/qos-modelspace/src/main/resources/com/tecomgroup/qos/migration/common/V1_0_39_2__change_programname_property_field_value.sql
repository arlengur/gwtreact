update mproperty SET
required=true
where id in (SELECT prop.id
  FROM mresultparameterconf_mproperty
  join mproperty as prop on mresultparameterconf_mproperty.properties_id = prop.id 
  join mresultconfigur_mresultparamet on mresultparameterconf_mproperty.mresultparameterconfigurat_id = mresultconfigur_mresultparamet.parameterconfigurations_id
  join magenttask as task on mresultconfigur_mresultparamet.mresultconfigurationsharedd_id = task.resultconfiguration_id
  where prop.name like 'programName' and task.entity_key like '%MpegTSStatistics%');


update mproperty SET
required=true
where id in (SELECT prop.id
    FROM mresultconfiguratio_mproperty
  join mproperty as prop on mresultconfiguratio_mproperty.propertyconfigurations_id = prop.id 
  join mresultconfiguration on mresultconfiguratio_mproperty.mresultconfigurationtempl_id = mresultconfiguration.templateresultconfiguration_id
  join magenttask as task on mresultconfiguration.id = task.resultconfiguration_id
  where prop.name like 'programName');