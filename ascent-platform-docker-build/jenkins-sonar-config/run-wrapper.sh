#!/bin/bash
ENV_CONSUL_CONFIG=/usr/share/configure/template/envconsul-config.hcl
ENVCONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"

echo "---- POLLING ALL SERVICES UNTIL THEY'RE UP"
$ENVCONSUL_CMD /usr/share/configure/poll-services.sh

echo "---- SONAR: SETTING MAIN PROFILE"
$ENVCONSUL_CMD /usr/share/configure/sonar/set_main_profile.sh

echo "---- SONAR: CONFIGURING JENKINS WEBHOOK"
$ENVCONSUL_CMD /usr/share/configure/sonar/set_jenkins_webhook.sh

echo "---- SONAR: GENERATING AN admin USER TOKEN"
$ENVCONSUL_CMD /usr/share/configure/sonar/set_token.sh

echo "done!"
tail -f /dev/null
