#!/bin/bash
VAULT_SECRETS_FILE=${VAULT_SECRETS_FILE:-"./vault/config/secrets.json"}
tempstr=""
blankspace=" "
  for vaultstr in $(jq -r 'keys[] as $k | "\($k) \(.[$k] | keys[])=\(.[$k] | .[]);"' < "$VAULT_SECRETS_FILE"); do 
         tempstr="${tempstr}${vaultstr}"
         tempstr="${tempstr}${blankspace}"
  done

IFS=';' read -r -a params <<< "$tempstr"
  
  for vaultparam in "${params[@]}"
  do
    vaultparamtrmd="$(echo -e "${vaultparam}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    echo "${vaultparamtrmd}"
    if [ -n "$vaultparamtrmd" ]; then
      docker-compose -f docker-compose.vault.yml -f docker-compose.vault.override.yml exec vault vault write ${vaultparamtrmd}
    fi
  done
