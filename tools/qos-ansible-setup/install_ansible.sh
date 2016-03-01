#!/bin/bash

if [[ $EUID -ne 0 ]]; then
    echo "Should be run as root"
    exit 1
fi

DIR=$(readlink -e "$(dirname "$0")")
REPO_FILE="/etc/yum.repos.d/localtmp.repo"

cat <<EOF > $REPO_FILE
[localtmp]
name=Local Repo
baseurl=file://${DIR}/roles/local_repo/files/localrepo
enabled=1
gpgcheck=0
EOF

yum -y --disablerepo=* --enablerepo=localtmp install ansible

rm -rf $REPO_FILE
