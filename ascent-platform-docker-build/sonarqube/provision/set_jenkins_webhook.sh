#!/bin/bash

# TODO: Let the jenkins URL be more flexible by 
#       an environment variable to be overridden 
#       through the docker-compose file
JENKINS_URL=http://jenkins:8080

curl -X POST \
  'http://localhost:9000/api/settings/set?key=sonar.webhooks.global' \
  -u admin:admin \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F "fieldValues={\"name\":\"test5\",\"url\":\"$JENKINS_URL/sonarqube-webhook/\"}"
