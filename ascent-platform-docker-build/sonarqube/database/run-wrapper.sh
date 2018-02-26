#!/bin/bash
ENVCONSUL_CONFIG="/template/envconsul-config.hcl"
CMD="/opt/cpm/bin/start.sh"

# Have the sql init script set theg
# database encoding to UTF8 once run
SETUP_SCRIPT="/opt/cpm/bin/setup.sql"
sed -i 's|create database PG_DATABASE|create database PG_DATABASE with encoding "UTF8" template=template0|g' $SETUP_SCRIPT

if [[ $VAULT_TOKEN ]]; then
  # poll for vault
   echo "--- polling to wait for vault"
   until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
     echo "--trying again"
     sleep 5
   done
 
  envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
  export PG_USER=sonar
  export PG_PASSWORD=sonar
  export PG_ROOT_PASSWORD=postgres
  export PG_DATABASE=$PG_USER
  export PG_PRIMARY_USER=primary
  export PG_PRIMARY_PASSWORD=replicate
  $CMD "$@"
fi

tail -f /dev/null
