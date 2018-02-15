#!/bin/bash
echo "--- polling to wait for vault"
echo "VAULT_ADDR=$VAULT_ADDR"
until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
    echo "--trying again"
    sleep 5
done

echo "--- polling to wait for jenkins"
echo "JENKINS_URL=$JENKINS_URL"
until $(curl -XGET --fail --output /dev/null --silent -u $JENKINS_USERNAME:$JENKINS_PASSWORD $JENKINS_URL/login); do
    echo "--trying again"
    sleep 5
done

echo "--- polling to wait for sonar"
echo "SONAR_URL=$SONAR_URL"
until $(curl -s -u admin:$SONAR_PASSWORD -f --output /dev/null "$SONAR_URL/api/system/info"); do
    echo "--trying again"
    sleep 5
done




