#!/bin/bash 

# poll to wait until elasticsearch is up successfully with the new password
echo "--- Polling to wait for config of kibana password to complete"
until $(curl -XGET --output /dev/null --silent --head --fail -u kibana:$ELASTICSEARCH_PASSWORD elasticsearch:9200/_cat/health); do
    sleep 5
done

echo "---- kibana waiting for elasticsearch cluster state to be green"
curl -XGET -u kibana:$ELASTICSEARCH_PASSWORD 'elasticsearch:9200/_cluster/health?wait_for_status=green'


echo "---- kibana: cluster is green"
