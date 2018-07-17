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
	# if the directory does not exist, clone the repos and run maven
	if [ ! -d "../$name" ]; then
		git clone $project ../$name
    fi
    
    cd ../$name
	
	latesttag=$(git ls-remote --tags $project | awk '{print $2}' | grep -v '{}' | awk -F"/" '{print $3}' | tail -n 1)
	echo $latesttag
	
	if [[ ! -z $latesttag ]]; then
		git checkout $latesttag
		echo "Building the project $name for $project"
		mvn clean install -DskipTests=true
	fi
	
	git checkout development
	git pull
	echo "\nBuilding the project $name for $project\n"
	mvn clean install -DskipTests=true

	
	git checkout development
	
	cd $cwd
done
