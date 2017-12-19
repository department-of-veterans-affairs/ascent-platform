#!/bin/bash

ENVCONSUL_CONFIG="/usr/share/elasticsearch/template/envconsul-config.hcl"
default_pass=changeme

if [[ -s $VAULT_TOKEN_FILE ]]; then
    echo "vault token file found and not empty."
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

echo "--- Polling to wait for elasticsearch to be up"
until $(curl -XGET --output /dev/null --silent --head --fail -u elastic:$default_pass elasticsearch:9200/_cat/health); do
    sleep 5
done

echo "--- Polling to wait for elasticsearch to be ready to configure"
curl -XGET -u elastic:$default_pass 'elasticsearch:9200/_cluster/health?wait_for_status=green'


# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    # Create first password using vault
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/changepass.sh
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/set-replicas.sh
else
    /docker/changepass.sh
    /docker/set-replicas.sh
fi

