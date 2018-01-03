@Library('ascent') _

mavenPipeline {
    //Specify string of comma separated upstream projects that will
    //trigger this build if successful
    upstreamProjects = '../ascent-security/development'
}

dockerPipeline {
    dockerBuilds = [
        "ascent/ascent-base": "ascent-platform-docker-build/ascent-base",
        "ascent/fluentd":"ascent-platform-docker-build/fluentd",
        "ascent/ascent-elasticsearch":"ascent-platform-docker-build/elasticsearch",
        "ascent/ascent-es-config":"ascent-platform-docker-build/es-config",
        "ascent/ascent-kibana":"ascent-platform-docker-build/kibana",
        "ascent/redis-sentinel":"ascent-platform-docker-build/redis-sentinel"
    ]
}