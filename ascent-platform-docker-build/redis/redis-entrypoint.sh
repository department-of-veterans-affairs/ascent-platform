#!/bin/sh

sed -i "s/\$redis_port/$REDIS_PORT/g" redis.conf 

if [[ $REDIS_ROLE = "master" ]]; then
    echo "master" 
    sed -i "s/\$REDIS_PORT/$REDIS_PORT/g" redis.conf 
    redis-server /redis/redis.conf 
else
    echo "slave" 
    sed -i "s/\$REDIS_PORT/$REDIS_PORT/g" redis.conf 
    sed -i "s/#slaveof/slaveof/g" redis.conf 
    sed -i "s/\$MASTER_PORT/$MASTER_PORT/g" redis.conf 
    redis-server /redis/redis.conf 
fi