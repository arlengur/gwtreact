#!/bin/bash

host=$1
port=$2

if ! nc -z "$host" "$port"; then
    exit 2
fi
