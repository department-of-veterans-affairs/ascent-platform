{{ with secret "secret/elasticsearch" }}{{ with .Data.server_certificate }}{{ trimSpace . }}{{ end }}{{ end }}
{{ with secret "pki/cert/ca" }}{{ with .Data.certificate }}{{ trimSpace . }}{{ end }}{{ end }}
