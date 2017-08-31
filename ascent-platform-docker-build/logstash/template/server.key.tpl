{{ with secret "secret/logstash" }}
{{ .Data.privatekey }}{{ end }}