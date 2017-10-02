@Library('ascent') _

mavenPipeline {
    directory = 'ascent-platform-parent'
}

dockerPipeline {
    directory = 'ascent-platform-docker-build/ascent-base'
    imageName = 'ascent/ascent-base'
}

dockerPipeline {
    directory = 'ascent-platform-docker-build/filebeat'
    imageName = 'ascent/ascent-filebeat'
}

dockerPipeline {
    directory = 'ascent-platform-docker-build/logstash'
    imageName = 'ascent/ascent-logstash'
}

dockerPipeline {
    directory = 'ascent-platform-docker-build/elasticsearch'
    imageName = 'ascent/ascent-elasticsearch'
}

dockerPipeline {
    directory = 'ascent-platform-docker-build/kibana'
    imageName = 'ascent/ascent-kibana'
}