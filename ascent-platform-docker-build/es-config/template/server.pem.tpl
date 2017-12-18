{{ with secret "secret/elasticsearch" }}
{{ .Data.certificate }}{{ end }}
{{ with secret "secret/ca" }}
{{ .Data.certificate }}{{ end }}