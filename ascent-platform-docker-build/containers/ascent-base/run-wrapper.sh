#! /bin/bash
set -e

JKS_DIR='/app/certs'
CLIENT_TRUSTSTORE="$JAVA_HOME/jre/lib/security/cacerts"
CLIENT_TRUSTSTORE_PASS='changeit'
CLIENT_KEYSTORE="$JKS_DIR/client.jks"
CLIENT_KEYSTORE_PASS=$(openssl rand -base64 14)
SERVER_TRUSTSTORE="$JKS_DIR/server-truststore.jks"
SERVER_TRUSTSTORE_PASS=$(openssl rand -base64 14)
SERVER_KEYSTORE="$JKS_DIR/server.jks"
SERVER_KEYSTORE_PASS=$(openssl rand -base64 14)
TMPDIR=/tmp
EXT_KEY_ALIAS="external"
export INSTANCE_HOST_NAME=$(hostname)

if [[ -z $JAVA_OPTS ]]; then
    JAVA_OPTS="-Xms128m -Xmx512m"
fi

if [[ -z $CMD ]]; then
    CMD="java $JAVA_OPTS -jar $JAR_FILE"
fi

if [[ -z $APP_NAME ]]; then
    APP_NAME=`echo ${JAR_FILE/%.jar/} | sed 's/\///'`
fi
KEY_ALIAS="$APP_NAME"

if [[ $VAULT_TOKEN_FILE ]]; then
    VAULT_TOKEN=$(cat $VAULT_TOKEN_FILE)
fi

mkdir -p $JKS_DIR

