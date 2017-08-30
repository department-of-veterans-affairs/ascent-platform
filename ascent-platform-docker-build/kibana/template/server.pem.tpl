{{ with secret "secret/kibana" }}
{{ .Data.certificate }}{{ end }}
{{ with secret "secret/ca" }}
{{ .Data.certificate }}{{ end }}