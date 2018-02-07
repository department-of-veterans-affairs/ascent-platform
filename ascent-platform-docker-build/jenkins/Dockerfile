FROM jenkins/jenkins:lts-alpine

USER root

RUN apk update --no-cache ssl-certs

RUN apk add --no-cache curl

COPY config/config.xml /usr/share/jenkins/ref/config.xml.override
COPY config/github-plugin-configuration.xml /usr/share/jenkins/ref/github-plugin-configuration.xml.override
COPY config/org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig.xml /usr/share/jenkins/ref/org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig.xml.override

COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
