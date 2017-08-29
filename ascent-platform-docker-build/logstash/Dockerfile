FROM docker.elastic.co/logstash/logstash:5.5.0

LABEL gov.va.ascent.component="logging"

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

# Install OpenSSL
RUN yum install -y openssl 

WORKDIR /usr/share/logstash

#To install additional plugins use the following command:
#RUN /usr/share/logstash/bin/logstash-plugin install <plugin name>
#Example: RUN /usr/share/logstash/bin/logstash-plugin install logstash-filter-json

COPY logstash.conf pipeline/logstash.conf

# Add our runwrapper
COPY run-wrapper.sh run-wrapper.sh

#Expose the Filebeat input port
EXPOSE 5044

# Add our envconsul config, consul-template configuration, and templates
COPY template/ template/

# Change file permissions to logstash user
RUN chown -R logstash:logstash template pipeline/logstash.conf run-wrapper.sh; \
    chmod 750 run-wrapper.sh

USER logstash

ENTRYPOINT ["/usr/share/logstash/run-wrapper.sh"]
