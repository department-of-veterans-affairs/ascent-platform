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
        "ascent/ascent-base": "ascent-platform-docker-build/ascent-base",
        "ascent/fluentd":"ascent-platform-docker-build/fluentd",
        "ascent/ascent-elasticsearch":"ascent-platform-docker-build/elasticsearch",
        "ascent/ascent-es-config":"ascent-platform-docker-build/es-config",
        "ascent/ascent-kibana":"ascent-platform-docker-build/kibana",
        "ascent/redis-sentinel":"ascent-platform-docker-build/redis-sentinel",
        "ascent/rabbitmq":"ascent-platform-docker-build/rabbitmq",
        "ascent/ascent-vault":"ascent-platform-docker-build/vault",
        "ascent/sonar":"ascent-platform-docker-build/sonarqube",
        "ascent/sonar-db":"ascent-platform-docker-build/sonarqube/database",
        "ascent/jenkins":"ascent-platform-docker-build/jenkins",
        "ascent/jenkins-sonar-config":"ascent-platform-docker-build/jenkins-sonar-config",
    ]
}