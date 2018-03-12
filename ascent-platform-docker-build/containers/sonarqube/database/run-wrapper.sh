#!/bin/bash
ENVCONSUL_CONFIG="/template/envconsul-config.hcl"
CMD="/entrypoint.sh"

# The entrypoint.sh script starts the database 
# then adds itself to the cluster in etcd, so
# need to poll for etcd existence first
if [ -n "$DISCOVERY_SERVICE" ]; then
  echo "--- polling to wait for etcd"
  until $(curl -XGET --fail --output /dev/null --silent --head http://$DISCOVERY_SERVICE/metrics); do
    echo "--trying again"
    echo "curl output"  
    curl -XGET --head http://$DISCOVERY_SERVICE/metrics
    echo ""
    echo ""
    sleep 5
  done
fi


if [[ $VAULT_TOKEN ]]; then
  # poll for vault
   echo "--- polling to wait for vault"
   until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
     echo "--trying again"
     sleep 5
   done
  envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN" $CMD "$@"
else
  $CMD "$@"
fi

# block forever
tail -f /dev/null
