FROM centos:7

LABEL gov.va.ascent.component="logging"
# Change ownership and permissions
USER root

ENV KIBANA_PASSWORD changeme
ENV ES_PASSWORD changeme
ENV REPLICA_AMOUNT 3
ENV ZIPKIN_STORAGE_ELASTICSEARCH_USERNAME zipkin
ENV ZIPKIN_STORAGE_ELASTICSEARCH_PASSWORD changeme

WORKDIR /tmp
# Install consul-template to populate secret data into files on disk
RUN curl -sL -o consul-template_0.19.0_linux_amd64.tgz https://releases.hashicorp.com/consul-template/0.19.0/consul-template_0.19.0_linux_amd64.tgz; \
    tar -xzf consul-template_0.19.0_linux_amd64.tgz; \
    mv consul-template /usr/local/bin/consul-template; \
    chmod +x /usr/local/bin/consul-template; \
    rm -f consul-template_0.19.0_linux_amd64.tgz

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -sL -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

# jq command line JSON parser
RUN curl -sL -o /usr/bin/jq https://github.com/stedolan/jq/releases/download/jq-1.5/jq-linux64; \
    chmod 755 /usr/bin/jq

# Install the Elasticsearch curator tool which will be used to manage snapshots and data retention
RUN rpm --import https://packages.elastic.co/GPG-KEY-elasticsearch
COPY curator/curator.repo /etc/yum.repos.d/curator.repo
RUN yum install -y elasticsearch-curator
# Install the curator configuration files
COPY curator/ /docker/curator/

WORKDIR /usr/share/elasticsearch

# Add our envconsul config, consul-template configuration, and templates
COPY template/ template/

COPY run-wrapper.sh run-wrapper.sh
COPY set-replicas.sh /docker/set-replicas.sh
COPY changepass.sh /docker/changepass.sh
COPY set-url.sh /docker/set-url.sh
COPY configure-snapshots.sh /docker/configure-snapshots.sh
COPY scheduled-snapshots.sh /docker/scheduled-snapshots.sh
COPY restore-snapshot.sh /docker/restore-snapshot.sh

# Change file permissions to elasticsearch user
RUN chmod 755 /docker/*;
RUN chown -R root:root template run-wrapper.sh  /docker; \
    chmod 750 run-wrapper.sh; chmod -R 750 /docker;

CMD ["/bin/bash", "run-wrapper.sh"]
