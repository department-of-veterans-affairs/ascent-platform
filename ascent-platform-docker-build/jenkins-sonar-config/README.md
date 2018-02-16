# Configuration
**LANGUAGE** - The language to set the sonar profile to. Usually set to 'java'

**PROFILE_NAME** - The name of the default profile to set. Set it to 'ASCENT'

**VAULT_TOKEN, VAULT_ADDR** - The vault token and vault address. Sonar and Jenkins **will not** authenticate if these are not set

**JENKINS_URL** - The url to the jenkins instance to configure

**SONAR_URL** - The url to the sonar instance to configure

**CONFIGURE_JENKINS** - A flag for whether or not jenkins should be configured. Set it to 'true' or 'false'

# To Run
Start vault first `docker-compose -f docker-compose.vault.yml -f docker-compose -f docker-compose.vault.override.yml up --build -d` from ascent-platform-docker-build directory.

### Configure Jenkins and SonarQube already up
`docker-compose up --build -d` in the jenkins-sonar-config directory

### Configuring SonarQube only (set to false for local devs)
Set **CONFIGURE_JENKINS** environment variable to false, then run `docker-compose up --build -d` in the jenkins-sonar-config directory

### Build and Configure Jenkins and SonarQube at once
`cd ascent-platform-docker-build/sonarqube`
`./test-start-sonar-jenkins.sh`

# Behavior
### Jenkins Credentials
**Create** - Detects if making a jenkins credential for the first time. 
**Edit**   - Detects if a jenkins credential with an id already exists. If it does, it will edit the credential already in existence
