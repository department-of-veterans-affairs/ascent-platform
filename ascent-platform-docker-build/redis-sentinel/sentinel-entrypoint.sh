#!/bin/sh

ENVCONSUL_CONFIG="/usr/share/elasticsearch/template/envconsul-config.hcl"
SENTINAL_CONF="/redis/sentinel.conf"
CMD="redis-server /redis/sentinel.conf --sentinel"

if [[ -s $VAULT_TOKEN_FILE ]]; then
    echo "vault token file found and not empty."
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi


if [[ $VAULT_TOKEN ]]; then
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" /docker/configure-sentinal-conf.sh
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
    /docker/configure-sentinal-conf.sh
    $CMD "$@"
fi 
 
