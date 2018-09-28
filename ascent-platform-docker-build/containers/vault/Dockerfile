FROM vault:0.9.0

ADD . /tmp/

RUN /tmp/setup.sh


RUN apk add --no-cache curl

ENTRYPOINT ["/opt/run.sh"]
CMD ["server", "-dev"]
HEALTHCHECK --interval=5s --timeout=2s \
  CMD [[ -f /opt/healthcheck ]]
