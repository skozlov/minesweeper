#!/usr/bin/env bash

(cd $(realpath $(dirname $0)) && mvn clean package)