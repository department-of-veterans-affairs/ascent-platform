#!/bin/bash

CREDENTIAL=$1
if $(curl -u $JENKINS_USERNAME:$JENKINS_PASSWORD --fail --output /dev/null --silent $JENKINS_URL/credentials/store/system/domain/_/credential/$CREDENTIAL); then
  echo "TRUE"
else
  echo "FALSE"
fi
