@Library('ascent') _

mavenPipeline {
    //Specify string of comma separated upstream projects that will
    //trigger this build if successful
    upstreamProjects = '../ascent-security/development'
}

dockerPipeline {
    //Specify string of comma separated upstream projects that will
    //trigger this build if successful
    upstreamProjects = '../ascent-security/development'
    dockerBuilds = [
        "ascent/ascent-vault":"ascent-platform-docker-build/containers/vault",
        "ascent/sonar":"ascent-platform-docker-build/containers/sonarqube",
        "ascent/jenkins":"ascent-platform-docker-build/containers/jenkins",
        "ascent/jenkins-sonar-config":"ascent-platform-docker-build/containers/jenkins-sonar-config",
    ]
}
