ALTER TABLE msetconfiguration
   ADD COLUMN favourite boolean;

update msetconfiguration set favourite=false where favourite is null;

alter TABLE msetconfiguration
   alter COLUMN favourite SET not null;