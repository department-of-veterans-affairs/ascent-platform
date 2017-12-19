#!/usr/bin/env bash
# Make sure shell is set to manager1 to before running docker stack ascent platform
eval $(docker-machine env manager1)

docker stack deploy --compose-file ./docker-compose.swarm.yml ascent

