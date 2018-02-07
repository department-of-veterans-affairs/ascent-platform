FROM centos7.4.1708

USER root

WORKDIR /docker
ADD run-wrapper.sh /docker
ADD config /docker/config

RUN chmod -r 755 /docker/*

ENTRYPOINT ['/bin/bash', '/docker/run-wrapper.sh']
