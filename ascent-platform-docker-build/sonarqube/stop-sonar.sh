#!/usr/bin/env bash

#Stops sonar cube
#   Can add additional args when starting up 
#   like `stop-sonar.sh -v` to remove volumes
docker-compose -f docker-compose.yml \
    down --rmi=all "$@"
