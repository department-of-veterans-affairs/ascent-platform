#!/bin/bash
#Create 3 nodes for our swarm. One manager and two workers.
docker-machine create --driver virtualbox \
  --virtualbox-memory 2048 \
  swarm-manager1
docker-machine create --driver virtualbox swarm-node1
docker-machine create --driver virtualbox swarm-node2



#Connect to the manager node and initialize our swarm
export MANAGER_IP="$(docker-machine ip swarm-manager1)"
docker-machine ssh swarm-manager1 "docker swarm init --advertise-addr $MANAGER_IP"

#Save the swarm tokens so we can join our worker nodes
export WORKER_TOKEN=$(docker-machine ssh swarm-manager1 "docker swarm join-token -q worker")
export MANAGER_TOKEN=$(docker-machine ssh swarm-manager1 "docker swarm join-token -q manager")

#Join our worker nodes to the swarm
docker-machine ssh swarm-node1 "docker swarm join --token $WORKER_TOKEN $MANAGER_IP:2377"
docker-machine ssh swarm-node2 "docker swarm join --token $WORKER_TOKEN $MANAGER_IP:2377"

#Connect to the swarm manager
eval $(docker-machine env swarm-manager1)

#Set labels on nodes
docker node update --label-add type=master swarm-node1
docker node update --label-add type=slave swarm-node2

docker-compose -f docker-compose.cache.swarm.yml -f docker-compose.cache.swarm.override.yml config > docker-stack.yml
docker stack deploy --compose-file docker-stack.yml redis-ha
