#!/usr/bin/env bash


######################################
## Shell script to run `mvn clean install` on each of the ascent projects
##
## Usage: put a file called `ascent-builds.txt` in the folder of the script containing the name of projects, line by line:
##
##git@github.com:department-of-veterans-affairs/ascent-discovery.git
##git@github.com:department-of-veterans-affairs/ascent-config.git
##git@github.com:department-of-veterans-affairs/ascent-gateway.git
######################################


cd ./../../
cwd=`pwd`
echo "Current working directoty $cwd"

eval $(docker-machine env manager1)

while read line
do
  projects+=( "$line" )
done < $cwd/build-projects.txt
for project in "${projects[@]}"
do
   :
	# try git@github.com:username/repo-name.git first
    name=$(echo $project | awk -F/ '{print $2}')
	if ! test -n "$name"; then
	    name=$(echo $project | awk -F/ '{print $5}')
	fi

	# try https://github.com/username/repo-name.git
	if ! test -n "$name"; then
	    echo "Unable to parse directory for $project"
	    continue
	fi

	name=${name%.git}
	echo $name
	# if the directory does not exist, clone the repos and run maven
	if [ ! -d "name" ]; then
		git clone $project ../$name
		cd ../$name
		git checkout development
		git pull
		echo "\nBuilding the project $name for $project\n"
		mvn clean install -DskipTests=true
		echo "\nPushing the docker image $name to local swarm registry\n"
		docker tag ascent/$name:latest localhost:5000/ascent/$name:latest
		docker push localhost:5000/ascent/$name:latest
		cd $cwd
	fi
done

cd ./ascent-platform-docker-build/elasticsearch
docker build -t ascent/ascent-elasticsearch .
docker tag ascent/ascent-elasticsearch:latest localhost:5000/ascent/ascent-elasticsearch:latest
docker push localhost:5000/ascent/ascent-elasticsearch:latest
cd ../fluentd
docker build -t ascent/fluentd .
docker tag ascent/fluentd:latest localhost:5000/ascent/fluentd:latest
docker push localhost:5000/ascent/fluentd:latest
cd ../kibana
docker build -t ascent/ascent-kibana .
docker tag ascent/ascent-kibana:latest localhost:5000/ascent/ascent-kibana:latest
docker push localhost:5000/ascent/ascent-kibana:latest
cd ../redis-sentinel
docker build -t ascent/redis-sentinel .
docker tag ascent/redis-sentinel:latest localhost:5000/ascent/redis-sentinel:latest
docker push localhost:5000/ascent/redis-sentinel:latest
cd ../rabbitmq
docker build -t ascent/ascent-amqp .
docker tag ascent/ascent-amqp:latest localhost:5000/ascent/ascent-amqp:latest
docker push localhost:5000/ascent/ascent-amqp:latest
cd ../es-config
docker build -t ascent/es-config .
docker tag ascent/es-config:latest localhost:5000/ascent/es-config:latest
docker push localhost:5000/ascent/es-config:latest
cd ../vault
docker build -t ascent/ascent-vault .
docker tag ascent/ascent-vault:latest localhost:5000/ascent/ascent-vault:latest
docker push localhost:5000/ascent/ascent-vault:latest
