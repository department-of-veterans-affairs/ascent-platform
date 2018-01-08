#!/bin/bash
CONSUL_TEMPLATE_CONFIG="/usr/share/elasticsearch/template/consul-template-config.hcl"
if [[ -z $SECURE_CONNECT ]]; then
    SECURE_CONNECT=false
    echo "elastic.internal.vets-api.gov:9200" >> es-url
fi

if [ "$SECURE_CONNECT" = true ]; then
    consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN"
    echo "--cacert /usr/share/elasticsearch/config/ca.pem  https://elastic.internal.vets-api.gov:9200" >> es-url
fi

