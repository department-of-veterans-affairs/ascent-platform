#!/bin/bash

API_TOKEN=`/usr/share/configure/jenkins/get-api-token.sh`
CRUMB=`/usr/share/configure/jenkins/get-crumb.sh $API_TOKEN`

cred_id=nexus
cred_exists=`/usr/share/configure/jenkins/credential-exists.sh $cred_id`

if [ "$cred_exists" = "FALSE" ]; then
   cred_url="$JENKINS_URL/credentials/store/system/domain/_/createCredentials"
   data='json={
           "": "0",
           "credentials": {
           "scope": "GLOBAL",
           "id": "'"$cred_id"'",
           "username": "'"$NEXUS_USERNAME"'",
           "password": "'"$NEXUS_PASSWORD"'",
           "description": "Nexus Deployment",
           "$class": "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl"
           }
         }'
else 
   cred_url="$JENKINS_URL/credentials/store/system/domain/_/credential/$cred_id/updateSubmit"
   data='json={
    "stapler-class": "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl",
    "scope": "GLOBAL", 
    "username": "'"$NEXUS_USERNAME"'",
    "password": "'"$NEXUS_PASSWORD"'", 
    "id": "'"$cred_id"'", 
    "description": "Nexus Deployment" 
    }'

fi
echo "cred_id=$cred_id"
echo "cred_exists=$cred_exists"
echo "cred_url=$cred_url"
echo "data=$data"

# Nexus Authentication
curl -H $CRUMB -X POST -u ${JENKINS_USERNAME}:${API_TOKEN} "$cred_url" \
     --data-urlencode "$data"


