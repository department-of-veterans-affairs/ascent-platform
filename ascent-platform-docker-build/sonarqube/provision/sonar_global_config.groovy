import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.*

def inst = Jenkins.getInstance()

def desc = inst.getDescriptor("hudson.plugins.sonar.SonarGlobalConfiguration")

def sinst = new SonarInstallation(
  "TEST2",
  "http://localhost:9000",
  "5.3",
  "34d4fffd37b6bf4d6e7b9dc27fcac9af1a469c4a",
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
