#!/bin/sh -x

set -e

apk add --no-cache jq || \
   (sed -i -e 's/dl-cdn/dl-4/g' /etc/apk/repositories && apk add --no-cache jq)
    
mkdir -p /opt/
mv /tmp/run.sh /opt/
mv /tmp/config/secrets.json /opt/
chmod a+x /opt/run.sh

rm -rf /var/cache/apk/*