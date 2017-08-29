#! /bin/bash

ENVCONSUL_CONFIG="/usr/share/logstash/template/envconsul-config.hcl"
CONSUL_TEMPLATE_CONFIG="/usr/share/logstash/template/consul-template-config.hcl"
FILEBEAT_SSL="/usr/share/logstash/template/filebeat.ssl.conf"
FILEBEAT="/usr/share/logstash/template/filebeat.conf"
FILEBEAT_CONFIG="/usr/share/logstash/pipeline/filebeat.conf"
ES_SSL="/usr/share/logstash/template/elasticsearch.ssl.conf"
ES="/usr/share/logstash/template/elasticsearch.conf"
ES_CONFIG="/usr/share/logstash/pipeline/elasticsearch.conf"
CMD="/usr/local/bin/docker-entrypoint"


if [[ -z $VAULT_ADDR ]]; then
    VAULT_ADDR="http://vault:8200"
fi

# If ENVCONSUL_CONFIG is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN ]]; then
    #Use the SSL config
    cp $FILEBEAT_SSL $FILEBEAT_CONFIG
    cp $ES_SSL $ES_CONFIG

    consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"

    #Create PKCS12 keystore for mutual auth
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" openssl pkcs12 -export -in server.pem -inkey server.key -out keystore.p12 -passout env:LS_KEYSTORE_PASSWORD -passin env:LS_PRIVATEKEY_PASSWORD

    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr="$VAULT_ADDR" $CMD "$@"
else
    #Use the non SSL config
    cp $FILEBEAT $FILEBEAT_CONFIG
    cp $ES $ES_CONFIG
    $CMD "$@"
fi
