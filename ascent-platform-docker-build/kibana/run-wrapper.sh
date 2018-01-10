#! /bin/bash

ENVCONSUL_CONFIG="/usr/share/kibana/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/kibana/template/consul-template-config.hcl"
SSL_CONFIG="/usr/share/kibana/config/kibana.yml"
BASE_CONFIG="/usr/share/kibana/template/kibana.yml"
CMD="/usr/local/bin/kibana-docker"

if [[ $VAULT_TOKEN_FILE ]]; then
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

if [[ -z $SECURE_CONNECT ]]; then
    SECURE_CONNECT=false
fi

cp $BASE_CONFIG /usr/share/kibana/config/kibana.yml

/docker/wait-es-container-shutdown.sh

# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    if [ "$SECURE_CONNECT" = true ]; then
        consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN"
    fi
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" $CMD "$@"
else
    $CMD "$@"
fi
