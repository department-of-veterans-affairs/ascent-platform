#!/bin/bash

# TODO: Error checking
API_TOKEN=$1

# Get a crumb to authenticate to jenkins
CRUMB=$(curl -s -u ${JENKINS_USERNAME}:${API_TOKEN} "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
echo "$CRUMB"
