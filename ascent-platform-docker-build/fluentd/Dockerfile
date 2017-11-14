FROM fluent/fluentd:v0.12-debian

LABEL gov.va.ascent.component="logging"

#Install ElasticSearch plugin
RUN ["gem", "install", "fluent-plugin-elasticsearch", "--no-rdoc", "--no-ri", "--version", "1.9.2"]

#Add our custom configurations
ADD conf /fluentd/etc