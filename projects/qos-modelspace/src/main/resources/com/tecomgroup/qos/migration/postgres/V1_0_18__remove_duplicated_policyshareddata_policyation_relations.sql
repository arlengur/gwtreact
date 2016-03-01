DROP TABLE IF EXISTS temp_table;
CREATE TEMP TABLE temp_table AS 
SELECT DISTINCT mpolicyshareddata_id, actions_id FROM mpolicysharedd_mpolicysendal;
DELETE FROM mpolicysharedd_mpolicysendal;
INSERT INTO mpolicysharedd_mpolicysendal SELECT * FROM temp_table;