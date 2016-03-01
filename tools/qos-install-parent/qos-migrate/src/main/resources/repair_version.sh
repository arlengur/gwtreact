#
# Repair the database
#

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
ABSOLUTE_PATH=`dirname $ABSOLUTE_PATH`
FLYWAY_HOME=$ABSOLUTE_PATH/flyway

$FLYWAY_HOME/flyway $@ repair