#!/bin/bash
function curlAdmin {
    curl -v -u admin:admin $@
}

SONAR_URL="http://localhost:9000"

# Restore qualityprofile and exclude Spring Boot Main Application.java class
if [ "$LANGUAGE" ] && [ "$PROFILE_NAME" ]; then
    curlAdmin -F "backup=@/opt/sonarqube/provision/qualityprofile/blue-java.xml" -X POST "$SONAR_URL/api/qualityprofiles/restore"
    curlAdmin -X POST "$SONAR_URL/api/qualityprofiles/set_default?language=$LANGUAGE&profileName=$PROFILE_NAME"
    curlAdmin -X POST "$SONAR_URL/api/settings/set?key=sonar.coverage.exclusions&values=**/api/v*/transfer/**/*,**/*Application.java,**/*AbstractRemoteServiceCallMock.java"

    curlAdmin -F "backup=@/opt/sonarqube/provision/qualityprofile/blue-xml.xml" -X POST "$SONAR_URL/api/qualityprofiles/restore"
    curlAdmin -X POST "$SONAR_URL/api/qualityprofiles/set_default?language=xml&profileName=$PROFILE_NAME"
fi

