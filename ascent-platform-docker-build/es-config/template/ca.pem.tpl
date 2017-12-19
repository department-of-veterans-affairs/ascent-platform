{{ with secret "secret/ca" }}
{{ .Data.certificate }}{{ end }}