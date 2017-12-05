#!/bin/bash
echo "--- Sleeping to wait for config of elasticsearch to complete"
sleep 2m
echo "--- setting number of replicas..."

curl -XPUT -u elastic:changeme 'localhost:9200/_template/all_index_template' -H 'Content-Type: application/json' -d ' 
{ 
  "template" : "*", 
  "settings" : {"number_of_replicas" : $REPLICA_AMOUNT }
} '
