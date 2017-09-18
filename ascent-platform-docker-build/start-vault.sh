# Starts the vault container for Ascent platform
#!/bin/sh

VAULT_CONFIG_DIR=./vault/config
VAULT_TOKEN="${VAULT_TOKEN:-vaultroot}"
export VAULT_TOKEN=$VAULT_TOKEN

#bring up the docker container for vault
docker-compose -f docker-compose.vault.yml -f docker-compose.vault.override.yml up --build -d


sleep 1 # wait for Vault to come up
docker-compose -f docker-compose.vault.yml -f docker-compose.vault.override.yml exec vault vault auth $VAULT_TOKEN
source ./writesecrets.sh
