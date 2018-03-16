#! /bin/bash

CMD="java -Xms64m -Xmx256m -jar $JAR_FILE"

export INSTANCE_HOST_NAME=$(hostname)

if [[ $VAULT_TOKEN_FILE ]]; then
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

# If VAULT_TOKEN is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN && $VAULT_ADDR ]]; then
    #Install the Vault CA certificate
    mkdir /usr/local/share/ca-certificates/ascent
    echo "Downloading Vault CA certificate from $VAULT_ADDR/v1/pki/ca/pem"
    curl -L -s --insecure $VAULT_ADDR/v1/pki/ca/pem > /usr/local/share/ca-certificates/ascent/vault-ca.crt
    echo 'Update CAs'
    update-ca-certificates
    keytool -importcert -alias vault -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit -file /usr/local/share/ca-certificates/ascent/vault-ca.crt
    

    #Launch the app in another shell to keep secrets secure
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr=$VAULT_ADDR $CMD
else
    $CMD
fi