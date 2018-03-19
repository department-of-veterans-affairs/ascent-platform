#!/usr/bin/env bash

#Starts sonar cube
docker-compose -f ../containers/sonarqube/docker-compose.yml up --build -d
