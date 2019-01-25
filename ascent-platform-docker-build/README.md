# Ascent Platform Docker Build

This project includes docker build and deployment for the Ascent Platform

## containers
Holds local containers developers need to run with platform services. The only service that developers will need to start manually will be **sonarqube**. Vault is automatically brought up with every service locally when `docker-compose up` is run in that project (if needed). See **Local Deployment** for list of containers.


## swarm
Contains docker-compose files and scripts necessary for deploying a stack to a swarm.

## Deployment of Containers

### Tool Needed
The following tools will need to be installed before you can successfully deploy and use the Ascent Platform Logging solution.
* [Docker](https://store.docker.com/search?type=edition&offering=community)

### Local Deployment
For each container that you want to bring up, you'll do the following:

```
cd containers/[CONTAINER_NAME]

# This makes output come through your console....
docker-compose up --build

# This runs the container in the background...
docker-compose up --build -d
```

To bring down the container...

```
cd containers/[CONTAINER_NAME]

# This just stops/deletes containers
docker-compose down

# This deletes containers and associated images/layered images
docker-compose down --rmi all
```

- **Sonarqube** - The only container that developers will need to manually start so that code can be scanned.

- **jenkins-jmeter-localint** -  a container that brings up a jenkins instance with jmeter plugins to execute jmeter tests.

- **jenkins-sonar-config** - a jenkins and sonar instance where sonarqube and jenkins are configured to run together with proper settings.

- **jenkins** - a jenkins container used for testing configurations

- **elasticsearch/kibana** - containers that can be run with application services if one wanted to even better simulate the higher environment. Not recommended for developer use because it's a drain on local resources and doesn't work reliably for local development.

- **vault** - container that simulates the vaults in the higher environments for local development. Does not need to be brought up manually, as every service has a local vault image/container defined already.

### Swarm Deployment
The **swarm** directory contains scripts that the ops team can use to test stacks in a local swarm environment.


## Custom Configurations
The following environment variables can be set when the containers are launched in order to customize their configuration:
* ElasticSearch
    * __VAULT_TOKEN__ - The token to authenticate to vault with. When this is specified, ElasticSearch will configure itself to use SSL authentication with Logstash and Kibana. It will pull its SSL information from Vault using the VAULT_ADDR env variable value.
    * __VAULT_ADDR__ - The address of the vault server. Defaults to "https://vault:8200"
* Kibana
    * __VAULT_TOKEN__ - The token to authenticate to vault with. When this is specified, Kibana will configure itself to use SSL authentication with ElasticSearch. It will pull its SSL information from Vault using the VAULT_ADDR env variable value.
    * __VAULT_ADDR__ - The address of the vault server. Defaults to "https://vault:8200"
