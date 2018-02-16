#!/bin/bash

API_TOKEN=`/usr/share/configure/jenkins/get-api-token.sh`
echo "API_TOKEN=$API_TOKEN"

CRUMB=`/usr/share/configure/jenkins/get-crumb.sh $API_TOKEN`
echo "CRUMB=$CRUMB"

# Hit the jenkins /scriptText endpoint to run the groovy script
# Works the same way for update as well as creation, so creates if doesn't
#   exist, and updates if id exists already
curl -d "script=$(cat /usr/share/configure/jenkins/sonar_global_config.groovy)" -H $CRUMB -v --user $JENKINS_USERNAME:$API_TOKEN $JENKINS_URL/scriptText
