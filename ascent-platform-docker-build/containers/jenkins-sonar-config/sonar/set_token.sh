#!/bin/bash

# Generate Sonar token
export SONAR_TOKEN=`curl -XPOST -u admin:$SONAR_PASSWORD $SONAR_URL/api/user_tokens/generate?name=JenkinsToken | python -c 'import sys, json; print json.load(sys.stdin)["token"]'`
echo ""
echo "SONAR_TOKEN=$SONAR_TOKEN"

# Replace ********** with sonar token in groovy script
sed -i "s@\*\*\*\*\*\*\*\*\*\*@$SONAR_TOKEN@g" /usr/share/configure/jenkins/sonar_global_config.groovy

