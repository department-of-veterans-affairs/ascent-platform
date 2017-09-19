FROM alpine:3.6 
ENV VAULT_ADDR="http://vault:8200"
ENV VAULT_TOKEN=vaultroot
ENV SKIP_SETCAP=1
USER root
WORKDIR /usr/share/vault
COPY ./scripts/writesecrets.sh writesecrets.sh
COPY ./scripts/installjq.sh installjq.sh
COPY ./scripts/secrets.json secrets.json
COPY ./scripts/run-wrapper.sh run-wrapper.sh

RUN chmod 755 writesecrets.sh
RUN chmod 755 installjq.sh
RUN chmod 755 run-wrapper.sh

ENTRYPOINT ["/usr/share/vault/run-wrapper.sh"]
CMD ["server", "-dev"]
