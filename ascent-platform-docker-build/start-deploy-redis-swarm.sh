#!/bin/bash
export SENTINEL_HOSTNAME=$1
export REDIS_MASTER_HOSTNAME=$2
export REDIS_SLAVE_HOSTNAME=$3

if [ -z $SENTINEL_HOSTNAME ] || [ -z $REDIS_MASTER_HOSTNAME ] || [ -z $REDIS_SLAVE_HOSTNAME ] ; then
  echo "Argument missing" >&2
  exit 1;
fi

export SENTINEL_IP=192.168.99.106
export REDIS_MASTER_IP=192.168.99.108

echo "Sentinel: $SENTINEL_HOSTNAME -  $SENTINEL_IP"
echo "Redis master: $REDIS_MASTER_HOSTNAME - $REDIS_MASTER_IP"
echo "Redis slave: $REDIS_SLAVE_HOSTNAME"

docker stack deploy -c docker-compose.cache.swarm.yml rha