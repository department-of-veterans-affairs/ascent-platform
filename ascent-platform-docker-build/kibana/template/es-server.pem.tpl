{{ with secret "secret/elasticsearch" }}{{ .Data.server_certificate }}{{ end }}
{{ with secret "pki/cert/ca" }}{{ .Data.certificate }}{{ end }}
