FROM docker.elastic.co/kibana/kibana:5.5.0

LABEL gov.va.ascent.component="logging"

# Change ownership and permissions
USER root

WORKDIR /usr/share/kibana

# Add our envconsul config, consul-template configuration, and templates
COPY template/ template/

COPY run-wrapper.sh run-wrapper.sh

# Change file permissions to elasticsearch user
RUN chown -R kibana:kibana template run-wrapper.sh; \
    chmod 750 run-wrapper.sh;

USER kibana

CMD ["/bin/bash", "run-wrapper.sh"]
