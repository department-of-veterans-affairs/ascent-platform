#!/bin/bash

# TODO: set from vault instead with a switch if $VAULT_TOKEN exists or not
export JENKINS_USERNAME=jenkins
export JENKINS_PASSWORD=jenkins
export JENKINS_URL=http://localhost:8080


printf "Checking availability of Jenkins "
COUNT=0
until $(curl --output /dev/null --silent --head -m 10 --fail $JENKINS_URL/login) || [ "$COUNT" == 5 ]; do
    COUNT=$(($COUNT+1))
    printf "."
    sleep 5
done

# Error out if apparent that jenkins is not up
if [ "$COUNT" == 5 ]; then
    printf "\nLooks like something is wrong with Jenkins"
    printf "\nCould be incorrect url or credentials"
    echo "   JENKINS_URL=$JENKINS_URL"
    echo "   JENKINS_USERNAME=$JENKINS_USERNAME"
    echo "   JENKINS_PASSWORD=$JENKINS_PASSWORD"
    echo "NOT going to attempt to set the sonar token in Jenkins"
    exit -1
fi

# Download the jenkins CLI client
curl -f $JENKINS_USERNAME:$JENKINS_PASSWORD -L -o jenkins-cli.jar $JENKINS_URL/jnlpJars/jenkins-cli.jar

# Get the API token for CLI authentication
API_TOKEN=$(curl -u admin:$ADMIN_PASSWORD $JENKINS_URL/me/configure | sed -rn 's/.*id="apiToken"[^>]*value="([a-z0-9]+)".*/\1/p')

JENKINS_CLI="java -jar jenkins-cli.jar -s $JENKINS_URL -auth admin:$API_TOKEN"

# Set global config sonar server in jenkins
java -jar jenkins-cli.jar groovy sonar_global_config.groovy
