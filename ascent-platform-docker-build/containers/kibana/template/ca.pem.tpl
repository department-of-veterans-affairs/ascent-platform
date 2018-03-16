{{ with secret "pki/cert/ca" }}{{ .Data.certificate }}{{ end }}
