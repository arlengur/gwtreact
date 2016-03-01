#!/bin/bash

# Works only on centos like systems
# Create repo in current direcotry

POSTGRES_VER=9.4.4
PGPOOL_VER=3.4.3
LIBSELINUX_VER=2.0.94

DIR=$(dirname "$0")
REPO_PATH="${DIR}/roles/local_repo/files/localrepo"

CONSUL_FILES="${DIR}/roles/consul/files/"
CONSUL_VER="0.5.2"
CONSUL_ZIP=https://dl.bintray.com/mitchellh/consul/${CONSUL_VER}_linux_amd64.zip
CONSUL_UI_ZIP=https://dl.bintray.com/mitchellh/consul/${CONSUL_VER}_web_ui.zip

yum -y install yum-utils createrepo \
     http://yum.postgresql.org/9.4/redhat/rhel-6-x86_64/pgdg-redhat94-9.4-1.noarch.rpm \
     http://download.fedoraproject.org/pub/epel/6/i386/epel-release-6-8.noarch.rpm

repotrack -p "$REPO_PATH" yum-utils rsync ansible\
          libselinux-python-$LIBSELINUX_VER \
          postgresql94-libs-$POSTGRES_VER \
          postgresql94-$POSTGRES_VER \
          postgresql94-server-$POSTGRES_VER \
          pgpool-II-94-$PGPOOL_VER \
          pgpool-II-94-extensions-$PGPOOL_VER \
          nano \
          unzip \
          nfs-utils \
          erlang

wget -P "$REPO_PATH" -N https://www.rabbitmq.com/releases/rabbitmq-server/v3.5.5/rabbitmq-server-3.5.5-3.noarch.rpm

createrepo "$REPO_PATH"

wget -P "$CONSUL_FILES" -N $CONSUL_ZIP
wget -P "$CONSUL_FILES" -N $CONSUL_UI_ZIP
