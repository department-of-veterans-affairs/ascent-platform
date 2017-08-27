{{ with secret "secret/filebeat" }}
{{ .Data.certificate }}{{ end }}
{{ with secret "secret/ca" }}
{{ .Data.certificate }}{{ end }}