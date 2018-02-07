#!/usr/bin/env bash

#Stops sonar cube
docker-compose -f docker-compose.yml \
    down -v --rmi=all
