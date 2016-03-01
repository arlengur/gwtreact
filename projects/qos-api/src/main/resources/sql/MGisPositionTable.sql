--------------------------------------------------------
--  DDL for Table MGISPOSITION
--------------------------------------------------------
DROP TABLE "MGISPOSITION"
;
CREATE TABLE "MGISPOSITION" ("ID" NUMBER(19,0), "LATITUDE" FLOAT(126), "LONGITUDE" FLOAT(126), "TITLE" VARCHAR2(255 CHAR))
;

Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979408','58,600945','49,650904','610017, Россия, г. Киров, ул. Горького, 5')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979409','55,781135','49,134332','420107, Россия, г. Казань, ул. Петербургская, 52')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979410','58,002095','56,295723','Россия, г.Пермь, ул.Аркадия Гайдара 8Б')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979411','57,637063','39,876205','150003, Россия, г. Ярославль, ул. Победы, д. 16 б')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979412','59,907318','30,26055','190020, Россия, г. Санкт-Петербург, ул. Лифляндская, д. 3, завод "Советская Звезда"')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979413','54,783266','56,031449','Россия, г. Уфа, ул. Проспект Октября, д. 103')
;
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979414','56,319524','44,021377','Россия, г. Нижний Новгород, ул. Горького, д. 195')
;
--Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979415','56,320892','44,036826','Россия, г. Нижний Новгород, ул. Сеченова, д. 2a');
Insert into MGISPOSITION (ID,LATITUDE,LONGITUDE,TITLE) values ('4979416','55,021491','82,927355','630007, г. Новосибирск, ул. Серебренниковская, 14')
;

--------------------------------------------------------
--  DDL for Index SYS_C0044981
--------------------------------------------------------

  CREATE UNIQUE INDEX "SYS_C0044981" ON "MGISPOSITION" ("ID") 
  ;

--------------------------------------------------------
--  Constraints for Table MGISPOSITION
--------------------------------------------------------

  ALTER TABLE "MGISPOSITION" MODIFY ("ID" NOT NULL ENABLE)
  ;
 
  ALTER TABLE "MGISPOSITION" MODIFY ("LATITUDE" NOT NULL ENABLE)
  ;
 
  ALTER TABLE "MGISPOSITION" MODIFY ("LONGITUDE" NOT NULL ENABLE)
  ;
 
  ALTER TABLE "MGISPOSITION" ADD CONSTRAINT "SYS_C0044981" PRIMARY KEY ("ID") ENABLE
  ;


