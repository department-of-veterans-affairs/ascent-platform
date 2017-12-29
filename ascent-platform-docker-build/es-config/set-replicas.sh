#!/bin/bash

generate_replica_data() {
  cat <<EOF
{
  "template" : "*",
  "settings" : {"number_of_replicas" : "$REPLICA_AMOUNT" }
}
EOF
}

echo "--- Setting number of replicas..."
curl -XPUT -u elastic:$ES_PASSWORD --cacert /usr/share/elasticsearch/config/ca.pem  'https://elastic.internal.vets-api.gov:9200/_template/all_index_template' -H 'Content-Type: application/json' -d "$(generate_replica_data)"
