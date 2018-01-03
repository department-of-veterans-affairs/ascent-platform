#!/bin/bash

generate_replica_data() {
  cat <<EOF
{
  "template" : "*",
  "settings" : {"number_of_replicas" : "$REPLICA_AMOUNT" }
}
EOF
}

ES_URL=elastic.internal.vets-api.gov:9200
if [[ $VAULT_TOKEN ]]; then
    if [ "$SECURE_CONNECT" = true ]; then
        consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -vault-token="$VAULT_TOKEN"
        ES_URL="--cacert /usr/share/elasticsearch/config/ca.pem  https://elastic.internal.vets-api.gov:9200"
    fi
fi


echo "password is $ES_PASSWORD"
echo "--- Setting number of replicas..."
curl -XPOST -u elastic:$ES_PASSWORD  $ES_URL/_template/all_index_template -H 'Content-Type: application/json' -d "$(generate_replica_data)"
