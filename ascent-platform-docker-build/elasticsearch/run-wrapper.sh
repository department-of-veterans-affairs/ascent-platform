#!/bin/bash

ENVCONSUL_CONFIG="/usr/share/elasticsearch/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/elasticsearch/template/consul-template-config.hcl"
BASE_CONFIG="/usr/share/elasticsearch/template/elasticsearch.yml"
SSL_CONFIG="/usr/share/elasticsearch/template/elasticsearch.ssl.yml"
CMD="bin/es-docker"


if [[ -s $VAULT_TOKEN_FILE ]]; then
    echo "vault token file found and not empty."
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

# Install our custom config
cp $BASE_CONFIG /usr/share/elasticsearch/config/elasticsearch.yml




# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then

    # Create first password using vault
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/changepass.sh &

    # TODO: set up vault to accept ssl configs
    #cat $SSL_CONFIG >> /usr/share/elasticsearch/config/elasticsearch.yml
    #consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN"
    #envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/set-replicas.sh bg & envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"

    # Using this for now until the rest of the secrets are set up in vault
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/set-replicas.sh bg & $CMD "$@"
else
    /docker/changepass.sh &
    /docker/set-replicas.sh bg & $CMD "$@"
fi

