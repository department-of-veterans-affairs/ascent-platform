#!/bin/bash
ENV_CONSUL_CONFIG=/usr/share/configure/template/envconsul-config.hcl
ENVCONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"
BASE_DIR=/usr/share/configure
SONAR_DIR=$BASE_DIR/sonar
JENKINS_DIR=$BASE_DIR/jenkins

echo "---- POLLING ALL SERVICES UNTIL THEY'RE UP"
$ENVCONSUL_CMD /usr/share/configure/poll-services.sh

profileExists=`$ENVCONSUL_CMD /usr/share/configure/sonar/test-if-ascent-profile-exists.sh`

# TODO: make a base path for the scripts
if [ "$profileExists" = "FALSE" ]; then 
  echo "---- SONAR: SETTING MAIN PROFILE"
  $ENVCONSUL_CMD $SONAR_DIR/set_main_profile.sh
  
  echo ""
  echo "---- SONAR: CONFIGURING JENKINS WEBHOOK"
  $ENVCONSUL_CMD $SONAR_DIR/set_jenkins_webhook.sh

  echo "---- SONAR: GENERATING AN admin USER TOKEN"
  $ENVCONSUL_CMD $SONAR_DIR/set_token.sh
else
  echo "---- SONAR: profile already configured. WILL NOT configure anything"
fi

if [ "$CONFIGURE_JENKINS" = "true" ]; then
   echo "## CONFIGURING JENKINS"
   echo "---- JENKINS: SET SONAR TOKEN"
   $ENVCONSUL_CMD $JENKINS_DIR/set_sonar_global_config.sh
   
   echo "---- JENKINS: SETTING NEXUS CREDENTIALS"
   $ENVCONSUL_CMD $JENKINS_DIR/nexus_credentials.sh   
   
   echo "---- JENKINS: SETTING VAULT CREDENTIALS"
   $ENVCONSUL_CMD $JENKINS_DIR/vault_credentials.sh
 
   echo "---- JENKINS: SETTING GITHUB CREDENTIALS"
   $ENVCONSUL_CMD $JENKINS_DIR/github_credentials.sh
else 
   echo "## Jenkins set to NOT be configured."
   echo "   If you want to change this, set the env variable"
   echo "   CONFIGURE_JENKINS to true in your docker-compose file"
fi
echo "done!"

# TODO: Remove this when finished with all config changes
tail -f /dev/null
