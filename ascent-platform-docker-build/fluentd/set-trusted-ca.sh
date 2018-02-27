#!/bin/bash

#Install the Vault CA certificate
mkdir /usr/local/share/ca-certificates/ascent

echo "------- setting trusted ca..."
echo "secure-connect: $SECURE_CONNECT"
#Check if VAULT_TOKEN set and SECURE_CONNECT set to true
echo "Secure: $SECURE_CONNECT"
echo "Downloading Vault CA certificate from $VAULT_ADDR/v1/pki/ca/pem"
curl -L -s --insecure $VAULT_ADDR/v1/pki/ca/pem > /usr/local/share/ca-certificates/ascent/vault-ca.crt
update-ca-certificates

