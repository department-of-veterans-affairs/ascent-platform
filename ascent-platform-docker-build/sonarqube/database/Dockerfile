FROM percona/percona-xtradb-cluster:latest

USER root
WORKDIR /

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -sL -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

COPY run-wrapper.sh run-wrapper.sh
COPY template template
RUN chmod 755 /run-wrapper.sh
RUN chmod -R 755 /template

ENTRYPOINT ["/bin/bash", "/run-wrapper.sh"]
