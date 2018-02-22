FROM jenkins/jenkins:lts-alpine

USER root
RUN apk update --no-cache ssl-certs
RUN apk add --no-cache curl

# Install consul-template to populate secret data into files on disk
RUN curl -sL -o consul-template_0.19.0_linux_amd64.tgz https://releases.hashicorp.com/consul-template/0.19.0/consul-template_0.19.0_linux_amd64.tgz; \
    tar -xzf consul-template_0.19.0_linux_amd64.tgz; \
    mv consul-template /usr/local/bin/consul-template; \
    chmod +x /usr/local/bin/consul-template; \
    rm -f consul-template_0.19.0_linux_amd64.tgz

COPY template /usr/share/jenkins/template
RUN chmod 755 -R /usr/share/jenkins/template
COPY config/populate-groovy-script.sh /usr/share/jenkins/populate-groovy-script.sh
RUN chmod 755 /usr/share/jenkins/populate-groovy-script.sh
RUN chown jenkins:jenkins /usr/share/jenkins/populate-groovy-script.sh

RUN awk 'NR==1{print; print "/usr/share/jenkins/populate-groovy-script.sh"} NR!=1' /usr/local/bin/jenkins.sh >> /usr/local/bin/jenkins.sh.tmp
RUN mv /usr/local/bin/jenkins.sh.tmp /usr/local/bin/jenkins.sh
RUN chown jenkins:jenkins /usr/local/bin/jenkins.sh
RUN chmod 755 /usr/local/bin/jenkins.sh

USER jenkins
COPY config/config.xml /usr/share/jenkins/ref/config.xml.override
COPY config/github-plugin-configuration.xml /usr/share/jenkins/ref/github-plugin-configuration.xml.override
COPY config/org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig.xml /usr/share/jenkins/ref/org.jenkinsci.plugins.pipeline.modeldefinition.config.GlobalConfig.xml.override
COPY config/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
