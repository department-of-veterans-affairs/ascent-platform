token=`curl -XPOST -u admin:admin localhost:9000/api/user_tokens/generate?name=jenkinsToken5 | python -c 'import sys, json; print json.load(sys.stdin)["token"]'`
echo "token is $token"
echo "{\"token\":\"$token\"}" >> token.json

echo "--- polling to see if vault is up"
until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
    echo "--trying again"
    sleep 5
done

echo "--- installing vault service"
curl -o vault.zip https://releases.hashicorp.com/vault/0.9.3/vault_0.9.3_linux_amd64.zip
unzip vault.zip -d /usr/bin


echo "--- uploading token to vault at secret/jenkins/sonar sonar.token"
vault write secret/jenkins/sonar @token.json

echo "--- vault written! Doing a read to confirm..."
vault read secret/jenkins/sonar

wait
