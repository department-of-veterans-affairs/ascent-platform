#!/bin/bash

API_TOKEN=`/usr/share/configure/jenkins/get-api-token.sh`
CRUMB=`/usr/share/configure/jenkins/get-crumb.sh $API_TOKEN`

# Nexus Authentication
curl -H $CRUMB -X POST -u ${JENKINS_USERNAME}:${API_TOKEN} "$JENKINS_URL/credentials/store/system/domain/_/createCredentials" \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "Nexus",
    "username": "'"$NEXUS_USERNAME"'",
    "password": "'"$NEXUS_PASSWORD"'",
    "description": "Nexus Deployment",
    "$class": "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl"
  }
}'


