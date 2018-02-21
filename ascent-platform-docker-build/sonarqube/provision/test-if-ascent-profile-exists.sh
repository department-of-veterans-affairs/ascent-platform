#!/bin/bash

# Test for existence of ascent profile in sonarqube
# echo TRUE if it exists, echo FALSE if it doesn't

test=`curl -u admin:$SONAR_PASSWORD $SONAR_URL/api/qualityprofiles/search? | grep "ASCENT"`

if [ "$test" ]; then
 echo "TRUE"
else
 echo "FALSE"
fi