# If VAULT_TOKEN is set then run under envconsul to provide secrets in env vars to the process
if [[ $VAULT_TOKEN && $VAULT_ADDR ]]; then

    until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/pki/ca/pem); do
        echo "Waiting for Vault to be available..."
        sleep 10
    done

    if curl -L -s --fail --insecure $VAULT_ADDR/v1/pki/ca/pem > /dev/null 2>&1; then
        #Install the Vault CA certificate
        mkdir /usr/local/share/ca-certificates/ascent
        echo "Downloading Vault CA certificate from $VAULT_ADDR/v1/pki/ca/pem"
        curl -L -s --insecure $VAULT_ADDR/v1/pki/ca/pem > /usr/local/share/ca-certificates/ascent/vault-ca.crt
        echo 'Updating CAs...'
        update-ca-certificates

        #Store the Vault CA cert in both the client truststore and the server truststore
        keytool -importcert -alias vault -keystore $CLIENT_TRUSTSTORE -noprompt -storepass $CLIENT_TRUSTSTORE_PASS -file /usr/local/share/ca-certificates/ascent/vault-ca.crt
        keytool -genkey -alias app -keystore $SERVER_TRUSTSTORE -storepass $SERVER_TRUSTSTORE_PASS -dname "CN=app.vetservices.gov, OU=OIT, O=VA, L=App, S=VA, C=US" -noprompt -keypass $SERVER_TRUSTSTORE_PASS
        keytool -delete -alias app -keystore $SERVER_TRUSTSTORE -storepass $SERVER_TRUSTSTORE_PASS
        keytool -importcert -alias vault -keystore $SERVER_TRUSTSTORE -noprompt -storepass $SERVER_TRUSTSTORE_PASS -file /usr/local/share/ca-certificates/ascent/vault-ca.crt

        #Create the server keystore
        echo "Creating server keystore for $APP_NAME..."
        keytool -genkey -alias app -keystore $SERVER_KEYSTORE -storepass $SERVER_KEYSTORE_PASS -dname "CN=app.vetservices.gov, OU=OIT, O=VA, L=App, S=VA, C=US" -noprompt -keypass $SERVER_KEYSTORE_PASS
        keytool -delete -alias app -keystore $SERVER_KEYSTORE -storepass $SERVER_KEYSTORE_PASS

        #Check for an external certificate to use, otherwise request one from Vault
        if curl -L -s --insecure -X GET -H "X-Vault-Token: $VAULT_TOKEN" --fail $VAULT_ADDR/v1/secret/$APP_NAME/ssl > /dev/null 2>&1; then
            echo "Retrieving existing server certificate from Vault"
            KEY_ALIAS="$EXT_KEY_ALIAS"
            #Creating template files to be populated by consul-template
            cat > '/tmp/app.crt.tpl' <<EOF
{{ with secret "secret/$APP_NAME/ssl" }}
{{ .Data.certificate }}
{{ .Data.issuing_ca }}{{ end }}
EOF
            cat > '/tmp/app.key.tpl' <<EOF
{{ with secret "secret/$APP_NAME/ssl" }}
{{ .Data.private_key }}{{ end }}
EOF
            # Use Consul Template to populate certificate files
            consul-template -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -once
            # Store the application server certificate in the server keystore
            echo "$SERVER_KEYSTORE_PASS" | openssl pkcs12 -export -out $TMPDIR/app.p12 -inkey $TMPDIR/app.key -in $TMPDIR/app.crt -password stdin -name $EXT_KEY_ALIAS
            keytool -importkeystore -srckeystore $TMPDIR/app.p12 -srcstoretype PKCS12 -destkeystore $SERVER_KEYSTORE -deststoretype JKS -deststorepass $SERVER_KEYSTORE_PASS -srcstorepass $SERVER_KEYSTORE_PASS -alias $EXT_KEY_ALIAS -destalias $EXT_KEY_ALIAS
        fi

        #Request a certificate for our application
        echo "Generating certificate from Vault..."
        #Creating template files to be populated by consul-template
        cat > '/tmp/app.crt.tpl' <<EOF
{{ with secret "pki/issue/vetservices" "common_name=*.internal.vetservices.gov" "alt_names=$INSTANCE_HOST_NAME,$APP_NAME.internal.vetservices.gov" }}
{{ .Data.certificate }}
{{ .Data.issuing_ca }}{{ end }}
EOF
        cat > '/tmp/app.key.tpl' <<EOF
{{ with secret "pki/issue/vetservices" "common_name=*.internal.vetservices.gov" "alt_names=$INSTANCE_HOST_NAME,$APP_NAME.internal.vetservices.gov" }}
{{ .Data.private_key }}{{ end }}
EOF

        # Use Consul Template to populate certificate files
        consul-template -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR" -once
        # Store the application server certificate in the server keystore
        echo "$SERVER_KEYSTORE_PASS" | openssl pkcs12 -export -out $TMPDIR/app.p12 -inkey $TMPDIR/app.key -in $TMPDIR/app.crt -password stdin -name $APP_NAME
        keytool -importkeystore -srckeystore $TMPDIR/app.p12 -srcstoretype PKCS12 -destkeystore $SERVER_KEYSTORE -deststoretype JKS -deststorepass $SERVER_KEYSTORE_PASS -srcstorepass $SERVER_KEYSTORE_PASS -alias $APP_NAME -destalias $APP_NAME
    fi
    
    #Build the client trusted keystore
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

    #Load our application certificate from Vault into this keystore
    echo "$CLIENT_KEYSTORE_PASS" | openssl pkcs12 -export -out $TMPDIR/app.p12 -inkey $TMPDIR/app.key -in $TMPDIR/app.crt -password stdin -name $APP_NAME
    keytool -importkeystore -srckeystore $TMPDIR/app.p12 -srcstoretype PKCS12 -destkeystore $CLIENT_KEYSTORE -deststoretype JKS -deststorepass $CLIENT_KEYSTORE_PASS -srcstorepass $CLIENT_KEYSTORE_PASS -alias $APP_NAME -destalias $APP_NAME

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

    #Determine which Key Alias to use. If IGNORE_EXT_CERT variable is set, we will ignore the existing
    #certificate from Vault and use the generated internal cert.
    if [[ $IGNORE_EXT_CERT ]]; then
        KEY_ALIAS="$APP_NAME"
    fi

    #Launch the app in another shell to keep secrets secure
    CMD="java $JAVA_OPTS -jar -Djavax.net.ssl.keyStore=$CLIENT_KEYSTORE -Djavax.net.ssl.keyStorePassword=$CLIENT_KEYSTORE_PASS -Djavax.net.ssl.trustStore=$CLIENT_TRUSTSTORE -Djavax.net.ssl.trustStorePassword=$CLIENT_TRUSTSTORE_PASS -Dserver.ssl.key-store=$SERVER_KEYSTORE -Dserver.ssl.key-store-password=$SERVER_KEYSTORE_PASS -Dserver.ssl.trust-store=$SERVER_TRUSTSTORE -Dserver.ssl.trust-store-password=$SERVER_TRUSTSTORE_PASS -Dserver.ssl.key-alias=$KEY_ALIAS -Dcom.netflix.eureka.shouldSSLConnectionsUseSystemSocketFactory=true $JAR_FILE"
    echo "Running Command: $CMD"
    envconsul -config="$ENVCONSUL_CONFIG" -vault-addr=$VAULT_ADDR $CMD
else
    $CMD
fi