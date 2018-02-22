#!/bin/bash
#
# Restore a snapshot from our repository


ES_URL=`cat es-url`
SNAPSHOT=$1

if [[ -z SNAPSHOT ]]; then
    # We need to close the indices first
    curl -XPOST $ES_URL/_all/_close

    # Restore the snapshot we want
    curl -XPOST $ES_URL/_snapshot/my_backup/$SNAPSHOT/_restore -d '{
    "ignore_unavailable": true,
    }'

    # Restore process automatically re-opens restored indices
    # Re-open the indices
    #curl -XPOST $ES_URL/_all/_open
else
    echo 'No snapshot ID specified, you must specify one of the following snapshot IDs:'
    curl -XGET $ES_URL/_snapshot/s3_repository/_all?verbose=false
fi