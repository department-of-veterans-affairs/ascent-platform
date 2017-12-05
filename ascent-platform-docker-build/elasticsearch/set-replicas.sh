#!/bin/bash

generate_replica_data() {
  cat <<EOF
{
  "template" : "*",
  "settings" : {"number_of_replicas" : "$REPLICA_AMOUNT" }
}
EOF
}


generate_newpass_data() {
  cat << EOF
{
  "password" : "$ES_PASSWORD"
}
EOF
}




echo "--- Sleeping to wait for config of elasticsearch to complete"
sleep 2m
echo "--- changing default password and setting number of replicas..."

curl -XPUT -u elastic:changeme 'localhost:9200/_xpack/security/user/elastic/_password' -H "Content-Type: application/json" -d "$(generate_newpass_data)"



curl -XPUT -u elastic:$ES_PASSWORD 'localhost:9200/_template/all_index_template' -H 'Content-Type: application/json' -d "$(generate_replica_data)"

