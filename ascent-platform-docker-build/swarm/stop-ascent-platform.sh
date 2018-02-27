#!/usr/bin/env bash

# Make sure shell is set to manager1 to remove ascent platform
eval $(docker-machine env manager1)

docker stack rm ascent

