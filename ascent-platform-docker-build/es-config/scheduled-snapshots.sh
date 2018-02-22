#!/bin/bash
ES_URL=`cat es-url`
SNAPSHOT_NAME=`date +%Y%m%d-%H%M%S`
# Name of our snapshot repository
REPO=s3_repository
# The amount of snapshots we want to keep.
LIMIT=30

######### Take Snapshot ##########
echo "Taking snapshot: $SNAPSHOT_NAME"
curl -s -XPUT -u elastic:$ES_PASSWORD $ES_URL/_snapshot/$REPO/$SNAPSHOT_NAME?wait_for_completion=true

######### Clean up for old elasticsearch snapshots. ##########

# Get a list of snapshots that we want to delete
SNAPSHOTS=`curl -s -XGET -u elastic:$ES_PASSWORD $ES_URL/_snapshot/$REPO/_all \
  | jq -r ".snapshots[:-${LIMIT}][].snapshot"`

# Loop over the results and delete each snapshot
for SNAPSHOT in $SNAPSHOTS
do
 echo "Deleting snapshot: $SNAPSHOT"
 curl -s -XDELETE -u elastic:$ES_PASSWORD $ES_URL/_snapshot/$REPO/$SNAPSHOT?pretty
done
echo "Done!"