#!/bin/bash

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

# Get the API token for CLI authentication
API_TOKEN=$(curl -u $JENKINS_USERNAME:$JENKINS_PASSWORD $JENKINS_URL/me/configure | sed -rn 's/.*id="apiToken"[^>]*value="([a-z0-9]+)".*/\1/p')
echo "API_TOKEN=$API_TOKEN"

# Get a crumb to authenticate to jenkins
CRUMB=$(curl -s -u ${JENKINS_USERNAME}:${API_TOKEN} "$JENKINS_URL/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
echo "CRUMB=$CRUMB"


echo "executing curl..."
curl -d "script=$(cat /opt/sonarqube/provision/sonar_global_config.groovy)" -H $CRUMB -v --user $JENKINS_USERNAME:$API_TOKEN $JENKINS_URL/scriptText


