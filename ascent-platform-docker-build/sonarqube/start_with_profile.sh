#!/bin/bash
#

# Install Github plugin

./provision/install_github_plugin.sh

# Start Sonar

./bin/run.sh &

# Change admin password
./provision/set_new_password.sh

#function curlAdmin {
#    curl -v -u admin:admin $@
#}
#
#function isUp {
#    curl -s -u admin:admin -f "$BASE_URL/api/system/info"
#}
#
## Wait for server to be up
#PING=`isUp`
#while [ -z "$PING" ]
#do
#    sleep 5
#    PING=`isUp`
#done
#
## Restore qualityprofile and exclude Spring Boot Main Application.java class
#if [ "$LANGUAGE" ] && [ "$PROFILE_NAME" ]; then
#    curlAdmin -F "backup=@/qualityprofile/java-ascent-32413.xml" -X POST "$BASE_URL/api/qualityprofiles/restore"
#    curlAdmin -X POST "$BASE_URL/api/qualityprofiles/set_default?language=$LANGUAGE&profileName=$PROFILE_NAME"
#    curlAdmin -X POST "$BASE_URL/api/settings/set?key=sonar.coverage.exclusions&values=**/api/v*/transfer/**/*,**/*Application.java"
#fi
#
## Set a jenkins webhook
#./provision/set_jenkins_webhook.sh
#
## Create a token for admin user to authenticate to jenkins
#./provision/set_token.sh
#
wait
