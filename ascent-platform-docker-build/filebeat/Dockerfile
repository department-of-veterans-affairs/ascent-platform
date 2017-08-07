FROM docker.elastic.co/beats/filebeat:5.5.0

LABEL gov.va.ascent.component="logging"

# Install our custome configuration
COPY filebeat.yml /usr/share/filebeat/filebeat.yml

# Create a volume for our registry file. The registry file saves Filebeats
# state about what logs have been shipped. Using a volume allows this to presist
# between container restarts.
VOLUME /var/lib/beats

# Need to run the Filebeat agent as root so it can read from the mounted Docker log
# volume.
USER root