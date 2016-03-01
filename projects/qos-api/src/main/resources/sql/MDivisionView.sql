
create or replace view MDivision (ID, NAME, ORDERNUMBER, PARENT_ID) as select ID, NAME, ORDER_NUMBER, PARENT_ID from REFER.REF_Division
;