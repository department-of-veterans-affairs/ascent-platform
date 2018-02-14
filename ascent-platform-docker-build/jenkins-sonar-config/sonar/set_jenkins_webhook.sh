#!/bin/bash

curl -X POST \
  $SONAR_URL/api/settings/set?key=sonar.webhooks.global \
  -u admin:$SONAR_PASSWORD \
  -F "fieldValues={\"name\":\"jenkins\",\"url\":\"$JENKINS_URL/sonarqube-webhook/\"}"
