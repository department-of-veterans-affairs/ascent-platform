#!/bin/sh

# --- Remove all the sentinal stuff
rm /redis/sentinel-wrapper.sh
rm /redis/sentinel.conf


echo "redis pass is: ${SPRING_REDIS_PASSWORD}"
redis-server --appendonly no --save "" --repl-diskless-sync yes --requirepass "${SPRING_REDIS_PASSWORD}" --masterauth "${SPRING_REDIS_PASSWORD}" "$@"
