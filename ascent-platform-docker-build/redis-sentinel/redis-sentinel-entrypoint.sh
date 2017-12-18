#!/bin/sh

ENVCONSUL_CONFIG="/redis/template/envconsul-config.hcl"
CMD=$START_COMMAND

if [[ $VAULT_TOKEN ]]; then
    echo "using vault token"
    echo "--- polling to wait for vault"
    until $(curl -XGET --output /dev/null --silent --head "X-Vault-Token: $VAULT_TOKEN" --fail  $VAULT_ADDR/secret/application/?list=true  ); do
       echo "--trying again"
       sleep 5
    done

    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
    $CMD "$@"
fi 
 
