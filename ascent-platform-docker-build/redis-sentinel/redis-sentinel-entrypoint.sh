#!/bin/sh

ENVCONSUL_CONFIG="/redis/template/envconsul-config.hcl"
CMD=$START_COMMAND

if [[ $VAULT_TOKEN ]]; then
    echo "using vault token"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
    $CMD "$@"
fi 
 
