#!/bin/bash

generate_pass_data() {
  cat <<EOF
{
  "password" : "$1"
}
EOF
}
echo "password is $ES_PASSWORD"
ES_URL=elastic.internal.vets-api.gov:9200
if [[  $VAULT_TOKEN ]]; then
    if [ "$SECURE_CONNECT" = true ]; then
        consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN"
        ES_URL="--cacert /usr/share/elasticsearch/config/ca.pem  https://elastic.internal.vets-api.gov:9200"
    fi
fi
echo "---- changing password"
default_pass=changeme
curl -XPOST -u elastic:$default_pass  $ES_URL/_xpack/security/user/elastic/_password?pretty -H 'Content-Type: application/json' -d "$(generate_pass_data $ES_PASSWORD)"
echo "---- changing kibana user pass"
curl -XPOST -u elastic:$ES_PASSWORD $ES_URL/_xpack/security/user/kibana/_password?pretty -H 'Content-Type: application/json' -d "$(generate_pass_data $KIBANA_PASSWORD)"

