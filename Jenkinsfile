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
        "ascent/ascent-base": "ascent-platform-docker-build/containers/ascent-base",
        "ascent/fluentd":"ascent-platform-docker-build/containers/fluentd",
        "ascent/redis-sentinel":"ascent-platform-docker-build/containers/redis-sentinel",
        "ascent/rabbitmq":"ascent-platform-docker-build/containers/rabbitmq",
        "ascent/ascent-vault":"ascent-platform-docker-build/containers/vault",
        "ascent/sonar":"ascent-platform-docker-build/containers/sonarqube",
        "ascent/jenkins":"ascent-platform-docker-build/containers/jenkins",
        "ascent/jenkins-sonar-config":"ascent-platform-docker-build/containers/jenkins-sonar-config",
    ]
}
