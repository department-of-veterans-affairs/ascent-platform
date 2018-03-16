#!/bin/bash

# Get the API token for CLI authentication
API_TOKEN=$(curl -u $JENKINS_USERNAME:$JENKINS_PASSWORD $JENKINS_URL/me/configure | sed -rn 's/.*id="apiToken"[^>]*value="([a-z0-9]+)".*/\1/p')
echo "$API_TOKEN"
