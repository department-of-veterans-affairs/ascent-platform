{{ with secret "secret/logstash" }}
{{ .Data.certificate }}{{ end }}