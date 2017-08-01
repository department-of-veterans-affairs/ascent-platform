#!/bin/sh

######################################
## Shell script to run `mvn clean install` on each of the ascent projects
##
## Usage: put a file called `ascent-builds.txt` in the folder of the script containing the name of projects, line by line:
##
##ascent-discovery
##ascent-config
##ascent-gateway
######################################

cwd=`pwd`

while read line
do
  projects+=( "$line" )
done < $cwd/build-projects.txt


for project in "${projects[@]}"
do
   :
	name=$(echo $project | awk -F/ '{print $1}')

	if ! test -n "$name"; then
	    echo "Unable to parse directory for $project"
	    continue
	fi	

	# if the directory exist, then run maven
	if [ -d "../$name" ]; then
		cd ../$name
		echo "\nBuilding the project $name\n"
		mvn clean install
		cd $cwd
	else 
		echo "\nSkipping the project $name\n"
	fi
done