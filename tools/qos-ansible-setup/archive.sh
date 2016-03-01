#!/bin/bash

DIR=$(dirname "$0")
SETUP_DIR=$DIR/setup
SETUP_TAR=setup.tar.gz

# Clear setup
rm -rf "$SETUP_DIR" $SETUP_TAR

mkdir -p "$SETUP_DIR"

cp -r README.md all.yml hosts install_ansible.sh pgpool.yml postgres.yml roles \
   vars "$SETUP_DIR"

tar -czf $SETUP_TAR "$SETUP_DIR"

# Delete tmp dir
rm -rf "$SETUP_DIR"
