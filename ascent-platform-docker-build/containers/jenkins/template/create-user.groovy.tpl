import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

// Create Jenkins user and add Administer privilege
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("{{ with secret "secret/jenkins" }}{{ .Data.username }}", "{{ with .Data.password }}{{ trimSpace . }}{{ end }}{{ end }}")
instance.setSecurityRealm(hudsonRealm)
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "jenkins")
instance.setAuthorizationStrategy(strategy)
instance.save()
