DROP TABLE "MLIVEBROADCAST"
;

CREATE TABLE "MLIVEBROADCAST" (
"ID" NUMBER(19,0), 
"IOSREFERENCE" VARCHAR2(255), 
"REFERENCE" VARCHAR2(255), 
"TITLE" VARCHAR2(255)) 
;

--NN
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177802','http://91.228.161.16/oflaDemo/streams/ipad/live.m3u8','rtmpt://91.228.161.16:80/oflaDemo/stream720','Прямой эфир')
;
--Ufa
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177803','http://81.30.220.218/oflaDemo/streams/ipad/live.m3u8','rtmpt://81.30.220.218:80/oflaDemo/stream720','Прямой эфир')
;
--Kirov
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177804','http://77.72.137.210/oflaDemo/streams/ipad/live.m3u8','rtmpt://77.72.137.210:80/oflaDemo/stream720','Прямой эфир')
;
--Yaroslavl
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177805','http://37.60.179.242/oflaDemo/streams/ipad/live.m3u8','rtmpt://37.60.179.242:80/oflaDemo/stream720','Прямой эфир')
;
--Perm
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177806','http://195.69.158.68/oflaDemo/streams/ipad/live.m3u8','rtmpt://195.69.158.68:80/oflaDemo/stream720','Прямой эфир')
;
--Kazan
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177807','http://31.13.131.14/oflaDemo/streams/ipad/live.m3u8','rtmpt://31.13.131.14:80/oflaDemo/stream720','Прямой эфир')
;
--Novosibirsk
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177808','http://91.190.236.179/oflaDemo/streams/ipad/live.m3u8','rtmpt://91.190.236.179:80/oflaDemo/stream720','Прямой эфир')
;
--SPB
Insert into MLIVEBROADCAST (ID,IOSREFERENCE,REFERENCE,TITLE) values ('5177809','http://31.28.9.230/oflaDemo/streams/ipad/live.m3u8','rtmpt://31.28.9.230:80/oflaDemo/stream720','Прямой эфир')
;


--------------------------------------------------------
--  DDL for Index SYS_C0012285
--------------------------------------------------------

CREATE UNIQUE INDEX "UniqeIndexMLiveBroadcast" ON "MLIVEBROADCAST" ("ID") 
;

--------------------------------------------------------
--  Constraints for Table MLIVEBROADCAST
--------------------------------------------------------

ALTER TABLE "MLIVEBROADCAST" MODIFY ("ID" NOT NULL ENABLE)
;
 
ALTER TABLE "MLIVEBROADCAST" ADD PRIMARY KEY ("ID") ENABLE
;