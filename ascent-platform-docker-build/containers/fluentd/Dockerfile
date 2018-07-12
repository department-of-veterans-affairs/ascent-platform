FROM fluent/fluentd:v1.0-debian

LABEL gov.va.ascent.component="logging"

# Default environment variables for runnning locally
ENV ES_PASSWORD=changeme
ENV ES_USER=elastic
ENV ES_HOST=elastic.internal.vets-api.gov
ENV ES_PORT=9200

USER root

#Install ElasticSearch plugin
RUN ["gem", "install", "fluent-plugin-elasticsearch", "--no-rdoc", "--no-ri", "--version", "1.9.5"]

WORKDIR /fluentd/etc

#Add our custom configurations
ADD conf /fluentd/etc

