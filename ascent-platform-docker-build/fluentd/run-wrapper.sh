#!/bin/bash

ENVCONSUL_CONFIG="/fluentd/etc/template/envconsul-config.hcl"
CMD="fluentd -c /fluentd/etc/$FLUENTD_CONF -p /fluentd/plugins $FLUENTD_OPT"


if [[ -s $VAULT_TOKEN_FILE ]]; then
    echo "vault token file found and not empty."
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi




# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    # Using this for now until the rest of the secrets are set up in vault
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
    $CMD "$@"
fi


