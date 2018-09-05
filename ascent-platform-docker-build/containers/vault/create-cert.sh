#!/bin/bash -x

vault auth -address=http://localhost:8200 vaultroot
CERT_JSON=$(vault write -address=http://localhost:8200 -format=json pki/issue/vetservices common_name=app.internal.vetservices.gov)
echo $CERT_JSON | jq -r '.data.private_key' > developer.key
echo $CERT_JSON | jq -r '.data.certificate' > developer.pem
echo $CERT_JSON | jq -r '.data.issuing_ca' > ca.pem
echo "changeit" | openssl pkcs12 -export -out developer.p12 -inkey developer.key -in developer.pem -password stdin -name developer