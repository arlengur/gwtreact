alter table MAgent add deleted boolean; 
update MAgent set deleted=false;
alter table MAgent alter column deleted set not null;

alter table MAgentTask add deleted boolean; 
update MAgentTask set deleted=false;
alter table MAgentTask alter column deleted set not null;

alter table MPolicy add deleted boolean; 
update MPolicy set deleted=false;
alter table MPolicy alter column deleted set not null;

alter table MPolicyTemplate add deleted boolean; 
update MPolicyTemplate set deleted=false;
alter table MPolicyTemplate alter column deleted set not null;