#!/bin/bash

# Install AWS authentication information for S3
/usr/share/elasticsearch/bin/elasticsearch-keystore create
if [[ -z $AWS_ACCESS_ID ]]; then
    echo "Adding aws access key to keystore..."
    echo $AWS_ACCESS_ID | /usr/share/elasticsearch/bin/elasticsearch-keystore add --stdin s3.client.default.access_key
fi

if [[ -z $AWS_SECRET_KEY ]]; then
    echo "Adding aws secret key to keystore..."
    echo $AWS_SECRET_KEY | /usr/share/elasticsearch/bin/elasticsearch-keystore add --stdin s3.client.default.secret_key
fi