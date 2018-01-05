#!/bin/sh

until rabbitmqctl set_policy ha-all "" '{"ha-mode":"all", "ha-sync-mode":"automatic"}'
do
  echo "-- trying again in 5 seconds"
  sleep 5
done
echo "----- policy set"

