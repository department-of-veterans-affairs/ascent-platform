#!/bin/bash

generate_pass_data() {
  cat <<EOF
{
  "password" : "$1"
}
EOF
}

echo "---- changing password"
default_pass=changeme

echo "--- Polling to wait for config of elasticsearch to complete"
until $(curl -XGET --output /dev/null --silent --head --fail -u elastic:$default_pass localhost:9200/_cat/health); do
    sleep 5
done

echo "---- done"
curl -XPOST -u elastic:$default_pass 'localhost:9200/_xpack/security/user/elastic/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $ES_PASSWORD)"

echo "---- changing kibana user pass"
curl -XPOST -u elastic:$ES_PASSWORD 'localhost:9200/_xpack/security/user/kibana/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $KIBANA_PASS)"




