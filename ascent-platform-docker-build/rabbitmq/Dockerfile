FROM rabbitmq

# Change ownership and permissions
USER root

WORKDIR /tmp

RUN apt-get -qq update -y
RUN apt-get -q install -y curl

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

WORKDIR /etc/rabbitmq

COPY template/ template/
COPY run-wrapper.sh run-wrapper.sh
RUN chmod 755 run-wrapper.sh

CMD ["/bin/bash", "/etc/rabbitmq/run-wrapper.sh"]
EXPOSE 15672
