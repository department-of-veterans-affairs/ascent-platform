#! /bin/bash
set -e

CLIENT_KEYSTORE="$JAVA_HOME/jre/lib/security/client.jks"
CLIENT_KEYSTORE_PASS=$(openssl rand -base64 14)
SERVER_KEYSTORE="$JAVA_HOME/jre/lib/security/server.jks"
SERVER_KEYSTORE_PASS=$(openssl rand -base64 14)

if [[ -z $JAVA_OPTS ]]; then
    JAVA_OPTS="-Xms128m -Xmx512m"
fi

if [[ -z $CMD ]]; then
    CMD="java $JAVA_OPTS -jar $JAR_FILE"
fi

if [[ -z $APP_NAME ]]; then
    APP_NAME=`echo ${JAR_FILE/%.jar/} | sed 's/\///'`
fi

export INSTANCE_HOST_NAME=$(hostname)

if [[ $VAULT_TOKEN_FILE ]]; then
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

# If VAULT_TOKEN is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN && $VAULT_ADDR ]]; then

    if curl -L -s --fail --insecure $VAULT_ADDR/v1/pki/ca/pem > /dev/null 2>&1; then
        #Install the Vault CA certificate
        mkdir /usr/local/share/ca-certificates/ascent
        echo "Downloading Vault CA certificate from $VAULT_ADDR/v1/pki/ca/pem"
        curl -L -s --insecure $VAULT_ADDR/v1/pki/ca/pem > /usr/local/share/ca-certificates/ascent/vault-ca.crt
        echo 'Updating CAs...'
        update-ca-certificates
        keytool -importcert -alias vault -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit -file /usr/local/share/ca-certificates/ascent/vault-ca.crt

        #Request a certificate for our application
        echo "Requesting server certificate from Vault..."
        #Creating template files to be populated by consul-template
        cat > '/tmp/app.crt.tpl' <<EOF
{{ with secret "pki/issue/vetservices" "common_name=$APP_NAME.internal.vetservices.gov" "alt_names=$INSTANCE_HOST_NAME" }}
{{ .Data.certificate }}{{ end }}
EOF
        cat > '/tmp/app.key.tpl' <<EOF
{{ with secret "pki/issue/vetservices" "common_name=$APP_NAME.internal.vetservices.gov" "alt_names=$INSTANCE_HOST_NAME" }}
{{ .Data.private_key }}{{ end }}
EOF
        consul-template -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"
        echo "$SERVER_KEYSTORE_PASS" | openssl pkcs12 -export -out $TMPDIR/app.p12 -inkey $TMPDIR/app.key -in $TMPDIR/app.crt -password stdin -name $APP_NAME
        echo "Creating server keystore for $APP_NAME..."
        keytool -genkey -alias app -keystore $SERVER_KEYSTORE -storepass $SERVER_KEYSTORE_PASS -dname "CN=app.vetservices.gov, OU=OIT, O=VA, L=App, S=VA, C=US" -noprompt -keypass $SERVER_KEYSTORE_PASS
        keytool -delete -alias app -keystore $SERVER_KEYSTORE -storepass $SERVER_KEYSTORE_PASS
        keytool -importkeystore -srckeystore $TMPDIR/app.p12 -srcstoretype PKCS12 -destkeystore $SERVER_KEYSTORE -deststoretype JKS -deststorepass $SERVER_KEYSTORE_PASS -srcstorepass $SERVER_KEYSTORE_PASS -alias $APP_NAME -destalias $APP_NAME
    fi
    
    #Build the trusted keystore
    if curl -L -s --insecure -X LIST -H "X-Vault-Token: $VAULT_TOKEN" --fail $VAULT_ADDR/v1/secret/ssl/trusted > /dev/null 2>&1; then
        CA_CERTS=$(curl -L -s --insecure -X LIST -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/ssl/trusted | jq -r '.data.keys[]')
        for cert in $CA_CERTS; do
            echo "Loading trusted certificate for $cert"
            curl -L -s --insecure -X GET -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/ssl/trusted/$cert | jq -r '.data.certificate' > $TMPDIR/$cert.crt
            keytool -importcert -alias $cert -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit -file $TMPDIR/$cert.crt
        done
    else
        echo 'No trusted certificates to load.'
    fi

    #Build the client keystore
    echo "Creating client keystore for $APP_NAME..."
    keytool -genkey -alias app -keystore $CLIENT_KEYSTORE -storepass $CLIENT_KEYSTORE_PASS -dname "CN=app.vetservices.gov, OU=OIT, O=VA, L=App, S=VA, C=US" -noprompt -keypass $CLIENT_KEYSTORE_PASS
    keytool -delete -alias app -keystore $CLIENT_KEYSTORE -storepass $CLIENT_KEYSTORE_PASS

    #Check to see if there are any client certificates for this app
    if curl -L -s --insecure -X LIST -H "X-Vault-Token: $VAULT_TOKEN" --fail $VAULT_ADDR/v1/secret/ssl/client/$APP_NAME > /dev/null 2>&1; then
        CLIENT_CERTS=$(curl -L -s --insecure -X LIST -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/ssl/client/$APP_NAME | jq -r '.data.keys[]')
        for cert in $CLIENT_CERTS; do
            echo "Loading certificate for $cert"
            curl -L -s --insecure -X GET -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/ssl/client/$APP_NAME/$cert | jq -r '.data.certificate' > $TMPDIR/$APP_NAME-$cert.crt
            curl -L -s --insecure -X GET -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/secret/ssl/client/$APP_NAME/$cert | jq -r '.data.private_key' > $TMPDIR/$APP_NAME-$cert.key
            
            echo "$CLIENT_KEYSTORE_PASS" | openssl pkcs12 -export -out $TMPDIR/$APP_NAME-$cert.p12 -inkey $TMPDIR/$APP_NAME-$cert.key -in $TMPDIR/$APP_NAME-$cert.crt -password stdin -name $cert
            keytool -importkeystore -srckeystore $TMPDIR/$APP_NAME-$cert.p12 -srcstoretype PKCS12 -destkeystore $CLIENT_KEYSTORE -deststoretype JKS -deststorepass $CLIENT_KEYSTORE_PASS -srcstorepass $CLIENT_KEYSTORE_PASS -alias $cert -destalias $cert
        done
    else
        echo 'No client certificates to load.'
    fi

    #Launch the app in another shell to keep secrets secure
    CMD="$CMD -Dpartner.client.keystore=$CLIENT_KEYSTORE -Dpartner.client.keystorePassword=$CLIENT_KEYSTORE_PASS -Dplatform.server.keystore=$SERVER_KEYSTORE -Dplatform.server.keystorePassword=$SERVER_KEYSTORE_PASS"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr=$VAULT_ADDR $CMD
else
    $CMD
fi