CREATE INDEX mresultconfigur_mresultparamet_mresultconfigurationsharedd_idx ON mresultconfigur_mresultparamet (mresultconfigurationsharedd_id);
CREATE INDEX magenttask_parent_deleted_idx ON magenttask (parent_id, deleted);
CREATE INDEX magenttask_mproperty_magenttask_idx ON magenttask_mproperty (magenttask_id);
CREATE INDEX magetnmodule_parent_idx ON magentmodule (parent_id);
CREATE INDEX mmediaagentmodule_parent_idx ON mmediaagentmodule (parent_id);
