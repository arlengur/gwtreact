alter table MFakeTask add createdby varchar(255);
alter table MFakeTask add creationdatetime timestamp;
alter table MFakeTask add modificationdatetime timestamp;
alter table MFakeTask add modifiedby varchar(255);
alter table MFakeTask add version int8 not null;