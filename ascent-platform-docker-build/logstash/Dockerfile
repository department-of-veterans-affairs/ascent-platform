FROM docker.elastic.co/logstash/logstash:5.5.0

LABEL gov.va.ascent.component="logging"

#To install additional plugins use the following command:
#RUN /usr/share/logstash/bin/logstash-plugin install <plugin name>
#Example: RUN /usr/share/logstash/bin/logstash-plugin install logstash-filter-json

COPY logstash.conf /usr/share/logstash/pipeline/logstash.conf

#Expose the Filebeat input port
EXPOSE 5044