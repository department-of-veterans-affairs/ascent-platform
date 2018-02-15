#!/bin/bash

API_TOKEN=`/usr/share/configure/jenkins/get-api-token.sh`
echo "API_TOKEN=$API_TOKEN"

CRUMB=`/usr/share/configure/jenkins/get-crumb.sh $API_TOKEN`
echo "CRUMB=$CRUMB"

# Vault Credential
curl -H $CRUMB -X POST -u $JENKINS_USERNAME:${API_TOKEN} "$JENKINS_URL/credentials/store/system/domain/_/createCredentials" \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "Vault",
    "accessToken": "'"${GITHUB_VAULT_TOKEN}"'",
    "description": "Nexus Deployment",
    "$class": "com.datapipe.jenkins.vault.credentials.VaultGithubTokenCredential"
  }
}'
