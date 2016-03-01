if [ "$1" = '-help' ] ; then
	echo ""
	echo "Usage:"
	echo "<parameters>			runs policy manager with provided params"
	echo "Example:"
	echo "policy.sh amqp.host=localhost"
	echo ""
	echo "-version			prints policy manager version"
	echo "-help				prints this help"
	exit 1
fi

if [ "$1" = '-version' ] ; then
	RUN_PARAMS=$1
	shift
fi

while [[ ! -z $1 ]]
do
	D_PARAMS=$D_PARAMS" "-D$1
	shift
done

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
ABSOLUTE_PATH=`dirname $ABSOLUTE_PATH`
PM_HOME=$ABSOLUTE_PATH
CLASSPATH=$PM_HOME:$PM_HOME/lib/*

java -Dpm.home=$PM_HOME $D_PARAMS -cp "$CLASSPATH" com.tecomgroup.qos.pm.PolicyManager $RUN_PARAMS
