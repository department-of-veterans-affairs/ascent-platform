FROM docker.elastic.co/kibana/kibana:5.5.0

LABEL gov.va.ascent.component="logging"

# Change ownership and permissions
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

WORKDIR /usr/share/kibana

# Add our envconsul config, consul-template configuration, and templates
COPY template/ template/

COPY run-wrapper.sh run-wrapper.sh
COPY wait-for-es-health.sh /docker/wait-for-es-health.sh


# Change file permissions to elasticsearch user
RUN chown -R kibana:kibana template config run-wrapper.sh /docker; \
    chmod 750 run-wrapper.sh; chmod -R 750 /docker;

USER kibana

ENV ELASTICSEARCH_PASSWORD kibana123

CMD ["/bin/bash", "run-wrapper.sh"]
