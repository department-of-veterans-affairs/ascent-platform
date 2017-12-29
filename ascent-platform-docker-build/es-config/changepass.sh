#!/bin/bash

generate_pass_data() {
  cat <<EOF
{
  "password" : "$1"
}
EOF
}

ES_URL="--cacert /usr/share/elasticsearch/config/ca.pem https://elastic.internal.vets-api.gov:9200/_xpack/security"
echo "---- changing password"
default_pass=changeme
curl -XPOST -u elastic:$default_pass  $ES_URL/user/elastic/_password?pretty -H 'Content-Type: application/json' -d "$(generate_pass_data $ES_PASSWORD)"
echo "---- changing kibana user pass"
curl -XPOST -u elastic:$ES_PASSWORD $ES_URL/user/kibana/_password?pretty -H 'Content-Type: application/json' -d "$(generate_pass_data $KIBANA_PASSWORD)"

