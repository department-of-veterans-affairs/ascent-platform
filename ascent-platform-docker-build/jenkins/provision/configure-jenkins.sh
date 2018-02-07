JENKINS_URL="http://localhost:8080"
JENKINS_HOME="/var/lib/jenkins"
CONFIG=/docker/config

#Wait till jenkins is available
printf "Waiting for Jenkins to start " 
COUNT=0
until $(curl --output /dev/null --silent --head -m 10 --fail $JENKINS_URL/login) || [ "$COUNT" == 20 ]; do
    COUNT=$(($COUNT+1))
    printf "."
    sleep 5
done

if [ "$COUNT" == 20 ]; then
    printf "\nJenkins failed to start"
    exit -1
fi

# Download the Jenkins CLI client
curl -L -o jenkins-cli.jar $JENKINS_URL/jnlpJars/jenkins-cli.jar

# Generate authentication token
ADMIN_PASSWORD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)

# Get the API token for CLI authentication
API_TOKEN=$(curl -u admin:$ADMIN_PASSWORD $JENKINS_URL/me/configure | sed -rn 's/.*id="apiToken"[^>]*value="([a-z0-9]+)".*/\1/p')

JENKINS_CLI="java -jar jenkins-cli.jar -s $JENKINS_URL -auth admin:$API_TOKEN"

# Loop through and install all plugins
PLUGINS=""
while read plugin; do
    PLUGINS="$PLUGINS $plugin"
done <plugins.txt
$JENKINS_CLI install-plugin $PLUGINS

# Stop the Jenkins server
sudo service jenkins stop
sleep 10

# Copy over our custom config files
sudo cp $CONFIG/* $JENKINS_HOME
sudo chown -R jenkins:jenkins $JENKINS_HOME

