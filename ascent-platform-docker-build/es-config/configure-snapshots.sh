#!/bin/bash

snapshot_repository_s3() {
  cat <<EOF
{
  "type": "s3",
  "settings": {
    "bucket": "${SNAPSHOT_BUCKET_NAME}",
    "base_path": "elasticsearch/5.5"
  }
}
EOF
}

ES_URL=`cat es-url`
echo "using url $ES_URL"
echo "--- Creating snapshot repository..."
curl -XPUT -s -u elastic:$ES_PASSWORD  $ES_URL/_snapshot/s3_repository -H 'Content-Type: application/json' -d "$(snapshot_repository_s3)"
