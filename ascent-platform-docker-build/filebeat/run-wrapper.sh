#! /bin/bash

ENVCONSUL_CONFIG="/usr/share/filebeat/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/filebeat/template/consul-template-config.hcl"
SSL_CONFIG="/usr/share/filebeat/filebeat.ssl.yml"

CMD=$1
if [[ -z $CMD ]]; then
    CMD="filebeat -e"
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" $CMD -c $SSL_CONFIG
else
    $CMD
fi
