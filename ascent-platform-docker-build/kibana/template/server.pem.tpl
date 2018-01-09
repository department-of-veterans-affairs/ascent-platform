{{ with secret "secret/kibana" }}{{ .Data.certificate }}{{ end }}
{{ with secret "pki/cert/ca" }}{{ .Data.certificate }}{{ end }}
