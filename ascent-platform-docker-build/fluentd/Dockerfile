FROM fluent/fluentd:v0.12-debian

LABEL gov.va.ascent.component="logging"

ENV ES_PASSWORD=changeme

USER root

#Install ElasticSearch plugin
RUN ["gem", "install", "--silent", "fluent-plugin-elasticsearch", "--no-rdoc", "--no-ri", "--version", "1.9.5"]

# Install curl to install envconsul and consul
RUN apt-get -q update -y
RUN apt-get -q install -y curl

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

#Add our custom configurations
ADD conf /fluentd/etc

WORKDIR /fluentd/etc
COPY template/ template/
COPY run-wrapper.sh run-wrapper.sh

RUN chmod 750 run-wrapper.sh

CMD ["/bin/bash", "run-wrapper.sh"]
