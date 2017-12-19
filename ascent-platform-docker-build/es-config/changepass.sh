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
curl -XPOST -s -u elastic:$default_pass 'elasticsearch:9200/_xpack/security/user/elastic/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $ES_PASSWORD)"
echo "---- changing kibana user pass"
curl -XPOST -s -u elastic:$ES_PASSWORD 'elasticsearch:9200/_xpack/security/user/kibana/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $KIBANA_PASSWORD)"

