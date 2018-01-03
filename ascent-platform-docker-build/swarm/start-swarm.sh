#!/bin/bash
#Create 3 nodes for our swarm. One manager and two workers.
docker-machine create --driver virtualbox \
  --virtualbox-memory 3072 \
  --virtualbox-boot2docker-url https://github.com/boot2docker/boot2docker/releases/download/v17.11.0-ce/boot2docker.iso \
  manager1
docker-machine create --driver virtualbox --virtualbox-boot2docker-url https://github.com/boot2docker/boot2docker/releases/download/v17.11.0-ce/boot2docker.iso --virtualbox-memory 3072 node1
docker-machine create --driver virtualbox --virtualbox-boot2docker-url https://github.com/boot2docker/boot2docker/releases/download/v17.11.0-ce/boot2docker.iso --virtualbox-memory 3072 node2

docker-machine ssh manager1 "sudo sysctl -w vm.max_map_count=262144"
docker-machine ssh node1 "sudo sysctl -w vm.max_map_count=262144"
docker-machine ssh node2 "sudo sysctl -w vm.max_map_count=262144"

#Connect to the manager node and initialize our swarm
export MANAGER_IP="$(docker-machine ip manager1)"
docker-machine ssh manager1 "docker swarm init --advertise-addr $MANAGER_IP"

#Save the swarm tokens so we can join our worker nodes
export WORKER_TOKEN=$(docker-machine ssh manager1 "docker swarm join-token -q worker")
export MANAGER_TOKEN=$(docker-machine ssh manager1 "docker swarm join-token -q manager")

#Join our worker nodes to the swarm
docker-machine ssh node1 "docker swarm join --token $WORKER_TOKEN $MANAGER_IP:2377"
docker-machine ssh node2 "docker swarm join --token $WORKER_TOKEN $MANAGER_IP:2377"

#Connect to the swarm manager
eval $(docker-machine env manager1)

#Set labels on nodes
docker node update --label-add type=master node1
docker node update --label-add type=slave node2

# Create local registry
docker service create --name registry --publish 5000:5000 registry:2

