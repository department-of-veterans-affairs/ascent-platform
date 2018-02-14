FROM alpine:3.7

USER root
RUN apk update --no-cache ssl-certs
RUN apk add --no-cache curl
RUN apk add --update bash && rm -r /var/cache/apk/*

# Install unzip for unzipping the vault package
RUN apk add --no-cache  unzip

# Install envconsul for retreiving secrets into ENV variables available only to the executed process
RUN curl -sL -o envconsul_0.7.1_linux_amd64.tgz https://releases.hashicorp.com/envconsul/0.7.1/envconsul_0.7.1_linux_amd64.tgz; \
    tar -xzf envconsul_0.7.1_linux_amd64.tgz; \
    mv envconsul /usr/local/bin/envconsul; \
    chmod +x /usr/local/bin/envconsul; \
    rm -f envconsul_0.7.1_linux_amd64.tgz

COPY poll-services.sh /usr/share/configure/poll-services.sh
COPY run-wrapper.sh /usr/share/configure/run-wrapper.sh
COPY sonar /usr/share/configure/sonar
COPY jenkins /usr/share/configure/jenkins

RUN chmod -R 755 /usr/share/configure
CMD ["/usr/share/configure/run-wrapper.sh"]
