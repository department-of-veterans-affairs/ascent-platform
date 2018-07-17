#!/bin/bash

######################################
## Shell script to run `mvn clean install` on each of the ascent projects
##
## Usage: put a file called `ascent-builds.txt` in the folder of the script containing the name of projects, line by line:
##
##git@github.com:department-of-veterans-affairs/ascent-discovery.git 
##git@github.com:department-of-veterans-affairs/ascent-config.git 
##git@github.com:department-of-veterans-affairs/ascent-gateway.git 
######################################

cwd=`pwd`

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
	
	# if the directory does not exist, clone the repo
	if [ ! -d "../$name" ]; then
		git clone $project ../$name
    fi
    
    cd ../$name
	
	# prune local git tags that don't exist on remote
	git tag -l | xargs git tag -d && git fetch -t
	
	# get the latest tag
	latesttag=$(git describe --tags $(git rev-list --tags --max-count=1))
	
	# if the latest tag isn't emptym checkout and build it locally
	if [[ ! -z $latesttag ]]; then
		git checkout $latesttag
		git pull
		echo "Building the project $name for $project"
		mvn clean install -DskipTests=true
	fi
	
	# checkout development branch and build
	git checkout development
	git pull
	echo "Building the project $name for $project"
	mvn clean install -DskipTests=true
	
	cd $cwd
	
done

# clean up docker images
docker rmi $(docker image ls | grep 'none' | awk '{print $3}')
