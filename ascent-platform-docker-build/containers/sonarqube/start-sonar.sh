#!/usr/bin/env bash

#Starts sonar cube
docker-compose -f ./docker-compose_local/docker-compose.yml ../sonar-database/docker-compose_local/docker-compose.yml up --build -d
