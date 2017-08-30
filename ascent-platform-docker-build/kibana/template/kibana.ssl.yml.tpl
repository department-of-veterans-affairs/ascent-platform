---
server.name: kibana
server.host: "0"

{{ with secret "secret/kibana" }}
# Elastic Search Client
elasticsearch.url: https://elasticsearch:9200
{{ if .Data.es_user }}
elasticsearch.username: {{ .Data.es_user }}
{{ else }}
elasticsearch.username: elastic
{{ end }}
{{ if .Data.es_password }}
elasticsearch.password: {{ .Data.es_password }}
{{ else }}
elasticsearch.password: changeme
{{ end }}
xpack.monitoring.ui.container.elasticsearch.enabled: true
elasticsearch.ssl.certificateAuthorities: [ "/usr/share/kibana/config/ca.pem" ]
elasticsearch.ssl.certificate: /usr/share/kibana/config/server.pem
elasticsearch.ssl.key: /usr/share/kibana/config/server.key
elasticsearch.ssl.keyPassphrase: {{ .Data.privatekey_password }}
elasticsearch.ssl.verificationMode: full

#SSL Settings
server.ssl.enabled: true
server.ssl.key: /usr/share/kibana/config/server.key
server.ssl.keyPassphrase: {{ .Data.privatekey_password }}
server.ssl.certificate: /usr/share/kibana/config/server.pem
{{ end }}