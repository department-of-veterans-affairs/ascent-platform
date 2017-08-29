#! /bin/bash

ENVCONSUL_CONFIG="/usr/share/elasticsearch/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/elasticsearch/template/consul-template-config.hcl"
CMD="bin/es-docker"

if [[ $VAULT_TOKEN_FILE ]]; then
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" $CMD "$@"
else
    $CMD "$@"
fi
