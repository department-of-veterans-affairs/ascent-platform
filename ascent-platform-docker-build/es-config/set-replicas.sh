#!/bin/bash

generate_replica_data() {
  cat <<EOF
{
  "template" : "*",
  "settings" : {"number_of_replicas" : "$REPLICA_AMOUNT" }
}
EOF
}

ES_URL=`cat es-url`
echo "using url $ES_URL"
echo "--- Setting number of replicas..."
curl -XPOST -s -u elastic:$ES_PASSWORD  $ES_URL/_template/all_index_template -H 'Content-Type: application/json' -d "$(generate_replica_data)"
