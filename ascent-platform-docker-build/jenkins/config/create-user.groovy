import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

// Create Jenkins user and add Administer privilege
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("jenkins", "jenkins")
instance.setSecurityRealm(hudsonRealm)
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "jenkins")
instance.setAuthorizationStrategy(strategy)
instance.save()
