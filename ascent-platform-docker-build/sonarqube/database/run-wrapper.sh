#!/bin/bash
ENVCONSUL_CONFIG="/template/envconsul-config.hcl"
CMD="/entrypoint.sh"


echo "--- polling to wait for etcd"
until $(curl -XGET --fail --output /dev/null --silent --head http://etcd:2379/metrics); do
  echo "--trying again"
  echo "curl output"  
  curl -XGET --head http://etcd:2379/metrics
  echo ""
  echo ""
  sleep 5
done


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


tail -f /dev/null
