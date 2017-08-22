#! /bin/bash

ENVCONSUL_CONFIG="/usr/share/logstash/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/logstash/template/consul-template-config.hcl"
SSL_CONFIG="/usr/share/logstash/template/filebeat.ssl.conf"
NO_SSL_CONFIG="/usr/share/logstash/template/filebeat.conf"
FILEBEAT_CONFIG="/usr/share/logstash/pipeline/filebeat.conf"
CMD="/usr/local/bin/docker-entrypoint"


if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    #Use the SSL config
    cp $SSL_CONFIG $FILEBEAT_CONFIG

    consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" $CMD "$@"
else
    #Use the non SSL config
    cp $NO_SSL_CONFIG $FILEBEAT_CONFIG
    $CMD "$@"
fi
