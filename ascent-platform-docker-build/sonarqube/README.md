## Local testing for devs
For local development, we've setup a SonarQube 6.5 Docker Image, a container for configure the SonarQube server, and a PostgreSQL 9.6.5-Alpine Docker Image using Docker Compose. 

Follow through the link below for details
https://github.com/department-of-veterans-affairs/ascent-platform/wiki/Local-SonarQube-Docker-Setup

## Local testing with jenkins and sonarqube
To test automating the configuration of jenkins and sonarqube through an api, you could do it one of two ways
* Bring up a jenkins ec2 instance and tear down as necessary
* Bring up a jenkins container locally

### Testing against a Jenkins Ec2 instance
For testing against the jenkins ec2 instance and sonarqube, set the $JENKINS_URL variable in the docker-compose file to the jenkins url that you want and run `docker-compose up --build -d` in the ascent-platform-docker-build/jenkins-sonar-config directory, and it will configure the jenkins instance at $JENKINS_URL.

### Testing against a local Jenkins container
To test configuring/building of the Sonarqube-Jenkins stack locally, run test-start-sonar-jenkins.sh or test-stop-sonar-jenkins.sh to bring up/down the containers. The goal of the Jenkins container is to pretty much just simulate the Jenkins instance in the CI environmnet so that config changes through the api can be tested locally.
###### A caveat or two...
One caveat of testing against a local jenkins container is the links of the Jenkins plugins work intermittently because the plugins get updated or their locations change. If that happens, the build of the Jenkins container errors out with "Some plugins could not be downloaded". Usually, after waiting 5-10 minutes it works again, though.
