FROM docker.elastic.co/beats/filebeat:5.5.0

LABEL gov.va.ascent.component="logging"

# Create a volume for our registry file. The registry file saves Filebeats
# state about what logs have been shipped. Using a volume allows this to presist
# between container restarts.
VOLUME /var/lib/beats

# Need to run the Filebeat agent as root so it can read from the mounted Docker log
# volume.
USER root

WORKDIR /tmp
# Install consul-template to populate secret data into files on disk
RUN curl -L -o consul-template_0.19.0_linux_amd64.tgz https://releases.hashicorp.com/consul-template/0.19.0/consul-template_0.19.0_linux_amd64.tgz; \
    tar -xzf consul-template_0.19.0_linux_amd64.tgz; \
    mv consul-template /usr/local/bin/consul-template; \
    chmod +x /usr/local/bin/consul-template; \
    rm -f consul-template_0.19.0_linux_amd64.tgz

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -L -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

WORKDIR /usr/share/filebeat

# Install our custom configuration
COPY filebeat.yml filebeat.yml
RUN chown root:filebeat filebeat.yml

# Add our envconsul config, consul-template configuration, and templates
COPY envconsul-config.hcl template/envconsul-config.hcl
COPY consul-template-config.hcl template/consul-template-config.hcl
COPY ca.pem.tpl template/ca.pem.tpl
COPY client.pem.tpl template/client.pem.tpl
COPY client.key.tpl template/client.key.tpl
RUN chown -R root:filebeat template

# Concatenate our SSL config with the base config
COPY filebeat.ssl.yml /tmp/filebeat.ssl.yml
RUN cp filebeat.yml filebeat.ssl.yml; \
    cat /tmp/filebeat.ssl.yml >> filebeat.ssl.yml; \
    rm /tmp/filebeat.ssl.yml

# Add our runwrapper
COPY run-wrapper.sh run-wrapper.sh
RUN chown root:filebeat run-wrapper.sh; \
    chmod 755 run-wrapper.sh

CMD ["/usr/share/filebeat/run-wrapper.sh"]