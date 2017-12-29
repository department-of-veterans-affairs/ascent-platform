---
server.name: ${HOSTNAME}
server.host: "0.0.0.0"

{{ with secret "secret/elasticsearch/kibana" }}
# Elastic Search Client
elasticsearch.url: https://elastic.internal.vets-api.gov:9200
{{ if .Data.username }}
elasticsearch.username: {{ .Data.username }}
{{ else }}
elasticsearch.username: kibana
{{ end }}
{{ if .Data.password }}
elasticsearch.password: {{ .Data.password }}
{{ else }}
elasticsearch.password: changeme
{{ end }}
{{ end }}
{{ with secret "secret/elasticsearch "}}
xpack.monitoring.ui.container.elasticsearch.enabled: true
elasticsearch.ssl.certificateAuthorities: [ "/usr/share/kibana/config/ca.pem" ]
elasticsearch.ssl.certificate: /usr/share/kibana/config/es-server.pem
elasticsearch.ssl.key: /usr/share/kibana/config/es-server.key
elasticsearch.ssl.keyPassphrase: {{ .Data.privatekey_password }}
elasticsearch.ssl.verificationMode: full
{{ end }}

{{ with secret "secret/kibana" }}
#SSL Settings
server.ssl.enabled: true
server.ssl.key: /usr/share/kibana/config/server.key
server.ssl.keyPassphrase: {{ .Data.privatekey_password }}
server.ssl.certificate: /usr/share/kibana/config/server.pem
{{ end }}
