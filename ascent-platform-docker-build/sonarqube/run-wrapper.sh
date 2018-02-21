#!/bin/bash
ENV_CONSUL_CONFIG=/opt/sonarqube/template/envconsul-config.hcl
CMD=./provision/set_new_password.sh

# Install Github plugin
./provision/install_github_plugin.sh

# Start Sonar
./bin/run.sh &

if [[ $VAULT_TOKEN ]]; then
   echo "Changing admin password..."
   envconsul -config="$ENV_CONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
   echo "Done!"
fi

wait
