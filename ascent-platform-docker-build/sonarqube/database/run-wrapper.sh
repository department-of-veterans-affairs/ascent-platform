#!/bin/bash
ENVCONSUL_CONFIG="/template/envconsul-config.hcl"
CMD="/docker-entrypoint.sh postgres"

if [[ $VAULT_TOKEN ]]; then
  envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
  export POSTGRES_USER=sonar
  export POSTGRES_PASSWORD=sonar
  $CMD "$@"
fi

wait
