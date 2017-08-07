# Centralize Logging for Ascent Platform

Implementation of the [Ascent Logging Design](https://github.com/department-of-veterans-affairs/ascent/wiki/Ascent-Logging:-Search-Visualize-and-Monitor) and [Platform Logging Design](https://github.com/department-of-veterans-affairs/ascent/wiki/Platform-Logging).

## Deployment

### Tool Needed
The following tools will need to be installed before you can successfully deploy and use the Ascent Platform Logging solution.
* [Docker](https://store.docker.com/search?type=edition&offering=community)

### Local Deployment
To deploy our logging stack locally, run the following command in your terminal:

```bash
docker-compose up --build -d
```

### Swarm Deployment
To deploy our logging stack to a Docker Swarm, run the following command in your terminal:

```bash
docker stack deploy -c docker-compose.yml logging
```
## Usage
The Kibana UI should be available at [http://localhost:5601](http://localhost:5601). The default credentials are:<br/>
_Username:_ elastic<br/>
_Password:_ changeme