#! /bin/sh
#bring up the docker container for vault
VAULT_TOKEN="${VAULT_TOKEN:-vaultroot}"
export VAULT_TOKEN=$VAULT_TOKEN
echo "Inside run-wrapper.sh"
vault server \
        -dev-root-token-id="${VAULT_TOKEN}" \
        -dev-listen-address="${VAULT_ADDR:-"http:/vault:8200"}" \
        -dev "$@" &

sleep 1 # wait for Vault to come up

vault auth $VAULT_TOKEN
source /usr/share/vault/writesecrets.sh

