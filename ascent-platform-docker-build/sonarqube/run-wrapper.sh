#!/bin/bash
ENV_CONSUL_CONFIG=/opt/sonarqube/template/envconsul-config.hcl
ENV_CONSUL_CMD="envconsul -config $ENV_CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN"

# Install Github plugin
./provision/install_github_plugin.sh

# Start Sonar
./bin/run.sh &

# Wait for sonar to be up
./provision/wait_for_sonar.sh

# TODO: add polling for vault availability
echo "Changing admin password..."
if [[ $VAULT_TOKEN ]]; then
   echo "Changing admin password..."
   $ENV_CONSUL_CMD ./provision/set_new_password.sh
   echo "Setting ascent profile..."
   $ENV_CONSUL_CMD ./provision/set_main_profile.sh
   echo "Done!"
fi

wait
