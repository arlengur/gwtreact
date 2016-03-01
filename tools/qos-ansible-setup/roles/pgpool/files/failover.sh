#!/bin/bash -x
FALLING_NODE=$1         # %d
OLDPRIMARY_NODE=$2      # %P
NEW_PRIMARY=$3          # %H
PGDATA=$4               # %R

if [ $FALLING_NODE = $OLDPRIMARY_NODE ]; then
    if [ $UID -eq 0 ]
    then
        sudo -u postgres ssh -T postgres@$NEW_PRIMARY "touch $PGDATA/trigger"
        exit 0
    fi
    ssh -T postgres@$NEW_PRIMARY "touch $PGDATA/trigger"
fi

exit 0
