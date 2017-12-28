#!/bin/bash

ENVCONSUL_CONFIG="/etc/rabbitmq/template/envconsul-config.hcl"
CMD="/docker-entrypoint.sh rabbitmq-server"

echo "--- polling to wait for vault"
until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/application); do
  echo "--trying again"
  sleep 5
done

envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
