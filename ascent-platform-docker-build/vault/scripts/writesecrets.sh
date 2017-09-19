#!/bin/bash
VAULT_SECRETS_FILE=${VAULT_SECRETS_FILE:-"./vault/scripts/secrets.json"}

#VAULT_SECRETS_FILE=${VAULT_SECRETS_FILE:-"/usr/share/vault/secrets.json"}
temptr=""
blankspace=" "
blankwithnospace=""
source ./vault/scripts/installjq.sh 
#source /usr/share/vault/installjq.sh
#  for vaultstr in $(jq -r 'keys[] as $k | "\($k) \(.[$k] | keys[])=\(.[$k] | .[]);"' < "$VAULT_SECRETS_FILE"); do
 for vaultstr in $(jq -r 'keys[] as $k | "\($k)- \(.[$k] );"' < "$VAULT_SECRETS_FILE"); do
         tempstr="${tempstr}${vaultstr}"
         tempstr="${tempstr}${blankspace}"
  done

IFS=';' read -r -a params <<< "$tempstr"
  
  for vaultparam in "${params[@]}"
  do
#    vaultparamtrmd="$(echo -e "${vaultparam}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
#    echo "${vaultparamtrmd}"
    vaultparamtrmd="$(echo -e "${vaultparam}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"
    vaultfinal="$(echo -e "${vaultparamtrmd}" | sed -e 's/{/'${blankwithnospace}'/g' -e 's/}/'${blankwithnospace}'/g' -e 's/\"/'${blankwithnospace}'/g' -e 's/,/ /g' -e 's/-/'${blankwithnospace}'/g' -e 's/:/=/g' )"
    echo "${vaultfinal}"
    if [ -n "$vaultfinal" ]; then
      docker-compose -f docker-compose.vault.yml -f docker-compose.vault.override.yml exec vault vault write ${vaultfinal}
    fi
  done
