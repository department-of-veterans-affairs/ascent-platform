#!/bin/bash

generate_replica_data() {
  cat <<EOF
{
  "index_patterns" : "*",
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




echo "--- Polling to wait for config of elasticsearch to complete"
until $(curl -XGET --output /dev/null --silent --head --fail -u elastic:$ES_PASSWORD localhost:9200/_cat/health); do
    sleep 5
done

curl -XGET -u elastic:$ES_PASSWORD 'localhost:9200/_cluster/health?wait_for_status=green'



echo "--- Setting number of replicas..."
curl -XPUT -u elastic:$ES_PASSWORD 'localhost:9200/_template/all_index_template' -H 'Content-Type: application/json' -d "$(generate_replica_data)"
