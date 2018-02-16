#!/bin/bash
API_TOKEN=`/usr/share/configure/jenkins/get-api-token.sh`
echo "API_TOKEN=$API_TOKEN"

CRUMB=`/usr/share/configure/jenkins/get-crumb.sh $API_TOKEN`
echo "CRUMB=$CRUMB"

# Vault Credential

# Check if it exists already
cred_id=Vault
cred_exists=`/usr/share/configure/jenkins/credential-exists.sh $cred_id`

echo "cred_id=$cred_id"
echo "cred_exists=$cred_exists"

# If the credentials with $cred_id doesn't exist, then set up variables to create a new onee
if [ "$cred_exists" = "FALSE" ]; then
   cred_url="$JENKINS_URL/credentials/store/system/domain/_/createCredentials"
   data='json={
           "": "0",
           "credentials": {
           "scope": "GLOBAL",
           "id": "'"$cred_id"'",
           "accessToken": "'"${GITHUB_VAULT_TOKEN}"'",
           "description": "Nexus Deployment",
           "$class": "com.datapipe.jenkins.vault.credentials.VaultGithubTokenCredential"
           }
         }'
# If the credential with $cred_id does exist, then update the existing
else 
   cred_url="$JENKINS_URL/credentials/store/system/domain/_/credential/$cred_id/updateSubmit"
   data='json={
    "stapler-class": "com.datapipe.jenkins.vault.credentials.VaultGithubTokenCredential",
    "scope": "GLOBAL", 
    "id": "'"$cred_id"'",
    "accessToken": "'"${GITHUB_VAULT_TOKEN}"'",
    "description": "Nexus Deployment" 
    }'

fi

echo "cred_url=$cred_url"
echo "data=$data"

curl -H $CRUMB -X POST -u ${JENKINS_USERNAME}:${API_TOKEN} "$cred_url" \
     --data-urlencode "$data"

