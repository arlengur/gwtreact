alter table MUser add ldap_authenticated boolean; 
update MUser set ldap_authenticated=false where ldap_authenticated IS NULL; 
alter table MUser alter column ldap_authenticated set not null; 