#!/bin/bash

generate_pass_data() {
  cat <<EOF
{
  "password" : "$1"
}
EOF
}

generate_zipkin_role_data() {
  cat <<EOF
{
  "cluster": [],
  "indices": [
    {
      "names": [ "zipkin*" ],
      "privileges": ["all"]
    }
  ]
}
EOF
}

generate_developer_role_data() {
  cat <<EOF
{
  "cluster": [],
  "indices": [
    {
      "names": [ "applogs-*", "auditlogs-*", ".kibana" ],
      "privileges": ["view_index_metadata", "read", "monitor"]
    }
  ]
}
EOF
}

generate_user_data() {
  cat <<EOF
{
  "password" : "$1",
  "roles" : [ "zipkin" ]
}
EOF
}

echo "---- changing password"
default_pass=changeme
curl -XPOST -sk -u elastic:$default_pass 'elasticsearch:9200/_xpack/security/user/elastic/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $ES_PASSWORD)"
echo "---- changing kibana user pass"
curl -XPOST -sk -u elastic:$ES_PASSWORD 'elasticsearch:9200/_xpack/security/user/kibana/_password?pretty' -H 'Content-Type: application/json' -d "$(generate_pass_data $KIBANA_PASSWORD)"

# Create role for Zipkin application
curl -XPOST -sk -u elastic:$ES_PASSWORD 'elasticsearch:9200/_xpack/security/role/zipkin' -H 'Content-Type: application/json' -d "$(generate_zipkin_role_data)"

# Create developer role for read-only access to application logs
curl -XPOST -sk -u elastic:$ES_PASSWORD 'elasticsearch:9200/_xpack/security/role/developer' -H 'Content-Type: application/json' -d "$(generate_developer_role_data)"

# ZIPKIN_STORAGE_ELASTIC_SEARCH_* environment variables are populated by envconsul from secrets in Vault
echo "---- Create Zipkin user account"
curl -XPOST -sk -u elastic:$ES_PASSWORD "elasticsearch:9200/_xpack/security/user/$ZIPKIN_STORAGE_ELASTIC_SEARCH_USERNAME" -H 'Content-Type: application/json' -d "$(generate_user_data $ZIPKIN_STORAGE_ELASTIC_SEARCH_PASSWORD)"

