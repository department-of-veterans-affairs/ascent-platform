#!/bin/bash


# Download jar file
curl -L -o github-plugin.jar "https://sonarsource.bintray.com/Distribution/sonar-github-plugin/sonar-github-plugin-1.4.2.1027.jar"

# Move to plugins dir
mv github-plugin.jar $SONARQUBE_HOME/extensions/plugins


wait
