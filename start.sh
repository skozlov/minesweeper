#!/usr/bin/env bash

java -jar $(realpath $(dirname $0))/app/target/mines.jar "$@"