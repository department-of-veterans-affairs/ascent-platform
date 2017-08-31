{{ with secret "secret/logstash" }}
{{ .Data.certificate }}{{ end }}
{{ with secret "secret/ca" }}
{{ .Data.certificate }}{{ end }}