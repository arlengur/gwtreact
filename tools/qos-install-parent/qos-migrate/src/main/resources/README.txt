####################################
#  Tools to inialize and upgrade 
#  Q'ligent Vision database schema
####################################

1) Clean install
1.1) Create empty database schema (in psql or PgAdmin for postgres)
1.2) Check that all properties in flyway/conf/flyway.properties are correct
1.3) Execute init_empty

2) Upgrade
2.1) Database schema is already created and ready for flyway
2.2) Check that all properties in flyway/conf/flyway.properties are correct
2.3) Execute init_existing
