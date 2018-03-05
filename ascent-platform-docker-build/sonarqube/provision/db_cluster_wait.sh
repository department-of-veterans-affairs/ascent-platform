#!/bin/bash

# Check for the existence of $DISCOVERY_SERVICE and 
#    skip to just polling db (set ip_list to 'db')if not found
if [ -z "$DISCOVERY_SERVICE" ]; then
  ip_list="db"
  INITIAL_CLUSTER_SIZE=1
else
  echo "--- polling for existence of cluster $CLUSTER_NAME in discovery service $DISCOVERY_SERVICE"
  until $(curl -XGET --fail --output /dev/null --silent --head http://$DISCOVERY_SERVICE/v2/keys/pxc-cluster/$CLUSTER_NAME/); do
    echo "--trying again"
    echo "curl output"  
    curl -XGET --head http://$DISCOVERY_SERVICE/v2/keys/pxc-cluster/$CLUSTER_NAME
    echo ""
    echo ""
    sleep 5
  done
  echo "--- cluster initialized. checking for nodes"
  ip_list=`curl http://$DISCOVERY_SERVICE/v2/keys/pxc-cluster/$CLUSTER_NAME/ | tac | tac | jq -r '.node.nodes[]?.key' | awk -F'/' '{print $(NF)}'`
fi

keepgoing=true
while [ "$keepgoing" = "true" ]; do
   if [ -z "$ip_list" ]; then
      echo "did not find nodes in cluster. trying again"
      sleep 5
   else
      echo "ip_list=$ip_list"
      node_amount=`echo "$ip_list" | wc -l`
      echo "node_amount=$node_amount"
      if [[ "$node_amount" -lt "$INITIAL_CLUSTER_SIZE" ]]; then
         echo "node amount is less than $INITIAL_CLUSTER_SIZE. Trying again"
         sleep 5
      else 
         for i in $ip_list
         do
            echo "POLLING $i"
            python ./provision/pollsql.py $i
         done
         keepgoing=false
         echo "keegoing=$keepgoing"
      fi
   fi
done
