#!/bin/sh

sh /redis/configure-sentinel-conf.sh

echo "polling for redis..."
until redis-cli -a ${SPRING_REDIS_PASSWORD} -h redis-master -p 6379 PING | grep -m 1 "PONG"; 
do 
  sleep 5 
  echo "didn't get PONG. Trying again."
done

redis-server /redis/sentinel.conf --sentinel
