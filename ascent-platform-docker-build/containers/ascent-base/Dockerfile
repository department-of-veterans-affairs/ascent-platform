FROM java:8

ENV ENVCONSUL_CONFIG "/envconsul-config.hcl"
ENV CONSUL_TEMPLATE_CONFIG "/consul-template-config.hcl"

# Install consul-template to populate secret data into files on disk
RUN curl -s -L -o consul-template_0.19.0_linux_amd64.tgz https://releases.hashicorp.com/consul-template/0.19.0/consul-template_0.19.0_linux_amd64.tgz; \
    tar -xzf consul-template_0.19.0_linux_amd64.tgz; \
    mv consul-template /usr/local/bin/consul-template; \
    chmod +x /usr/local/bin/consul-template; \
    rm -f consul-template_0.19.0_linux_amd64.tgz

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -s -L -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

# jq command line JSON parser
RUN curl -s -L -o /usr/bin/jq https://github.com/stedolan/jq/releases/download/jq-1.5/jq-linux64; \
    chmod 755 /usr/bin/jq

COPY envconsul-config.hcl $ENVCONSUL_CONFIG
COPY consul-template-config.hcl $CONSUL_TEMPLATE_CONFIG
COPY run-wrapper.sh /run-wrapper.sh

ENTRYPOINT /run-wrapper.sh
