#!/bin/sh

# --- Remove all the sentinal stuff
rm /redis/sentinal-wrapper.sh
rm sentinel.conf
rm sentinel-wrapper.sh

echo "redis pass is: ${REDIS_SENTINEL_PASSWORD}"
redis-server --appendonly no --save "" --repl-diskless-sync yes --requirepass "${REDIS_SENTINEL_PASSWORD}" --masterauth "${REDIS_SENTINEL_PASSWORD}"
