FROM quay.io/coreos/etcd:latest

COPY run-wrapper.sh /run-wrapper.sh
RUN chmod +x run-wrapper.sh

ENTRYPOINT [ "/bin/sh", "/run-wrapper.sh" ]
