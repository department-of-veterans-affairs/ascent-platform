JENKINS_URL="http://localhost:8080"
# Generate authentication token
ADMIN_USER="admin"
ADMIN_PASSWORD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)
# Get the API token for CLI authentication
API_TOKEN=$(curl -u $ADMIN_USER:$ADMIN_PASSWORD $JENKINS_URL/me/configure | sed -rn 's/.*id="apiToken"[^>]*value="([a-z0-9]+)".*/\1/p')

CRUMB=$(curl -s -u ${ADMIN_USER}:${API_TOKEN} "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")

# GitHub Authentication
curl -H $CRUMB -X POST -u ${ADMIN_USER}:${API_TOKEN} "$JENKINS_URL/credentials/store/system/domain/_/createCredentials" \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "Github Authentication",
    "secret": "'"${GITHUB_TOKEN}"'",
    "description": "Authentication to VA Github Repository",
    "$class": "org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl"
  }
}'

# Nexus Authentication
curl -H $CRUMB -X POST -u ${ADMIN_USER}:${API_TOKEN} "$JENKINS_URL/credentials/store/system/domain/_/createCredentials" \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "Nexus",
    "username": "'"$NEXUS_USER"'",
    "password": "'"$NEXUS_PASSWORD"'",
    "description": "Nexus Deployment",
    "$class": "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl"
  }
}'

# Vault Credential
curl -H $CRUMB -X POST -u ${ADMIN_USER}:${API_TOKEN} "$JENKINS_URL/credentials/store/system/domain/_/createCredentials" \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "Vault",
    "accessToken": "'"${GITHUB_TOKEN}"'",
    "description": "Nexus Deployment",
    "$class": "com.datapipe.jenkins.vault.credentials.VaultGithubTokenCredential"
  }
}'
