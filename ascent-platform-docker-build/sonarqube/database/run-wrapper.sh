#!/bin/bash
ENVCONSUL_CONFIG="/template/envconsul-config.hcl"
CMD="/opt/cpm/bin/start.sh"

# Have the sql init script set theg
# database encoding to UTF8 once run
SETUP_SCRIPT="/opt/cpm/bin/setup.sql"
sed -i 's|create database PG_DATABASE|create database PG_DATABASE with encoding "UTF8" template=template0|g' $SETUP_SCRIPT

if [[ $VAULT_TOKEN ]]; then
  envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
  export POSTGRES_USER=sonar
  export POSTGRES_PASSWORD=sonar
  $CMD "$@"
fi

tail -f /dev/null
wait
