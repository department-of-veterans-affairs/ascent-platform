#!/bin/sh

CONSUL_TEMPLATE_CONFIG="/etc/rabbitmq/template/consul-template-config.hcl"

CMD="rabbitmq-server"

echo "--- polling to wait for vault"
until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
  echo "--trying again"
  sleep 5
done

consul-template -once -config="$CONSUL_TEMPLATE_CONFIG" -vault-addr="$VAULT_ADDR"

echo $RABBITMQ_ERLANG_COOKIE >> $HOME/.erlang.cookie
chmod 600 $HOME/.erlang.cookie

exec /etc/rabbitmq/mirror-queues.sh & 

$CMD "$@"
