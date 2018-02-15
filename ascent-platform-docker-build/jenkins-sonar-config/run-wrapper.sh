#!/bin/bash
ENV_CONSUL_CONFIG=/usr/share/configure/template/envconsul-config.hcl
ENVCONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"

echo "---- POLLING ALL SERVICES UNTIL THEY'RE UP"
$ENVCONSUL_CMD /usr/share/configure/poll-services.sh

profileExists=`$ENVCONSUL_CMD /usr/share/configure/sonar/test-if-ascent-profile-exists.sh`

if [ "$profileExists" = "FALSE" ]; then 
  echo "---- SONAR: SETTING MAIN PROFILE"
  $ENVCONSUL_CMD /usr/share/configure/sonar/set_main_profile.sh
  
  echo ""
  echo "---- SONAR: CONFIGURING JENKINS WEBHOOK"
  $ENVCONSUL_CMD /usr/share/configure/sonar/set_jenkins_webhook.sh

  echo "---- SONAR: GENERATING AN admin USER TOKEN"
  $ENVCONSUL_CMD /usr/share/configure/sonar/set_token.sh
else
  echo "---- SONAR: profile already configured. WILL NOT configure anything"
fi

if [ "$CONFIGURE_JENKINS" = "true" ]; then
   echo "## CONFIGURING JENKINS"
else 
   echo "## Jenkins set to NOT be configured."
   echo "   If you want to change this, set the env variable"
   echo "   CONFIGURE_JENKINS to true in your docker-compose file"
fi
echo "done!"
tail -f /dev/null
