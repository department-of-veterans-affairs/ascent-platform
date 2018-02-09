
# Generates a private/public key so can authenticate Jenkins cli
# By default, remoting is set to false in jenkins and cannot authenticate
# cli through username/password

echo "--- polling to see if vault is up"
until $(curl -XGET --insecure --fail --output /dev/null --silent -H "X-Vault-Token: $VAULT_TOKEN" $VAULT_ADDR/v1/sys/health); do
   echo "-- trying again"
   sleep 5
done

echo "--- installing vault service"
curl -o vault.zip https://releases.hashicorp.com/vault/0.9.3/vault_0.9.3_linux_amd64.zip
unzip vault.zip -d /usr/bin

echo "--- vault is ready to go locally for upload"
echo "--- generating ssh key"
ssh-keygen -f /root/.ssh/id_rsa -t rsa -N ''

echo "--- ssh key generated. uploading to vault at secret/jenkins/ssh at public_key"
id_rsa=`cat /root/.ssh/id_rsa.pub`
echo "id_rsa.pub is $id_rsa"
echo "{\"public_key\":\"@/root/.ssh/id_rsa.pub\"}" >> public_key.json
vault write secret/jenkins/ssh public_key=@/root/.ssh/id_rsa.pub

echo "vault written! Contents are"
vault read secret/jenkins/ssh
