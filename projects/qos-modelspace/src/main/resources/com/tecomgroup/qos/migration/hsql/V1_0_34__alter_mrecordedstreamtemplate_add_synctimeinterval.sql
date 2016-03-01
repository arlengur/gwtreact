alter table MRecordedStreamTemplate add column synctimeinter_clienttimezone varchar(255);
alter table MRecordedStreamTemplate add column synctimeinterval_enddatetime timestamp;
alter table MRecordedStreamTemplate add column synctimeinterval_startdatetime timestamp;
alter table MRecordedStreamTemplate add column synctimeinterval_timezone varchar(255);
alter table MRecordedStreamTemplate add column synctimeinterval_timezonetype varchar(255);
alter table MRecordedStreamTemplate add column synctimeinterval_type varchar(255);