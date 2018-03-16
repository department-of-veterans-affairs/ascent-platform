#!/bin/bash
ENV_CONSUL_CONFIG=/usr/share/configure/template/envconsul-config.hcl
ENVCONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"
BASE_DIR=/usr/share/configure
SONAR_DIR=$BASE_DIR/sonar
JENKINS_DIR=$BASE_DIR/jenkins

echo "---- POLLING ALL SERVICES UNTIL THEY'RE UP"
$ENVCONSUL_CMD /usr/share/configure/poll-services.sh


echo "---- SONAR: CONFIGURING JENKINS WEBHOOK"
$ENVCONSUL_CMD $SONAR_DIR/set_jenkins_webhook.sh

echo "---- SONAR: GENERATING AN admin USER TOKEN"
$ENVCONSUL_CMD $SONAR_DIR/set_token.sh

echo "## CONFIGURING JENKINS"
echo "---- JENKINS: SET SONAR TOKEN"
$ENVCONSUL_CMD $JENKINS_DIR/set_sonar_global_config.sh

echo "---- JENKINS: SETTING NEXUS CREDENTIALS"
$ENVCONSUL_CMD $JENKINS_DIR/nexus_credentials.sh

echo "---- JENKINS: SETTING VAULT CREDENTIALS"
$ENVCONSUL_CMD $JENKINS_DIR/vault_credentials.sh
 
echo "---- JENKINS: SETTING GITHUB CREDENTIALS"
$ENVCONSUL_CMD $JENKINS_DIR/github_credentials.sh
echo "done!"

