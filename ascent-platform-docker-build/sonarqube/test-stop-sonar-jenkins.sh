#!/bin/bash

docker-compose -f ./docker-compose.jenkins-sonar.yml \
	down -v --rmi=all
