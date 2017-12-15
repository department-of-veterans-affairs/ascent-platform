#!/bin/sh

sh /redis/configure-sentinel-conf.sh
redis-server /redis/sentinel.conf --sentinel
