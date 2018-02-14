#!/bin/bash

function isUp {
    curl -s -u admin:admin -f "$BASE_URL/api/system/info"
}

# Wait for server to be up
PING=`isUp`
while [ -z "$PING" ]
do
    sleep 5
    PING=`isUp`
done

# Change password from admin
curl -X POST   http://localhost:9000/api/users/change_password \
     -u admin:admin \
     -H 'cache-control: no-cache' \
     -F login=admin \
     -F password={SONAR_PASSWORD} \
     -F previousPassword=admin
