import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

// Script to set the Sonar Server config in jenkins
// Can be used with the jenkins CLI by calling
// java -jar jenkins-cli.jar groovy sonar_global_config.groovy

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("hudson.plugins.sonar.SonarGlobalConfiguration")

def sinst = new SonarInstallation(
  "CI",
  "http://sonarqube:9000",
  "5.3",
  "**********",         // Overwritten by sed in set_token with real token once generated
  "",   // databaseUrl
  "",   // databaseLogin
  "",   // databasePassword
  "",   // mojoVersion
  "",   // additionalProperties
  new TriggersConfig(),
  "",   // sonarLogin
  "",   // sonarPassword
  "",   // additionalAnalysisProperties
)
desc.setInstallations(sinst)

desc.save()
