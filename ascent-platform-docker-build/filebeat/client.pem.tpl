{{ with secret "secret/filebeat" }}
{{ .Data.certificate }}{{ end }}