#!/bin/bash
ENV_CONSUL_CONFIG=/opt/sonarqube/template/envconsul-config.hcl
ENV_CONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"

# Install Github plugin
./provision/install_github_plugin.sh

if [[ $VAULT_TOKEN ]]; then
   # poll for vault
   echo "--- polling to wait for vault"
   until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
     echo "output of curl for vault"
     curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health
     echo "--trying again"
     sleep 5
   done
 
   # Change the jdbc env variable to have USER instead of USERNAME
   # to match what's going to come from envconsul
   sed -i 's/USERNAME/USER/g' ./bin/run.sh 
  
   # Start Sonar
   $ENV_CONSUL_CMD ./bin/run.sh &
else 
   ./bin/run.sh &
fi

# Wait for sonar to be up
./provision/wait_for_sonar.sh
./provision/set_main_profile.sh

if [[ $VAULT_TOKEN ]]; then
   echo "Changing admin password..."
   $ENV_CONSUL_CMD ./provision/set_new_password.sh
fi

echo "Done!"
wait
