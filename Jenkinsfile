@Library('ascent') _

mavenPipeline {
    directory = 'ascent-platform-parent'
}

mavenPipeline {
    directory = 'ascent-platform-starters'
}

dockerPipeline {
    dockerBuilds = [
        "ascent/ascent-base": "ascent-platform-docker-build/ascent-base",
        "ascent/fluentd":"ascent-platform-docker-build/fluentd",
        "ascent/ascent-elasticsearch":"ascent-platform-docker-build/elasticsearch",
        "ascent/ascent-es-config":"ascent-platform-docker-build/es-config",
        "ascent/ascent-kibana":"ascent-platform-docker-build/kibana"
    ]
}