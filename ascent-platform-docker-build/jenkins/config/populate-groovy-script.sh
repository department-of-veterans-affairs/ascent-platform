#!/bin/bash

CONSUL_CONFIG=/usr/share/jenkins/template/consul-template-config.hcl
VAULT_ADDR=http://vault:8200
VAULT_TOKEN=vaultroot
consul-template -once -config $CONSUL_CONFIG -vault-addr=$VAULT_ADDR -vault-token=$VAULT_TOKEN

