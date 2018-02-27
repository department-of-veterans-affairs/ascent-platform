#!/usr/bin/env bash

#Stops sonar cube
docker-compose -f docker-compose.yml \
    down --rmi=all
