#
#  Creates and initializes the metadata table in the schema
#  Example: 
#     init_existing
#   Also you can override flyway properties
#     init_existing -url=jdbc:hsqldb:hsql://localhost:9001/qosdb -user=qos_ipad -password=qos_ipad
#

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
ABSOLUTE_PATH=`dirname $ABSOLUTE_PATH`
FLYWAY_HOME=$ABSOLUTE_PATH/flyway

$FLYWAY_HOME/flyway $@ -initVersion=1 init